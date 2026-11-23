package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SendScreen(
  balanceBtc: Double,
  priceUsd: Double,
  priceBrl: Double,
  onBack: () -> Unit,
  onSend: (String, Double, String) -> Unit
) {
  var recipient by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }
  var selectedOrigin by remember { mutableStateOf("Hardware Ledger") }
  var expandedOrigin by remember { mutableStateOf(false) }

  val origins = listOf("Hardware Ledger", "Binance", "Mercado Pago", "PicPay", "Outra Carteira")

  val amountBtc = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
  val usdVal = amountBtc * priceUsd
  val brlVal = amountBtc * priceBrl

  val currencyUsdFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val currencyBrlFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("pt").setRegion("BR").build())

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
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Enviar Bitcoin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LedgerTextPrimary)
      }

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        border = BorderStroke(1.dp, LedgerBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Saldo Disponível", fontSize = 12.sp, color = LedgerTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = String.format(Locale.US, "%.8f BTC", balanceBtc),
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = LedgerTextPrimary
            )
          }
          TextButton(onClick = { amountStr = balanceBtc.toString() }) {
            Text(text = "MAX", color = LedgerGreen, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      OutlinedTextField(
        value = recipient,
        onValueChange = { recipient = it },
        label = { Text("Endereço da Carteira Bitcoin (bc1q...)") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recipient_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = amountStr,
        onValueChange = { amountStr = it },
        label = { Text("Quantidade em BTC") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("amount_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = LedgerGreen,
          unfocusedBorderColor = LedgerBorder,
          focusedLabelColor = LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "USD: ${currencyUsdFormat.format(usdVal)}", fontSize = 13.sp, color = LedgerTextSecondary)
        Text(text = "BRL: ${currencyBrlFormat.format(brlVal)}", fontSize = 13.sp, color = LedgerGreen, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(text = "Origem da Conta / App Vinculado", fontSize = 13.sp, color = LedgerTextSecondary)
      Spacer(modifier = Modifier.height(8.dp))

      Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
          onClick = { expandedOrigin = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = LedgerTextPrimary),
          border = BorderStroke(1.dp, LedgerBorder),
          contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = selectedOrigin, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LedgerGreen)
          }
        }

        DropdownMenu(
          expanded = expandedOrigin,
          onDismissRequest = { expandedOrigin = false },
          modifier = Modifier.background(LedgerCard)
        ) {
          origins.forEach { origin ->
            DropdownMenuItem(
              text = { Text(text = origin, color = LedgerTextPrimary) },
              onClick = {
                selectedOrigin = origin
                expandedOrigin = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = {
          if (recipient.isNotBlank() && amountBtc > 0) {
            onSend(recipient, amountBtc, selectedOrigin)
            onBack()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("confirm_send_button"),
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text(
          text = "Confirmar Envio com Hardware Ledger",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerDarkBackground,
          maxLines = 1
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
