package com.aistudio.bitcoinledger.btcxyz

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.bitcoinledger.btcxyz.ui.screens.*
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
import com.aistudio.bitcoinledger.btcxyz.viewmodel.AppState
import com.aistudio.bitcoinledger.btcxyz.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: WalletViewModel = viewModel()
        val appState by viewModel.appState.collectAsState()
        val account by viewModel.account.collectAsState()
        val transactions by viewModel.transactions.collectAsState()
        val priceUsd by viewModel.bitcoinPriceUsd.collectAsState()
        val priceBrl by viewModel.bitcoinPriceBrl.collectAsState()
        val isDevUnlocked by viewModel.isDevModeUnlocked.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()

        LaunchedEffect(toastMessage) {
          toastMessage?.let {
            Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
            viewModel.clearToast()
          }
        }

        var currentScreen by remember { mutableStateOf("home") }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = LedgerDarkBackground
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (appState) {
              is AppState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  CircularProgressIndicator(color = LedgerGreen)
                }
              }
              is AppState.Onboarding -> {
                OnboardingScreen(
                  onAccountCreated = { name, cpf, keyAddress, pin ->
                    viewModel.createAccount(name, cpf, keyAddress, pin)
                    currentScreen = "home"
                  }
                )
              }
              is AppState.Locked -> {
                LockScreen(
                  walletName = account?.name ?: "Bitcoin Ledger",
                  onUnlock = { pin ->
                    viewModel.verifyPin(pin)
                  }
                )
              }
              is AppState.Unlocked -> {
                if (account != null) {
                  when (currentScreen) {
                    "home" -> {
                      HomeScreen(
                        account = account!!,
                        transactions = transactions,
                        priceUsd = priceUsd,
                        priceBrl = priceBrl,
                        onNavigateSend = { currentScreen = "send" },
                        onNavigateReceive = { currentScreen = "receive" },
                        onNavigateTransactions = { currentScreen = "transactions" },
                        onNavigateSettings = { currentScreen = "settings" }
                      )
                    }
                    "send" -> {
                      BackHandler { currentScreen = "home" }
                      SendScreen(
                        balanceBtc = account!!.balanceBtc,
                        priceUsd = priceUsd,
                        priceBrl = priceBrl,
                        onBack = { currentScreen = "home" },
                        onSend = { recipient, amount, origin ->
                          viewModel.sendBitcoin(recipient, amount, origin)
                          currentScreen = "home"
                        }
                      )
                    }
                    "receive" -> {
                      BackHandler { currentScreen = "home" }
                      ReceiveScreen(
                        keyAddress = account!!.keyAddress,
                        hardwareLocation = account!!.hardwareLocation,
                        onBack = { currentScreen = "home" }
                      )
                    }
                    "transactions" -> {
                      BackHandler { currentScreen = "home" }
                      TransactionsScreen(transactions = transactions, onBack = { currentScreen = "home" })
                    }
                    "settings" -> {
                      BackHandler { currentScreen = "home" }
                      SettingsScreen(
                        account = account!!,
                        isDevUnlocked = isDevUnlocked,
                        onBack = { currentScreen = "home" },
                        onLockApp = { viewModel.lockApp() },
                        onNavigateTransactions = { currentScreen = "transactions" },
                        onUnlockDev = { code -> viewModel.verifyDevPasscode(code) },
                        onOpenDevPanel = { currentScreen = "dev_panel" }
                      )
                    }
                    "dev_panel" -> {
                      BackHandler { currentScreen = "settings" }
                      DevPanelScreen(
                        balanceBtc = account!!.balanceBtc,
                        hardwareLocation = account!!.hardwareLocation,
                        transactions = transactions,
                        onBack = { currentScreen = "settings" },
                        onAddBtc = { amount -> viewModel.devAddBtc(amount) },
                        onSubtractBtc = { amount -> viewModel.devSubtractBtc(amount) },
                        onUpdateLocation = { loc -> viewModel.devUpdateHardwareLocation(loc) },
                        onFastForward = { viewModel.devFastForwardHour() },
                        onAddCustomStatement = { type, sender, recipient, institution, amountBtc, status, note, applyToBalance ->
                          viewModel.devAddCustomStatement(type, sender, recipient, institution, amountBtc, status, note, applyToBalance)
                        },
                        onUpdateTransaction = { original, type, sender, recipient, institution, amountBtc, status, note, timestamp ->
                          viewModel.devUpdateTransaction(original, type, sender, recipient, institution, amountBtc, status, note, timestamp)
                        },
                        onDeleteTransaction = { tx -> viewModel.devDeleteTransaction(tx) }
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
