package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.data.TransactionEntity
import com.aistudio.bitcoinledger.btcxyz.data.WalletAccountEntity
import com.aistudio.bitcoinledger.btcxyz.ui.components.StatementReceiptDialog
import com.aistudio.bitcoinledger.btcxyz.ui.components.TransactionItemCard
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
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
  var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

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

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
          Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = LedgerDarkBackground, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Enviar", fontWeight = FontWeight.Bold, color = LedgerDarkBackground, fontSize = 14.sp)
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
          Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = LedgerGreen, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Receber", fontWeight = FontWeight.Bold, color = LedgerTextPrimary, fontSize = 14.sp)
        }

        OutlinedButton(
          onClick = onNavigateTransactions,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("statement_button"),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, LedgerBorder),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = LedgerTextPrimary)
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = LedgerGreen, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Extrato", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Últimas Movimentações", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
        TextButton(onClick = onNavigateTransactions) {
          Text(text = "Ver Extrato", color = LedgerGreen, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      val recentTxs = transactions.take(5)
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
          items(recentTxs, key = { it.id }) { tx ->
            TransactionItemCard(
              tx = tx,
              onClick = { selectedTransaction = tx }
            )
          }
        }
      }
    }
  }

  selectedTransaction?.let { tx ->
    StatementReceiptDialog(
      tx = tx,
      onDismiss = { selectedTransaction = null }
    )
  }
}
