package com.aistudio.bitcoinledger.btcxyz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.bitcoinledger.btcxyz.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.SecureRandom
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
    startHourlyMonitor()
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  private fun generateTxHash(): String {
    val chars = "0123456789abcdef"
    val random = SecureRandom()
    return buildString(64) {
      repeat(64) {
        append(chars[random.nextInt(chars.length)])
      }
    }
  }

  fun fetchPrices() {
    viewModelScope.launch {
      try {
        val response = CoinGeckoApi.instance.getBitcoinPrice()
        response.bitcoin?.let { bp ->
          bp.usd?.let { _bitcoinPriceUsd.value = it }
          bp.brl?.let { _bitcoinPriceBrl.value = it }
        }
      } catch (_: Exception) {
        // Keep cached/fallback prices when offline
      }
    }
  }

  private fun startHourlyMonitor() {
    viewModelScope.launch {
      while (true) {
        delay(60_000L)
        val acc = dao.getAccount()
        if (acc != null) {
          processHourlyRewardsAndPendingTransfers(acc)
        }
      }
    }
  }

  private fun checkAccountAndInit() {
    viewModelScope.launch {
      val acc = dao.getAccount()
      if (acc == null) {
        _appState.value = AppState.Onboarding
      } else {
        processHourlyRewardsAndPendingTransfers(acc)
        _appState.value = AppState.Locked
      }
    }
  }

  fun createAccount(name: String, cpf: String, keyAddress: String, pin: String) {
    viewModelScope.launch {
      val finalKey = keyAddress.trim().ifBlank { "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh" }
      val initialAccount = WalletAccountEntity(
        id = 1,
        name = name.trim(),
        cpf = cpf.trim(),
        keyAddress = finalKey,
        hardwareLocation = "Rua Leocádia, 856 - Realengo",
        pin = pin,
        balanceBtc = 0.45200000,
        lastHourlyUpdate = System.currentTimeMillis()
      )
      dao.insertAccount(initialAccount)
      dao.insertTransaction(
        TransactionEntity(
          type = "RECEIVE",
          sender = "Custódia Hardware Ledger",
          recipient = name.trim(),
          institution = "Hardware Ledger (SegWit)",
          amountBtc = 0.45200000,
          amountUsd = 0.45200000 * _bitcoinPriceUsd.value,
          amountBrl = 0.45200000 * _bitcoinPriceBrl.value,
          feeBtc = 0.000014,
          txHash = generateTxHash(),
          status = "CONCLUDED",
          timestamp = System.currentTimeMillis(),
          note = "Inicialização de carteira conectada ao Hardware Ledger"
        )
      )
      _appState.value = AppState.Unlocked
    }
  }

  fun verifyPin(enteredPin: String): Boolean {
    val acc = account.value ?: return false
    return if (enteredPin == acc.pin) {
      _appState.value = AppState.Unlocked
      viewModelScope.launch { processHourlyRewardsAndPendingTransfers(acc) }
      true
    } else {
      _toastMessage.value = "PIN incorreto. Tente novamente."
      false
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

    if (elapsedMillis >= oneHourMillis) {
      val hoursElapsed = elapsedMillis / oneHourMillis
      val rewardBtc = hoursElapsed * 0.00005
      currentBalance += rewardBtc
      updatedTimestamp += (hoursElapsed * oneHourMillis)

      dao.insertTransaction(
        TransactionEntity(
          type = "HOURLY_REWARD",
          sender = "Hardware Ledger Node",
          recipient = acc.name,
          institution = "Bitcoin Network (Pool)",
          amountBtc = rewardBtc,
          amountUsd = rewardBtc * _bitcoinPriceUsd.value,
          amountBrl = rewardBtc * _bitcoinPriceBrl.value,
          feeBtc = 0.0,
          txHash = generateTxHash(),
          status = "CONCLUDED",
          timestamp = now,
          note = "Rendimento automático a cada 1 hora ($hoursElapsed h creditadas)"
        )
      )
    }

    dao.updateBalanceAndTimestamp(currentBalance, updatedTimestamp)

    val pendingTxs = dao.getPendingTransactions()
    for (tx in pendingTxs) {
      val txElapsed = now - tx.timestamp
      if (txElapsed >= oneHourMillis) {
        val originLabel = tx.institution.ifBlank { "Binance / Mercado Pago" }
        dao.updateTransactionStatus(
          tx.id,
          "FAILED_BANK_RETENTION",
          "Falha na liquidação ($originLabel): O banco emissor reteu o dinheiro enviado por política de prevenção e análise de conformidade."
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

      val newBalance = acc.balanceBtc - amountBtc
      dao.updateBalanceAndTimestamp(newBalance, acc.lastHourlyUpdate)

      val usdValue = amountBtc * _bitcoinPriceUsd.value
      val brlValue = amountBtc * _bitcoinPriceBrl.value
      val now = System.currentTimeMillis()

      dao.insertTransaction(
        TransactionEntity(
          type = "SEND",
          sender = "${acc.name} ($originApp)",
          recipient = recipientAddress,
          institution = originApp,
          amountBtc = amountBtc,
          amountUsd = usdValue,
          amountBrl = brlValue,
          feeBtc = 0.000018,
          txHash = generateTxHash(),
          status = "PENDING",
          timestamp = now,
          note = "Enviado via $originApp - Aguardando compensação bancária (prazo ~1h)..."
        )
      )
      _toastMessage.value = "Transferência enviada. Processando via $originApp..."
    }
  }

  fun verifyDevPasscode(code: String): Boolean {
    return if (code == "6565") {
      _isDevModeUnlocked.value = true
      _toastMessage.value = "Acesso autorizado."
      true
    } else {
      _toastMessage.value = "Senha incorreta."
      false
    }
  }

  fun devAddBtc(amount: Double) {
    viewModelScope.launch {
      if (amount <= 0) {
        _toastMessage.value = "Informe um valor maior que zero."
        return@launch
      }
      val acc = account.value ?: return@launch
      val newBalance = acc.balanceBtc + amount
      dao.updateBalanceAndTimestamp(newBalance, acc.lastHourlyUpdate)
      dao.insertTransaction(
        TransactionEntity(
          type = "RECEIVE",
          sender = "Hardware Ledger Deposit",
          recipient = acc.name,
          institution = "Bitcoin Network (SegWit)",
          amountBtc = amount,
          amountUsd = amount * _bitcoinPriceUsd.value,
          amountBrl = amount * _bitcoinPriceBrl.value,
          feeBtc = 0.000012,
          txHash = generateTxHash(),
          status = "CONCLUDED",
          timestamp = System.currentTimeMillis(),
          note = "Crédito recebido em carteira Ledger"
        )
      )
      _toastMessage.value = "+${String.format("%.6f", amount)} BTC adicionados ao saldo."
    }
  }

  fun devSubtractBtc(amount: Double) {
    viewModelScope.launch {
      if (amount <= 0) {
        _toastMessage.value = "Informe um valor maior que zero."
        return@launch
      }
      val acc = account.value ?: return@launch
      val deducted = minOf(acc.balanceBtc, amount)
      val newBalance = (acc.balanceBtc - deducted).coerceAtLeast(0.0)
      dao.updateBalanceAndTimestamp(newBalance, acc.lastHourlyUpdate)
      if (deducted > 0) {
        dao.insertTransaction(
          TransactionEntity(
            type = "SEND",
            sender = acc.name,
            recipient = "bc1qledgervault99a8x4k2m0p",
            institution = "Hardware Ledger Vault",
            amountBtc = deducted,
            amountUsd = deducted * _bitcoinPriceUsd.value,
            amountBrl = deducted * _bitcoinPriceBrl.value,
            feeBtc = 0.000012,
            txHash = generateTxHash(),
            status = "CONCLUDED",
            timestamp = System.currentTimeMillis(),
            note = "Débito processado na carteira"
          )
        )
      }
      _toastMessage.value = "-${String.format("%.6f", deducted)} BTC reduzidos do saldo."
    }
  }

  fun devUpdateHardwareLocation(location: String) {
    viewModelScope.launch {
      val clean = location.trim().ifBlank { "Rua Leocádia, 856 - Realengo" }
      dao.updateHardwareLocation(clean)
      _toastMessage.value = "Localização do hardware atualizada."
    }
  }

  fun devFastForwardHour() {
    viewModelScope.launch {
      val acc = account.value ?: return@launch
      val forcedTime = acc.lastHourlyUpdate - TimeUnit.HOURS.toMillis(1)
      dao.updateBalanceAndTimestamp(acc.balanceBtc, forcedTime)
      processHourlyRewardsAndPendingTransfers(dao.getAccount()!!)
      _toastMessage.value = "Ciclo de 1 hora processado."
    }
  }

  fun devAddCustomStatement(
    type: String,
    sender: String,
    recipient: String,
    institution: String,
    amountBtc: Double,
    status: String,
    note: String,
    applyToBalance: Boolean
  ) {
    viewModelScope.launch {
      val acc = account.value ?: return@launch
      if (amountBtc <= 0) {
        _toastMessage.value = "Valor em BTC inválido."
        return@launch
      }

      if (applyToBalance) {
        val updatedBalance = if (type == "SEND") {
          (acc.balanceBtc - amountBtc).coerceAtLeast(0.0)
        } else {
          acc.balanceBtc + amountBtc
        }
        dao.updateBalanceAndTimestamp(updatedBalance, acc.lastHourlyUpdate)
      }

      dao.insertTransaction(
        TransactionEntity(
          type = type,
          sender = sender.ifBlank { if (type == "SEND") acc.name else "Carteira Externa" },
          recipient = recipient.ifBlank { if (type == "SEND") "bc1qexterna..." else acc.name },
          institution = institution.ifBlank { "Bitcoin Network (SegWit)" },
          amountBtc = amountBtc,
          amountUsd = amountBtc * _bitcoinPriceUsd.value,
          amountBrl = amountBtc * _bitcoinPriceBrl.value,
          feeBtc = 0.000015,
          txHash = generateTxHash(),
          status = status,
          timestamp = System.currentTimeMillis(),
          note = note
        )
      )
      _toastMessage.value = "Lançamento adicionado ao extrato."
    }
  }

  fun devUpdateTransaction(
    original: TransactionEntity,
    newType: String,
    newSender: String,
    newRecipient: String,
    newInstitution: String,
    newAmountBtc: Double,
    newStatus: String,
    newNote: String,
    newTimestamp: Long
  ) {
    viewModelScope.launch {
      val updated = original.copy(
        type = newType,
        sender = newSender,
        recipient = newRecipient,
        institution = newInstitution,
        amountBtc = newAmountBtc,
        amountUsd = newAmountBtc * _bitcoinPriceUsd.value,
        amountBrl = newAmountBtc * _bitcoinPriceBrl.value,
        txHash = original.txHash.ifBlank { generateTxHash() },
        status = newStatus,
        note = newNote,
        timestamp = newTimestamp
      )
      dao.updateTransaction(updated)
      _toastMessage.value = "Comprovante do extrato atualizado."
    }
  }

  fun devDeleteTransaction(tx: TransactionEntity) {
    viewModelScope.launch {
      dao.deleteTransaction(tx)
      _toastMessage.value = "Registro removido do extrato."
    }
  }
}
