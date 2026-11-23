package com.aistudio.bitcoinledger.btcxyz.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.data.TransactionEntity
import com.aistudio.bitcoinledger.btcxyz.ui.components.StatementReceiptDialog
import com.aistudio.bitcoinledger.btcxyz.ui.components.TransactionItemCard
import com.aistudio.bitcoinledger.btcxyz.ui.components.formatStatementDate
import com.aistudio.bitcoinledger.btcxyz.ui.components.statusDisplayLabel
import com.aistudio.bitcoinledger.btcxyz.ui.components.typeDisplayLabel
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionsScreen(
  transactions: List<TransactionEntity>,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "RECEIVE", "SEND"
  var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

  val filteredTransactions = remember(transactions, selectedFilter) {
    when (selectedFilter) {
      "RECEIVE" -> transactions.filter { it.type == "RECEIVE" || it.type == "HOURLY_REWARD" }
      "SEND" -> transactions.filter { it.type == "SEND" }
      else -> transactions
    }
  }

  val totalReceivedBtc = remember(transactions) {
    transactions.filter { it.type != "SEND" }.sumOf { it.amountBtc }
  }
  val totalSentBtc = remember(transactions) {
    transactions.filter { it.type == "SEND" }.sumOf { it.amountBtc }
  }
  val totalReceivedBrl = remember(transactions) {
    transactions.filter { it.type != "SEND" }.sumOf { it.amountBrl }
  }
  val totalSentBrl = remember(transactions) {
    transactions.filter { it.type == "SEND" }.sumOf { it.amountBrl }
  }

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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Extrato Financeiro",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = LedgerTextPrimary
          )
        }

        IconButton(
          onClick = {
            val statementText = buildString {
              appendLine("=== EXTRATO BITCOIN LEDGER ===")
              appendLine("Total Recebido: +${String.format(Locale.US, "%.6f", totalReceivedBtc)} BTC (${currencyBrlFormat.format(totalReceivedBrl)})")
              appendLine("Total Enviado: -${String.format(Locale.US, "%.6f", totalSentBtc)} BTC (${currencyBrlFormat.format(totalSentBrl)})")
              appendLine("--------------------------------")
              filteredTransactions.forEach { tx ->
                val sign = if (tx.type == "SEND") "-" else "+"
                appendLine("${formatStatementDate(tx.timestamp)} | ${typeDisplayLabel(tx.type)}")
                appendLine("Valor: $sign${String.format(Locale.US, "%.6f", tx.amountBtc)} BTC (${currencyBrlFormat.format(tx.amountBrl)})")
                appendLine("De: ${tx.sender} -> Para: ${tx.recipient}")
                appendLine("Status: ${statusDisplayLabel(tx.status)}")
                if (tx.note.isNotBlank()) appendLine("Obs: ${tx.note}")
                appendLine("--------------------------------")
              }
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, statementText)
            }
            context.startActivity(Intent.createChooser(intent, "Compartilhar Extrato"))
          },
          modifier = Modifier.testTag("share_statement_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Exportar Extrato", tint = LedgerGreen)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Summary Row: Entradas vs Saídas
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Recebimentos", fontSize = 11.sp, color = LedgerTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "+${String.format(Locale.US, "%.5f", totalReceivedBtc)} BTC",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = LedgerGreen
            )
            Text(
              text = currencyBrlFormat.format(totalReceivedBrl),
              fontSize = 11.sp,
              color = LedgerTextSecondary
            )
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Transferências", fontSize = 11.sp, color = LedgerTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "-${String.format(Locale.US, "%.5f", totalSentBtc)} BTC",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = LedgerRed
            )
            Text(
              text = currencyBrlFormat.format(totalSentBrl),
              fontSize = 11.sp,
              color = LedgerTextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedFilter == "ALL",
          onClick = { selectedFilter = "ALL" },
          label = { Text("Todos", fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LedgerGreen,
            selectedLabelColor = LedgerDarkBackground,
            containerColor = LedgerCard,
            labelColor = LedgerTextSecondary
          )
        )
        FilterChip(
          selected = selectedFilter == "RECEIVE",
          onClick = { selectedFilter = "RECEIVE" },
          label = { Text("Recebimentos", fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LedgerGreen,
            selectedLabelColor = LedgerDarkBackground,
            containerColor = LedgerCard,
            labelColor = LedgerTextSecondary
          )
        )
        FilterChip(
          selected = selectedFilter == "SEND",
          onClick = { selectedFilter = "SEND" },
          label = { Text("Transferências", fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LedgerGreen,
            selectedLabelColor = LedgerDarkBackground,
            containerColor = LedgerCard,
            labelColor = LedgerTextSecondary
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (filteredTransactions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "Nenhum registro encontrado", color = LedgerTextSecondary, fontSize = 14.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredTransactions, key = { it.id }) { tx ->
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
