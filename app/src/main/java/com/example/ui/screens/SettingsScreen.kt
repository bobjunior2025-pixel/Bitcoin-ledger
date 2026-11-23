package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
  isDevUnlocked: Boolean,
  onBack: () -> Unit,
  onLockApp: () -> Unit,
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
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Configurações", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LedgerTextPrimary)
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Lock Wallet Option
      Card(
        modifier = Modifier.fillMaxWidth(),
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

      // GitHub APK Integration Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Code, contentDescription = null, tint = LedgerGreen)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Integração GitHub Actions APK", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LedgerTextPrimary)
              Text(text = "Status: Workflow v1.2.0 Concluído (Sucesso)", fontSize = 12.sp, color = LedgerGreen)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "O APK otimizado desta carteira Bitcoin está sincronizado com o repositório GitHub para distribuição e instalação direta no dispositivo Android.",
            fontSize = 11.sp,
            color = LedgerTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Developer Mode / Secret Passcode Entry
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        onClick = {
          if (isDevUnlocked) {
            onOpenDevPanel()
          } else {
            showDevDialog = true
          }
        }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LedgerOrange)
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Função de Desenvolvedor (Secreto)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LedgerTextPrimary)
            Text(text = if (isDevUnlocked) "Painel Desbloqueado (Toque para abrir)" else "Requer senha secreta (6565)", fontSize = 12.sp, color = LedgerTextSecondary)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LedgerTextSecondary)
        }
      }
    }
  }

  if (showDevDialog) {
    AlertDialog(
      onDismissRequest = { showDevDialog = false },
      containerColor = LedgerCard,
      title = { Text("Acesso Desenvolvedor", color = LedgerTextPrimary) },
      text = {
        Column {
          Text("Digite a senha secreta (6565) para acessar as funções de desenvolvedor e gerenciamento de ativos:", color = LedgerTextSecondary, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = devPinInput,
            onValueChange = {
              devPinInput = it
              devError = false
            },
            label = { Text("Senha Secreta") },
            visualTransformation = PasswordVisualTransformation(),
            isError = devError,
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
              onOpenDevPanel()
            } else {
              devError = true
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
        ) {
          Text("Confirmar", color = LedgerDarkBackground)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDevDialog = false }) {
          Text("Cancelar", color = LedgerTextSecondary)
        }
      }
    )
  }
}
