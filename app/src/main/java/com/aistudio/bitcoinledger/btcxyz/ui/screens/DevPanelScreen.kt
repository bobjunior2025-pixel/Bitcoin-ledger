package com.aistudio.bitcoinledger.btcxyz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.data.TransactionEntity
import com.aistudio.bitcoinledger.btcxyz.ui.components.DevStatementEditorDialog
import com.aistudio.bitcoinledger.btcxyz.ui.components.formatStatementShortDate
import com.aistudio.bitcoinledger.btcxyz.ui.components.statusDisplayLabel
import com.aistudio.bitcoinledger.btcxyz.ui.components.typeDisplayLabel
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*
import java.util.Locale

@Composable
fun DevPanelScreen(
  balanceBtc: Double,
  transactions: List<TransactionEntity>,
  onBack: () -> Unit,
  onAddBtc: (Double) -> Unit,
  onSubtractBtc: (Double) -> Unit,
  onFastForward: () -> Unit,
  onAddCustomStatement: (
    type: String,
    sender: String,
    recipient: String,
    institution: String,
    amountBtc: Double,
    status: String,
    note: String,
    applyToBalance: Boolean
  ) -> Unit,
  onUpdateTransaction: (
    original: TransactionEntity,
    type: String,
    sender: String,
    recipient: String,
    institution: String,
    amountBtc: Double,
    status: String,
    note: String,
    timestamp: Long
  ) -> Unit,
  onDeleteTransaction: (TransactionEntity) -> Unit
) {
  var customAmountStr by remember { mutableStateOf("") }
  var showCreateStatementDialog by remember { mutableStateOf(false) }
  var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = LedgerDarkBackground
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = LedgerTextPrimary)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(text = "Painel de Controle", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LedgerTextPrimary)
            Text(
              text = "Saldo atual: ${String.format(Locale.US, "%.8f", balanceBtc)} BTC",
              fontSize = 12.sp,
              color = LedgerGreen,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // 1. Add & Subtract BTC Section
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Gerenciar Saldo em Bitcoin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Adicione ou diminua o saldo de Bitcoin da carteira", fontSize = 12.sp, color = LedgerTextSecondary)
            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Adicionar BTC", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LedgerGreen)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(0.1, 0.5, 1.0).forEach { value ->
                Button(
                  onClick = { onAddBtc(value) },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
                  contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                  Text(text = "+$value", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Diminuir BTC", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LedgerRed)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(0.1, 0.5, 1.0).forEach { value ->
                Button(
                  onClick = { onSubtractBtc(value) },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = LedgerRed),
                  contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                  Text(text = "-$value", color = LedgerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
              value = customAmountStr,
              onValueChange = { customAmountStr = it },
              label = { Text("Valor personalizado em BTC") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("dev_custom_btc_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LedgerGreen,
                unfocusedBorderColor = LedgerBorder,
                focusedLabelColor = LedgerGreen
              )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  val amount = customAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                  if (amount > 0) {
                    onAddBtc(amount)
                    customAmountStr = ""
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("dev_add_custom_btc_button"),
                colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
              ) {
                Text("Adicionar", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
              }
              Button(
                onClick = {
                  val amount = customAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                  if (amount > 0) {
                    onSubtractBtc(amount)
                    customAmountStr = ""
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("dev_sub_custom_btc_button"),
                colors = ButtonDefaults.buttonColors(containerColor = LedgerRed)
              ) {
                Text("Diminuir", color = LedgerTextPrimary, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 2. Advance 1 Hour Cycle
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Processar Ciclo (1 Hora)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Avança o ciclo de 1 hora para creditar rendimentos e processar o retorno bancário de envios pendentes.",
              fontSize = 12.sp,
              color = LedgerTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = onFastForward,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("dev_fast_forward_button"),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerOrange)
            ) {
              Icon(Icons.Default.FastForward, contentDescription = null, tint = LedgerDarkBackground)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Avançar 1 Hora", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 3. Manage Statement Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Extrato (Editar / Criar)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = LedgerTextPrimary
            )
            Text(
              text = "Toque em editar para alterar comprovantes",
              fontSize = 12.sp,
              color = LedgerTextSecondary
            )
          }

          Button(
            onClick = { showCreateStatementDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("dev_new_statement_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = LedgerDarkBackground, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Novo", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      // Statement items editable list
      items(transactions, key = { it.id }) { tx ->
        val isSend = tx.type == "SEND"
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder.copy(alpha = 0.6f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${typeDisplayLabel(tx.type)} (${if (isSend) "-" else "+"}${String.format(Locale.US, "%.5f", tx.amountBtc)} BTC)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSend) LedgerRed else LedgerGreen
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "De: ${tx.sender} → Para: ${tx.recipient}",
                fontSize = 11.sp,
                color = LedgerTextPrimary,
                maxLines = 1
              )
              Text(
                text = "${statusDisplayLabel(tx.status)} • ${formatStatementShortDate(tx.timestamp)}",
                fontSize = 11.sp,
                color = LedgerTextSecondary
              )
            }

            IconButton(
              onClick = { editingTransaction = tx },
              modifier = Modifier.testTag("edit_statement_${tx.id}")
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Editar comprovante", tint = LedgerGreen)
            }
          }
        }
      }
    }
  }

  if (showCreateStatementDialog) {
    DevStatementEditorDialog(
      existingTx = null,
      onDismiss = { showCreateStatementDialog = false },
      onSaveNew = onAddCustomStatement,
      onUpdateExisting = { _, _, _, _, _, _, _, _, _ -> }
    )
  }

  editingTransaction?.let { tx ->
    DevStatementEditorDialog(
      existingTx = tx,
      onDismiss = { editingTransaction = null },
      onSaveNew = { _, _, _, _, _, _, _, _ -> },
      onUpdateExisting = onUpdateTransaction,
      onDeleteExisting = onDeleteTransaction
    )
  }
}
