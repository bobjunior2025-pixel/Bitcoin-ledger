package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

@Composable
fun DevPanelScreen(
  onBack: () -> Unit,
  onAddBtc: (Double) -> Unit,
  onFastForward: () -> Unit
) {
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
        Text(text = "Painel de Desenvolvedor", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LedgerTextPrimary)
      }

      Spacer(modifier = Modifier.height(24.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "Adicionar Bitcoin ao Saldo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Injete fundos instantaneamente na carteira Ledger", fontSize = 13.sp, color = LedgerTextSecondary)
          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Button(
              onClick = { onAddBtc(0.1) },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
            ) {
              Text(text = "+0.1 BTC", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = { onAddBtc(0.5) },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
            ) {
              Text(text = "+0.5 BTC", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = { onAddBtc(1.0) },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
            ) {
              Text(text = "+1.0 BTC", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "Avançar Tempo (1 Hora)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Simula a passagem de 1 hora para creditar rendimentos e forçar falhas de retenção bancária em transferências pendentes.", fontSize = 13.sp, color = LedgerTextSecondary)
          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onFastForward,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = LedgerOrange)
          ) {
            Icon(Icons.Default.FastForward, contentDescription = null, tint = LedgerDarkBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Avançar 1 Hora Instantaneamente", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
