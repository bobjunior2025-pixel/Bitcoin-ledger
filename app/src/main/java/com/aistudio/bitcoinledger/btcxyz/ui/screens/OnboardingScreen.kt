package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.aistudio.bitcoinledger.btcxyz.ui.components.CpfVisualTransformation
import com.aistudio.bitcoinledger.btcxyz.ui.components.formatCpfDigits
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

@Composable
fun OnboardingScreen(
  onAccountCreated: (name: String, cpf: String, keyAddress: String, pin: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var cpfDigits by remember { mutableStateOf("") }
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
        .padding(horizontal = 24.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Confidential Notice Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        border = BorderStroke(1.dp, LedgerOrange.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Aviso Confidencial",
            tint = LedgerOrange,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "AVISO DE ACESSO SECRETO",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = LedgerOrange
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Este aplicativo é secreto e está conectado diretamente à conta Hardware Ledger do usuário Jhonatan.",
              fontSize = 11.sp,
              color = LedgerTextPrimary,
              lineHeight = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Box(
        modifier = Modifier
          .size(68.dp)
          .background(LedgerCard, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Segurança Hardware Ledger",
          tint = LedgerGreen,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Cadastro de Conta",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = LedgerTextPrimary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Hardware Ledger • Rua Leocádia, 856 - Realengo",
        fontSize = 12.sp,
        color = LedgerGreen,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          errorMsg = null
        },
        label = { Text("Nome completo") },
        placeholder = { Text("Ex: Jhonatan Silva") },
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
        value = cpfDigits,
        onValueChange = { input ->
          cpfDigits = input.filter { it.isDigit() }.take(11)
          errorMsg = null
        },
        label = { Text("CPF") },
        placeholder = { Text("000.000.000-00") },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = LedgerGreen) },
        visualTransformation = CpfVisualTransformation(),
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
        label = { Text("Definir PIN de login (4+ dígitos)") },
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

      Spacer(modifier = Modifier.height(22.dp))

      Button(
        onClick = {
          when {
            name.isBlank() -> errorMsg = "Informe seu nome completo."
            cpfDigits.length != 11 -> errorMsg = "Informe um CPF válido com 11 dígitos."
            keyAddress.isBlank() -> errorMsg = "Informe o endereço de chave da carteira."
            pin.length < 4 -> errorMsg = "O PIN deve ter pelo menos 4 dígitos."
            pin != confirmPin -> errorMsg = "Os PINs não coincidem."
            else -> onAccountCreated(name, formatCpfDigits(cpfDigits), keyAddress, pin)
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
          text = "Cadastrar e Ir para Login",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerDarkBackground,
          maxLines = 1
        )
      }
    }
  }
}
