package com.aistudio.bitcoinledger.btcxyz.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.data.TransactionEntity
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatStatementDate(timestamp: Long): String {
  val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss", Locale.Builder().setLanguage("pt").setRegion("BR").build())
  return sdf.format(Date(timestamp))
}

fun formatStatementShortDate(timestamp: Long): String {
  val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.Builder().setLanguage("pt").setRegion("BR").build())
  return sdf.format(Date(timestamp))
}

fun statusDisplayLabel(status: String): String = when (status) {
  "CONCLUDED" -> "Concluído"
  "PENDING" -> "Em processamento"
  "FAILED_BANK_RETENTION" -> "Retido pelo Banco"
  else -> status
}

fun typeDisplayLabel(type: String): String = when (type) {
  "SEND" -> "Transferência Enviada"
  "HOURLY_REWARD" -> "Rendimento Automático"
  else -> "Recebimento"
}

@Composable
fun TransactionItemCard(
  tx: TransactionEntity,
  onClick: (() -> Unit)? = null
) {
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
    modifier = Modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = LedgerCard),
    border = BorderStroke(1.dp, LedgerBorder.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
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
            contentDescription = typeDisplayLabel(tx.type),
            tint = if (isSend) LedgerRed else LedgerGreen,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = when {
              isSend -> "Envio • ${tx.recipient.take(14)}${if (tx.recipient.length > 14) "..." else ""}"
              isReward -> "Rendimento Automático"
              else -> "Recebimento • ${tx.sender}"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = LedgerTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$statusText • ${formatStatementShortDate(tx.timestamp)}",
            fontSize = 11.sp,
            color = statusColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (tx.note.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tx.note,
              fontSize = 10.sp,
              color = LedgerTextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

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

@Composable
fun StatementReceiptDialog(
  tx: TransactionEntity,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val isSend = tx.type == "SEND"
  val currencyUsdFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val currencyBrlFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("pt").setRegion("BR").build())

  val statusColor = when (tx.status) {
    "CONCLUDED" -> LedgerGreen
    "PENDING" -> LedgerOrange
    "FAILED_BANK_RETENTION" -> LedgerRed
    else -> LedgerTextSecondary
  }

  val txIdDisplay = tx.txHash.ifBlank {
    "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a${tx.id}"
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = LedgerCard,
    title = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = when (tx.status) {
            "FAILED_BANK_RETENTION" -> Icons.Default.ErrorOutline
            "PENDING" -> Icons.Default.Schedule
            else -> Icons.Default.Verified
          },
          contentDescription = null,
          tint = statusColor,
          modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Comprovante de ${typeDisplayLabel(tx.type)}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          color = statusColor.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = statusDisplayLabel(tx.status),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = statusColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Card(
          colors = CardDefaults.cardColors(containerColor = LedgerSurface),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${if (isSend) "-" else "+"}${String.format(Locale.US, "%.8f", tx.amountBtc)} BTC",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSend) LedgerRed else LedgerGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${currencyBrlFormat.format(tx.amountBrl)}  •  ${currencyUsdFormat.format(tx.amountUsd)}",
              fontSize = 12.sp,
              color = LedgerTextSecondary
            )
          }
        }

        ReceiptRow("Data e Hora", formatStatementDate(tx.timestamp))
        ReceiptRow("Quem enviou", tx.sender.ifBlank { "Ledger Nano X" })
        ReceiptRow("Destinatário", tx.recipient)
        ReceiptRow("Instituição / Rede", tx.institution.ifBlank { "Bitcoin Network (SegWit)" })
        ReceiptRow("Taxa de Rede", "${String.format(Locale.US, "%.6f", tx.feeBtc)} BTC")

        HorizontalDivider(color = LedgerBorder)

        Text(text = "ID da Transação (TXID)", fontSize = 11.sp, color = LedgerTextSecondary)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(LedgerSurface, RoundedCornerShape(8.dp))
            .clickable {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("TXID", txIdDisplay))
              Toast.makeText(context, "TXID copiado!", Toast.LENGTH_SHORT).show()
            }
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = txIdDisplay,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = LedgerTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.Default.ContentCopy, contentDescription = "Copiar TXID", tint = LedgerGreen, modifier = Modifier.size(16.dp))
        }

        if (tx.note.isNotBlank()) {
          Text(text = "Observação", fontSize = 11.sp, color = LedgerTextSecondary)
          Text(
            text = tx.note,
            fontSize = 12.sp,
            color = if (tx.status == "FAILED_BANK_RETENTION") LedgerRed else LedgerTextPrimary
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val shareText = buildString {
            appendLine("=== COMPROVANTE BITCOIN LEDGER ===")
            appendLine("Tipo: ${typeDisplayLabel(tx.type)}")
            appendLine("Status: ${statusDisplayLabel(tx.status)}")
            appendLine("Valor: ${String.format(Locale.US, "%.8f", tx.amountBtc)} BTC")
            appendLine("Valor BRL: ${currencyBrlFormat.format(tx.amountBrl)}")
            appendLine("Valor USD: ${currencyUsdFormat.format(tx.amountUsd)}")
            appendLine("Data: ${formatStatementDate(tx.timestamp)}")
            appendLine("Quem enviou: ${tx.sender}")
            appendLine("Destinatário: ${tx.recipient}")
            appendLine("Instituição/Rede: ${tx.institution}")
            appendLine("Taxa: ${String.format(Locale.US, "%.6f", tx.feeBtc)} BTC")
            appendLine("TXID: $txIdDisplay")
            if (tx.note.isNotBlank()) appendLine("Obs: ${tx.note}")
          }
          val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
          }
          context.startActivity(Intent.createChooser(intent, "Compartilhar Comprovante"))
        },
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
      ) {
        Icon(Icons.Default.Share, contentDescription = null, tint = LedgerDarkBackground, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Compartilhar", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Fechar", color = LedgerTextSecondary)
      }
    }
  )
}

@Composable
private fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(text = label, fontSize = 12.sp, color = LedgerTextSecondary, modifier = Modifier.width(120.dp))
    Text(
      text = value,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      color = LedgerTextPrimary,
      modifier = Modifier.weight(1f)
    )
  }
}
