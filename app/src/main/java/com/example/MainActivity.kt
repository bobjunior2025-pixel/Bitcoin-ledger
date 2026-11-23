package com.example

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppState
import com.example.viewmodel.WalletViewModel

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

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = LedgerDarkBackground
        ) {
          when (appState) {
            is AppState.Loading -> {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LedgerGreen)
              }
            }
            is AppState.Onboarding -> {
              OnboardingScreen(
                onAccountCreated = { name, pin ->
                  viewModel.createAccount(name, pin)
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
                    ReceiveScreen(onBack = { currentScreen = "home" })
                  }
                  "transactions" -> {
                    BackHandler { currentScreen = "home" }
                    TransactionsScreen(transactions = transactions, onBack = { currentScreen = "home" })
                  }
                  "settings" -> {
                    BackHandler { currentScreen = "home" }
                    SettingsScreen(
                      isDevUnlocked = isDevUnlocked,
                      onBack = { currentScreen = "home" },
                      onLockApp = { viewModel.lockApp() },
                      onUnlockDev = { code -> viewModel.verifyDevPasscode(code) },
                      onOpenDevPanel = { currentScreen = "dev_panel" }
                    )
                  }
                  "dev_panel" -> {
                    BackHandler { currentScreen = "settings" }
                    DevPanelScreen(
                      onBack = { currentScreen = "settings" },
                      onAddBtc = { amount -> viewModel.devAddBtc(amount) },
                      onFastForward = { viewModel.devFastForwardHour() }
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
