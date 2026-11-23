package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
fun OnboardingScreen(onAccountCreated: (String, String) -> Unit) {
  var name by remember { mutableStateOf("Carteira Principal") }
  var pin by remember { mutableStateOf("") }
  var confirmPin by remember { mutableStateOf("") }
  var errorMsg by remember { mutableStateOf<String?>(null) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = LedgerDarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .background(LedgerCard, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Ledger Security",
          tint = LedgerGreen,
          modifier = Modifier.size(40.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Configurar Carteira Bitcoin",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = LedgerTextPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Proteja seus ativos com criptografia de nível de hardware",
        fontSize = 14.sp,
        color = LedgerTextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      Spacer(modifier = Modifier.height(32.dp))

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nome da Carteira") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LedgerGreen) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("wallet_name_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = pin,
        onValueChange = { pin = it },
        label = { Text("Definir PIN (4 dígitos)") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("pin_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = confirmPin,
        onValueChange = { confirmPin = it },
        label = { Text("Confirmar PIN") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("confirm_pin_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      if (errorMsg != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = errorMsg!!, color = LedgerRed, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(32.dp))

      Button(
        onClick = {
          if (name.isBlank()) {
            errorMsg = "Digite um nome para a carteira."
          } else if (pin.length < 4) {
            errorMsg = "O PIN deve ter pelo menos 4 dígitos."
          } else if (pin != confirmPin) {
            errorMsg = "Os PINs não coincidem."
          } else {
            onAccountCreated(name, pin)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("create_account_button"),
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = "Criar Carteira com Segurança", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LedgerDarkBackground)
      }
    }
  }
}
