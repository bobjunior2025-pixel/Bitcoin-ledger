package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.data.WalletAccountEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
  account: WalletAccountEntity,
  transactions: List<TransactionEntity>,
  priceUsd: Double,
  priceBrl: Double,
  onNavigateSend: () -> Unit,
  onNavigateReceive: () -> Unit,
  onNavigateTransactions: () -> Unit,
  onNavigateSettings: () -> Unit
) {
  val btcBalance = account.balanceBtc
  val usdValue = btcBalance * priceUsd
  val brlValue = btcBalance * priceBrl

  val btcFormat = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 8
    maximumFractionDigits = 8
  }
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
      // Top Bar / Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(LedgerCard, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Shield, contentDescription = "Ledger", tint = LedgerGreen, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = account.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(LedgerGreen, CircleShape)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Ledger Nano X Conectado", fontSize = 12.sp, color = LedgerGreen)
            }
          }
        }

        IconButton(onClick = onNavigateSettings, modifier = Modifier.testTag("settings_button")) {
          Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = LedgerTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Balance Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Saldo Total em Bitcoin", fontSize = 13.sp, color = LedgerTextSecondary)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "${btcFormat.format(btcBalance)} BTC",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = LedgerTextPrimary
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = LedgerSurface,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "US: ${currencyUsdFormat.format(usdValue)}",
                fontSize = 12.sp,
                color = LedgerTextSecondary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
            Surface(
              color = LedgerSurface,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "BR: ${currencyBrlFormat.format(brlValue)}",
                fontSize = 12.sp,
                color = LedgerGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider(color = LedgerBorder)
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LedgerGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Rendimento automático a cada 1h", fontSize = 12.sp, color = LedgerTextSecondary)
            }
            Text(text = "+0.00005 BTC/h", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LedgerGreen)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Button(
          onClick = onNavigateSend,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("send_button"),
          colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = LedgerDarkBackground)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Enviar", fontWeight = FontWeight.Bold, color = LedgerDarkBackground, fontSize = 15.sp)
        }

        Button(
          onClick = onNavigateReceive,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("receive_button"),
          colors = ButtonDefaults.buttonColors(containerColor = LedgerCard),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = LedgerGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Receber", fontWeight = FontWeight.Bold, color = LedgerTextPrimary, fontSize = 15.sp)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Transactions Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Histórico de Transações", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
        TextButton(onClick = onNavigateTransactions) {
          Text(text = "Ver Todas", color = LedgerGreen, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Recent Transactions List (top 3)
      val recentTxs = transactions.take(4)
      if (recentTxs.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "Nenhuma transação registrada ainda", color = LedgerTextSecondary, fontSize = 13.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(recentTxs) { tx ->
            TransactionItemCard(tx = tx)
          }
        }
      }
    }
  }
}

@Composable
fun TransactionItemCard(tx: TransactionEntity) {
  val isSend = tx.type == "SEND"
  val isReward = tx.type == "HOURLY_REWARD"
  val currencyBrlFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("pt").setRegion("BR").build())

  val statusColor = when (tx.status) {
    "CONCLUDED" -> LedgerGreen
    "PENDING" -> LedgerOrange
    "FAILED_BANK_RETENTION" -> LedgerRed
    else -> LedgerTextSecondary
  }

  val statusText = when (tx.status) {
    "CONCLUDED" -> "Concluído"
    "PENDING" -> "Pendente (Aguardando)"
    "FAILED_BANK_RETENTION" -> "Falhou: Banco reteu o valor"
    else -> tx.status
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = LedgerCard)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(if (isSend) LedgerRed.copy(alpha = 0.15f) else LedgerGreen.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when {
              isSend -> Icons.Default.CallMade
              isReward -> Icons.Default.Autorenew
              else -> Icons.Default.CallReceived
            },
            contentDescription = null,
            tint = if (isSend) LedgerRed else LedgerGreen,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = if (isSend) "Envio para ${tx.recipient.take(12)}..." else if (isReward) "Rendimento Automático" else "Recebimento",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = LedgerTextPrimary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = statusText, fontSize = 11.sp, color = statusColor)
          if (tx.note.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = tx.note, fontSize = 10.sp, color = LedgerTextSecondary, maxLines = 1)
          }
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${if (isSend) "-" else "+"}${String.format(Locale.US, "%.6f", tx.amountBtc)} BTC",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = if (isSend) LedgerRed else LedgerGreen
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = currencyBrlFormat.format(tx.amountBrl),
          fontSize = 11.sp,
          color = LedgerTextSecondary
        )
      }
    }
  }
}
