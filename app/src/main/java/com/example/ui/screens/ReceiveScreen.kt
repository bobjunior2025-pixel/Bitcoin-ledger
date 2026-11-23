package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ReceiveScreen(onBack: () -> Unit) {
  val context = LocalContext.current
  val btcAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = LedgerDarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Receber Bitcoin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LedgerTextPrimary)
      }

      Spacer(modifier = Modifier.height(32.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(180.dp)
              .background(LedgerSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.QrCode,
              contentDescription = "QR Code",
              tint = LedgerGreen,
              modifier = Modifier.size(140.dp)
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          Text(
            text = "Seu Endereço Ledger Segurado",
            fontSize = 14.sp,
            color = LedgerTextSecondary,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = btcAddress,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = LedgerTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .background(LedgerSurface, RoundedCornerShape(8.dp))
              .padding(12.dp)
          )

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = android.content.ClipData.newPlainText("Bitcoin Address", btcAddress)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Endereço copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("copy_address_button"),
            colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = LedgerDarkBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Copiar Endereço", fontWeight = FontWeight.Bold, color = LedgerDarkBackground)
          }
        }
      }
    }
  }
}
