package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed class AppState {
  object Loading : AppState()
  object Onboarding : AppState()
  object Locked : AppState()
  object Unlocked : AppState()
}

class WalletViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = WalletDatabase.getDatabase(application).walletDao()

  private val _appState = MutableStateFlow<AppState>(AppState.Loading)
  val appState: StateFlow<AppState> = _appState.asStateFlow()

  val account: StateFlow<WalletAccountEntity?> = dao.observeAccount()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val transactions: StateFlow<List<TransactionEntity>> = dao.observeTransactions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _bitcoinPriceUsd = MutableStateFlow(91500.0)
  val bitcoinPriceUsd: StateFlow<Double> = _bitcoinPriceUsd.asStateFlow()

  private val _bitcoinPriceBrl = MutableStateFlow(542000.0)
  val bitcoinPriceBrl: StateFlow<Double> = _bitcoinPriceBrl.asStateFlow()

  private val _isDevModeUnlocked = MutableStateFlow(false)
  val isDevModeUnlocked: StateFlow<Boolean> = _isDevModeUnlocked.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  init {
    checkAccountAndInit()
    fetchPrices()
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  fun fetchPrices() {
    viewModelScope.launch {
      try {
        val response = CoinGeckoApi.instance.getBitcoinPrice()
        response.bitcoin?.let { bp ->
          bp.usd?.let { _bitcoinPriceUsd.value = it }
          bp.brl?.let { _bitcoinPriceBrl.value = it }
        }
      } catch (e: Exception) {
        // Keep fallback prices
      }
    }
  }

  private fun checkAccountAndInit() {
    viewModelScope.launch {
      val acc = dao.getAccount()
      if (acc == null) {
        _appState.value = AppState.Onboarding
      } else {
        // Check hourly rewards and pending transactions on start
        processHourlyRewardsAndPendingTransfers(acc)
        _appState.value = AppState.Locked
      }
    }
  }

  fun createAccount(name: String, pin: String) {
    viewModelScope.launch {
      val initialAccount = WalletAccountEntity(
        id = 1,
        name = name,
        pin = pin,
        balanceBtc = 0.45200000, // Initial generous Ledger BTC balance
        lastHourlyUpdate = System.currentTimeMillis()
      )
      dao.insertAccount(initialAccount)
      dao.insertTransaction(
        TransactionEntity(
          type = "RECEIVE",
          recipient = "Ledger Hardware Genesis",
          amountBtc = 0.45200000,
          amountUsd = 0.45200000 * _bitcoinPriceUsd.value,
          amountBrl = 0.45200000 * _bitcoinPriceBrl.value,
          status = "CONCLUDED",
          timestamp = System.currentTimeMillis(),
          note = "Genesis Ledger Wallet Initialization"
        )
      )
      _appState.value = AppState.Unlocked
    }
  }

  fun verifyPin(enteredPin: String): Boolean {
    val acc = account.value ?: return false
    if (enteredPin == acc.pin) {
      _appState.value = AppState.Unlocked
      viewModelScope.launch { processHourlyRewardsAndPendingTransfers(acc) }
      return true
    } else {
      _toastMessage.value = "PIN incorreto. Tente novamente."
      return false
    }
  }

  fun lockApp() {
    _appState.value = AppState.Locked
  }

  private suspend fun processHourlyRewardsAndPendingTransfers(acc: WalletAccountEntity) {
    val now = System.currentTimeMillis()
    val elapsedMillis = now - acc.lastHourlyUpdate
    val oneHourMillis = TimeUnit.HOURS.toMillis(1)

    var currentBalance = acc.balanceBtc
    var updatedTimestamp = acc.lastHourlyUpdate

    // 1. Process hourly accumulation (+0.00005 BTC per elapsed hour)
    if (elapsedMillis >= oneHourMillis) {
      val hoursElapsed = elapsedMillis / oneHourMillis
      val rewardBtc = hoursElapsed * 0.00005
      currentBalance += rewardBtc
      updatedTimestamp += (hoursElapsed * oneHourMillis)

      dao.insertTransaction(
        TransactionEntity(
          type = "HOURLY_REWARD",
          recipient = "Ledger Staking / Mining Node",
          amountBtc = rewardBtc,
          amountUsd = rewardBtc * _bitcoinPriceUsd.value,
          amountBrl = rewardBtc * _bitcoinPriceBrl.value,
          status = "CONCLUDED",
          timestamp = now,
          note = "Rendimento automático a cada 1 hora ($hoursElapsed h creditadas)"
        )
      )
    }

    dao.updateBalanceAndTimestamp(currentBalance, updatedTimestamp)

    // 2. Check pending transfers (if 1 hour elapsed since sending, fail them due to bank retention)
    val pendingTxs = dao.getPendingTransactions()
    for (tx in pendingTxs) {
      val txElapsed = now - tx.timestamp
      if (txElapsed >= oneHourMillis) {
        dao.updateTransactionStatus(
          tx.id,
          "FAILED_BANK_RETENTION",
          "Transferência recusada: O banco emissor (ex: Binance / Mercado Pago) reteu o saldo por política de conformidade e prevenção à fraude."
        )
      }
    }
  }

  fun sendBitcoin(recipientAddress: String, amountBtc: Double, originApp: String) {
    viewModelScope.launch {
      val acc = account.value ?: return@launch
      if (amountBtc <= 0) {
        _toastMessage.value = "Digite um valor válido para envio."
        return@launch
      }
      if (acc.balanceBtc < amountBtc) {
        _toastMessage.value = "Saldo insuficiente na carteira Ledger."
        return@launch
      }

      // Deduct balance or hold
      val newBalance = acc.balanceBtc - amountBtc
      dao.updateBalanceAndTimestamp(newBalance, acc.lastHourlyUpdate)

      val usdValue = amountBtc * _bitcoinPriceUsd.value
      val brlValue = amountBtc * _bitcoinPriceBrl.value
      val now = System.currentTimeMillis()

      dao.insertTransaction(
        TransactionEntity(
          type = "SEND",
          recipient = recipientAddress,
          amountBtc = amountBtc,
          amountUsd = usdValue,
          amountBrl = brlValue,
          status = "PENDING",
          timestamp = now,
          note = "Enviado via $originApp - Aguardando compensação do banco..."
        )
      )
      _toastMessage.value = "Transferência enviada. Processando via $originApp..."
    }
  }

  fun verifyDevPasscode(code: String): Boolean {
    if (code == "6565") {
      _isDevModeUnlocked.value = true
      _toastMessage.value = "Modo Desenvolvedor Ativado com Sucesso!"
      return true
    } else {
      _toastMessage.value = "Senha secreta incorreta."
      return false
    }
  }

  // Developer functions
  fun devAddBtc(amount: Double) {
    viewModelScope.launch {
      val acc = account.value ?: return@launch
      val newBalance = acc.balanceBtc + amount
      dao.updateBalanceAndTimestamp(newBalance, acc.lastHourlyUpdate)
      dao.insertTransaction(
        TransactionEntity(
          type = "RECEIVE",
          recipient = "Dev Injection Ledger",
          amountBtc = amount,
          amountUsd = amount * _bitcoinPriceUsd.value,
          amountBrl = amount * _bitcoinPriceBrl.value,
          status = "CONCLUDED",
          timestamp = System.currentTimeMillis(),
          note = "Adicionado via Painel de Desenvolvedor"
        )
      )
      _toastMessage.value = "+$amount BTC adicionados com sucesso!"
    }
  }

  fun devFastForwardHour() {
    viewModelScope.launch {
      val acc = account.value ?: return@launch
      // Force lastHourlyUpdate back by 1 hour
      val forcedTime = acc.lastHourlyUpdate - TimeUnit.HOURS.toMillis(1)
      dao.updateBalanceAndTimestamp(acc.balanceBtc, forcedTime)
      processHourlyRewardsAndPendingTransfers(dao.getAccount()!!)
      _toastMessage.value = "Avançado 1 hora (Rendimentos e retenção de banco processados)."
    }
  }
}
