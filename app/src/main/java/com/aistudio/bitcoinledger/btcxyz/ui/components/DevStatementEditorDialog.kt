package com.aistudio.bitcoinledger.btcxyz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.bitcoinledger.btcxyz.data.TransactionEntity
import com.aistudio.bitcoinledger.btcxyz.ui.theme.*

@Composable
fun DevStatementEditorDialog(
  existingTx: TransactionEntity?,
  onDismiss: () -> Unit,
  onSaveNew: (
    type: String,
    sender: String,
    recipient: String,
    institution: String,
    amountBtc: Double,
    status: String,
    note: String,
    applyToBalance: Boolean
  ) -> Unit,
  onUpdateExisting: (
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
  onDeleteExisting: ((TransactionEntity) -> Unit)? = null
) {
  var type by remember { mutableStateOf(existingTx?.type ?: "RECEIVE") }
  var sender by remember { mutableStateOf(existingTx?.sender ?: "Binance Exchange") }
  var recipient by remember { mutableStateOf(existingTx?.recipient ?: "Carteira Principal") }
  var institution by remember { mutableStateOf(existingTx?.institution ?: "Bitcoin Network (SegWit)") }
  var amountStr by remember { mutableStateOf(existingTx?.amountBtc?.toString() ?: "0.05") }
  var status by remember { mutableStateOf(existingTx?.status ?: "CONCLUDED") }
  var note by remember { mutableStateOf(existingTx?.note ?: "") }
  var applyToBalance by remember { mutableStateOf(true) }
  var hoursAgoStr by remember { mutableStateOf("0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = LedgerCard,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (existingTx == null) "Novo Registro no Extrato" else "Editar Comprovante",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = LedgerTextPrimary
        )
        if (existingTx != null && onDeleteExisting != null) {
          IconButton(
            onClick = {
              onDeleteExisting(existingTx)
              onDismiss()
            }
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = LedgerRed)
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Tipo de Operação", fontSize = 12.sp, color = LedgerTextSecondary)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("RECEIVE" to "Recebimento", "SEND" to "Envio", "HOURLY_REWARD" to "Rendimento").forEach { (key, label) ->
            FilterChip(
              selected = type == key,
              onClick = { type = key },
              label = { Text(label, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LedgerGreen,
                selectedLabelColor = LedgerDarkBackground,
                containerColor = LedgerSurface,
                labelColor = LedgerTextSecondary
              )
            )
          }
        }

        OutlinedTextField(
          value = sender,
          onValueChange = { sender = it },
          label = { Text("Quem enviou (Remetente)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerGreen,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerGreen
          )
        )

        OutlinedTextField(
          value = recipient,
          onValueChange = { recipient = it },
          label = { Text("Destinatário / Carteira") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerGreen,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerGreen
          )
        )

        OutlinedTextField(
          value = institution,
          onValueChange = { institution = it },
          label = { Text("Instituição / App (ex: Binance, Mercado Pago)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerGreen,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerGreen
          )
        )

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("Valor em BTC") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerGreen,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerGreen
          )
        )

        Text("Status da Operação", fontSize = 12.sp, color = LedgerTextSecondary)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          listOf(
            "CONCLUDED" to "Concluído",
            "PENDING" to "Pendente (Aguardando)",
            "FAILED_BANK_RETENTION" to "Falhou (Retido pelo Banco)"
          ).forEach { (key, label) ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              RadioButton(
                selected = status == key,
                onClick = { status = key },
                colors = RadioButtonDefaults.colors(selectedColor = LedgerGreen)
              )
              Text(text = label, fontSize = 12.sp, color = LedgerTextPrimary)
            }
          }
        }

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Observação / Motivo") },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerGreen,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerGreen
          )
        )

        if (existingTx != null) {
          OutlinedTextField(
            value = hoursAgoStr,
            onValueChange = { hoursAgoStr = it },
            label = { Text("Recuar horário (horas atrás, ex: 0, 2, 24)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = LedgerGreen,
              unfocusedBorderColor = LedgerBorder,
              focusedLabelColor = LedgerGreen
            )
          )
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Checkbox(
              checked = applyToBalance,
              onCheckedChange = { applyToBalance = it },
              colors = CheckboxDefaults.colors(checkedColor = LedgerGreen)
            )
            Text(
              text = "Atualizar saldo da carteira automaticamente",
              fontSize = 12.sp,
              color = LedgerTextSecondary
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
          if (amount > 0) {
            if (existingTx == null) {
              onSaveNew(type, sender, recipient, institution, amount, status, note, applyToBalance)
            } else {
              val hoursBack = hoursAgoStr.toLongOrNull() ?: 0L
              val adjustedTs = if (hoursBack > 0L) {
                System.currentTimeMillis() - hoursBack * 3600_000L
              } else {
                existingTx.timestamp
              }
              onUpdateExisting(existingTx, type, sender, recipient, institution, amount, status, note, adjustedTs)
            }
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Salvar", color = LedgerDarkBackground, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = LedgerTextSecondary)
      }
    }
  )
}
