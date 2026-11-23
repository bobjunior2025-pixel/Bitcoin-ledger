package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
fun LockScreen(walletName: String, onUnlock: (String) -> Boolean) {
  var pin by remember { mutableStateOf("") }
  var error by remember { mutableStateOf(false) }

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
          imageVector = Icons.Default.Lock,
          contentDescription = "Lock",
          tint = LedgerGreen,
          modifier = Modifier.size(40.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = walletName,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = LedgerTextPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Digite seu PIN para desbloquear",
        fontSize = 14.sp,
        color = LedgerTextSecondary
      )

      Spacer(modifier = Modifier.height(32.dp))

      OutlinedTextField(
        value = pin,
        onValueChange = {
          pin = it
          error = false
        },
        label = { Text("PIN de Acesso") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("unlock_pin_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (error) LedgerRed else LedgerGreen,
          unfocusedBorderColor = if (error) LedgerRed else LedgerBorder,
          focusedLabelColor = if (error) LedgerRed else LedgerGreen,
          cursorColor = LedgerGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      if (error) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "PIN incorreto", color = LedgerRed, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          val success = onUnlock(pin)
          if (!success) {
            error = true
            pin = ""
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("unlock_button"),
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = "Desbloquear Carteira", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LedgerDarkBackground)
      }
    }
  }
}
