package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

private fun formatCpfInput(digits: String): String {
  val clean = digits.filter { it.isDigit() }.take(11)
  return buildString {
    clean.forEachIndexed { index, c ->
      if (index == 3 || index == 6) append('.')
      if (index == 9) append('-')
      append(c)
    }
  }
}

@Composable
fun OnboardingScreen(
  onAccountCreated: (name: String, cpf: String, keyAddress: String, pin: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var cpf by remember { mutableStateOf("") }
  var keyAddress by remember { mutableStateOf("bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh") }
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
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(horizontal = 24.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .background(LedgerCard, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Segurança Hardware Ledger",
          tint = LedgerGreen,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Cadastro de Conta",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = LedgerTextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Conectado ao Hardware Ledger • Rua Leocádia, 856 - Realengo",
        fontSize = 12.sp,
        color = LedgerGreen,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          errorMsg = null
        },
        label = { Text("Nome completo") },
        placeholder = { Text("Ex: João Silva") },
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

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = cpf,
        onValueChange = { input ->
          cpf = formatCpfInput(input)
          errorMsg = null
        },
        label = { Text("CPF") },
        placeholder = { Text("000.000.000-00") },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = LedgerGreen) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("cpf_input"),
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
        value = keyAddress,
        onValueChange = {
          keyAddress = it
          errorMsg = null
        },
        label = { Text("Endereço de chave (Carteira Bitcoin)") },
        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = LedgerGreen) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("key_address_input"),
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
        label = { Text("Definir PIN de acesso (4+ dígitos)") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = confirmPin,
        onValueChange = {
          confirmPin = it
          errorMsg = null
        },
        label = { Text("Confirmar PIN") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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
        Text(text = errorMsg!!, color = LedgerRed, fontSize = 13.sp, textAlign = TextAlign.Center)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          val cpfDigits = cpf.filter { it.isDigit() }
          when {
            name.isBlank() -> errorMsg = "Informe seu nome completo."
            cpfDigits.length != 11 -> errorMsg = "Informe um CPF válido com 11 dígitos."
            keyAddress.isBlank() -> errorMsg = "Informe o endereço de chave da carteira."
            pin.length < 4 -> errorMsg = "O PIN deve ter pelo menos 4 dígitos."
            pin != confirmPin -> errorMsg = "Os PINs não coincidem."
            else -> onAccountCreated(name, cpf, keyAddress, pin)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("create_account_button"),
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Criar Conta e Vincular Ledger",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerDarkBackground,
          maxLines = 1
        )
      }
    }
  }
}
