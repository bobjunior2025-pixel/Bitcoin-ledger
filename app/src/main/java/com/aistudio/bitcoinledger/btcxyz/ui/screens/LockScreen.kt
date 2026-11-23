package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.ui.components.CpfVisualTransformation
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

@Composable
fun LockScreen(
  walletName: String,
  storedCpf: String,
  hardwareLocation: String,
  onLogin: (cpfInput: String, pinInput: String) -> Boolean
) {
  var cpfDigits by remember(storedCpf) { mutableStateOf(storedCpf.filter { it.isDigit() }) }
  var pin by remember { mutableStateOf("") }
  var errorMsg by remember { mutableStateOf<String?>(null) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = LedgerDarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Secret Ledger Notice for Jhonatan
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        border = BorderStroke(1.dp, LedgerOrange.copy(alpha = 0.65f))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Aviso Secreto",
            tint = LedgerOrange,
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "APLICATIVO SECRETO & CONFIDENCIAL",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = LedgerOrange
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "Este aplicativo é secreto e está conectado diretamente à conta Hardware Ledger do usuário Jhonatan.",
              fontSize = 12.sp,
              color = LedgerTextPrimary,
              lineHeight = 16.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Box(
        modifier = Modifier
          .size(74.dp)
          .background(LedgerCard, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Login Seguro",
          tint = LedgerGreen,
          modifier = Modifier.size(38.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Entrar na Conta",
        fontSize = 23.sp,
        fontWeight = FontWeight.Bold,
        color = LedgerTextPrimary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "$walletName • ${hardwareLocation.ifBlank { "Rua Leocádia, 856 - Realengo" }}",
        fontSize = 12.sp,
        color = LedgerGreen,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(26.dp))

      OutlinedTextField(
        value = cpfDigits,
        onValueChange = { input ->
          cpfDigits = input.filter { it.isDigit() }.take(11)
          errorMsg = null
        },
        label = { Text("CPF do Titular") },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = CpfVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_cpf_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = pin,
        onValueChange = {
          pin = it
          errorMsg = null
        },
        label = { Text("PIN de Acesso") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("unlock_pin_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (errorMsg != null) LedgerRed else LedgerGreen,
          unfocusedBorderColor = if (errorMsg != null) LedgerRed else LedgerBorder,
          focusedLabelColor = if (errorMsg != null) LedgerRed else LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      if (errorMsg != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMsg!!, color = LedgerRed, fontSize = 13.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          val success = onLogin(cpfDigits, pin)
          if (!success) {
            errorMsg = "CPF ou PIN inválido"
            pin = ""
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("unlock_button"),
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text(
          text = "Logar na Carteira",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerDarkBackground,
          maxLines = 1
        )
      }
    }
  }
}
