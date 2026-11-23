package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

@Composable
fun SettingsScreen(
  isDevUnlocked: Boolean,
  onBack: () -> Unit,
  onLockApp: () -> Unit,
  onNavigateTransactions: () -> Unit,
  onUnlockDev: (String) -> Boolean,
  onOpenDevPanel: () -> Unit
) {
  var showDevDialog by remember { mutableStateOf(false) }
  var devPinInput by remember { mutableStateOf("") }
  var devError by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = LedgerDarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Configurações",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = LedgerTextPrimary
          )
        }

        // Discreet small sun icon to trigger restricted access
        IconButton(
          onClick = {
            if (isDevUnlocked) {
              onOpenDevPanel()
            } else {
              showDevDialog = true
            }
          },
          modifier = Modifier.testTag("secret_sun_button")
        ) {
          Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = "Aparência",
            tint = LedgerTextSecondary.copy(alpha = 0.65f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("lock_wallet_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        onClick = onLockApp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen)
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Bloquear Carteira", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LedgerTextPrimary)
            Text(text = "Exigir PIN na próxima sessão", fontSize = 12.sp, color = LedgerTextSecondary)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LedgerTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("statement_settings_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        onClick = onNavigateTransactions
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = LedgerGreen)
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Extrato de Transferências", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LedgerTextPrimary)
            Text(text = "Comprovantes de envio e recebimento", fontSize = 12.sp, color = LedgerTextSecondary)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LedgerTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = LedgerGreen)
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Ledger Nano X", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LedgerTextPrimary)
            Text(text = "Assinatura por hardware ativa • Firmware v2.2.3", fontSize = 12.sp, color = LedgerTextSecondary)
          }
          Icon(Icons.Default.Verified, contentDescription = "Verificado", tint = LedgerGreen, modifier = Modifier.size(20.dp))
        }
      }
    }
  }

  if (showDevDialog) {
    AlertDialog(
      onDismissRequest = {
        showDevDialog = false
        devPinInput = ""
        devError = false
      },
      containerColor = LedgerCard,
      title = { Text("Acesso Restrito", color = LedgerTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "Digite a senha de autorização para continuar:",
            color = LedgerTextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = devPinInput,
            onValueChange = {
              devPinInput = it
              devError = false
            },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = devError,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("dev_passcode_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = LedgerGreen,
              unfocusedBorderColor = LedgerBorder,
              focusedLabelColor = LedgerGreen,
              cursorColor = LedgerGreen
            )
          )
          if (devError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Senha incorreta", color = LedgerRed, fontSize = 12.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val ok = onUnlockDev(devPinInput)
            if (ok) {
              showDevDialog = false
              devPinInput = ""
              devError = false
              onOpenDevPanel()
            } else {
              devError = true
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
          modifier = Modifier.testTag("confirm_dev_passcode_button")
        ) {
          Text("Confirmar", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showDevDialog = false
            devPinInput = ""
            devError = false
          }
        ) {
          Text("Cancelar", color = LedgerTextSecondary)
        }
      }
    )
  }
}
