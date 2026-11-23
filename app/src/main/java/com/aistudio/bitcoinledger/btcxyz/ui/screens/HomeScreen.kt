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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
      // Header with Hardware Ledger connection & default location
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(LedgerCard, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Shield, contentDescription = "Hardware Ledger", tint = LedgerGreen, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = account.name,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = LedgerTextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(LedgerGreen, CircleShape)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Conectado ao Hardware Ledger",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LedgerGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Localização do Hardware",
                tint = LedgerTextSecondary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = account.hardwareLocation.ifBlank { "Rua Leocádia, 856 - Realengo" },
                fontSize = 11.sp,
                color = LedgerTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        IconButton(onClick = onNavigateSettings, modifier = Modifier.testTag("settings_button")) {
          Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = LedgerTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Balance Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerCard),
        border = BorderStroke(1.dp, LedgerBorder.copy(alpha = 0.6f))
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Saldo Total em Bitcoin", fontSize = 13.sp, color = LedgerTextSecondary)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "${btcFormat.format(btcBalance)} BTC",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = LedgerTextPrimary,
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = LedgerSurface,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, LedgerBorder.copy(alpha = 0.5f))
            ) {
              Text(
                text = "US: ${currencyUsdFormat.format(usdValue)}",
                fontSize = 12.sp,
                color = LedgerTextSecondary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                maxLines = 1
              )
            }
            Surface(
              color = LedgerSurface,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, LedgerGreen.copy(alpha = 0.35f))
            ) {
              Text(
                text = "BR: ${currencyBrlFormat.format(brlValue)}",
                fontSize = 12.sp,
                color = LedgerGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                maxLines = 1
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
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LedgerGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Rendimento a cada 1h",
                fontSize = 12.sp,
                color = LedgerTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "+0.00005 BTC/h",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = LedgerGreen,
              softWrap = false,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Redesigned Crisp Action Buttons (Icon on top + single-line label below)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        HomeActionButton(
          label = "Enviar",
          icon = Icons.Default.ArrowUpward,
          isPrimary = true,
          onClick = onNavigateSend,
          modifier = Modifier
            .weight(1f)
            .testTag("send_button")
        )
        HomeActionButton(
          label = "Receber",
          icon = Icons.Default.ArrowDownward,
          isPrimary = false,
          onClick = onNavigateReceive,
          modifier = Modifier
            .weight(1f)
            .testTag("receive_button")
        )
        HomeActionButton(
          label = "Extrato",
          icon = Icons.Default.ReceiptLong,
          isPrimary = false,
          onClick = onNavigateTransactions,
          modifier = Modifier
            .weight(1f)
            .testTag("statement_button")
        )
      }

      Spacer(modifier = Modifier.height(22.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Últimas Movimentações", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
        TextButton(onClick = onNavigateTransactions) {
          Text(text = "Ver Extrato", color = LedgerGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun HomeActionButton(
  label: String,
  icon: ImageVector,
  isPrimary: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor = if (isPrimary) LedgerGreen else LedgerCard
  val contentColor = if (isPrimary) LedgerDarkBackground else LedgerTextPrimary
  val iconBgColor = if (isPrimary) LedgerDarkBackground.copy(alpha = 0.14f) else LedgerSurface
  val iconTint = if (isPrimary) LedgerDarkBackground else LedgerGreen
  val border: BorderStroke? = if (isPrimary) null else BorderStroke(1.dp, LedgerBorder)

  Surface(
    onClick = onClick,
    modifier = modifier.height(82.dp),
    shape = RoundedCornerShape(16.dp),
    color = containerColor,
    border = border
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .background(iconBgColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = iconTint,
          modifier = Modifier.size(19.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = label,
        color = contentColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false
      )
    }
  }
}
