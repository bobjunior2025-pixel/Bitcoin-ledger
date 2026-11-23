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
import androidx.compose.material.icons.filled.Warning
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
  priceBrl: Double,
  hardwareLocation: String,
  transactions: List<TransactionEntity>,
  onBack: () -> Unit,
  onAddBtc: (Double) -> Unit,
  onAddBrlAsBtc: (Double) -> Unit,
  onSubtractBtc: (Double) -> Unit,
  onUpdateLocation: (String) -> Unit,
  onFastForward: () -> Unit,
  onMarkBankFailure: (TransactionEntity, String) -> Unit,
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
  var customBrlStr by remember { mutableStateOf("") }
  var locationInput by remember(hardwareLocation) { mutableStateOf(hardwareLocation) }
  var showCreateStatementDialog by remember { mutableStateOf(false) }
  var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
  var bankFailureTargetTx by remember { mutableStateOf<TransactionEntity?>(null) }

  val parsedBrl = customBrlStr.replace(",", ".").toDoubleOrNull() ?: 0.0
  val previewConvertedBtc = if (parsedBrl > 0 && priceBrl > 0) parsedBrl / priceBrl else 0.0

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

      // 1. Add Reais (BRL) and automatically convert to BTC
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerGreen.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Adicionar Reais (BRL → Bitcoin)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = LedgerTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Digite ou escolha um valor em Reais para converter automaticamente para Bitcoin na cotação atual.",
              fontSize = 12.sp,
              color = LedgerTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(1000.0 to "+R$1k", 10000.0 to "+R$10k", 50000.0 to "+R$50k").forEach { (brlVal, label) ->
                Button(
                  onClick = { onAddBrlAsBtc(brlVal) },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
                  contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
                ) {
                  Text(text = label, color = LedgerDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = customBrlStr,
              onValueChange = { customBrlStr = it },
              label = { Text("Valor em Reais (R$)") },
              placeholder = { Text("Ex: 2500.00") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("dev_custom_brl_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LedgerGreen,
                unfocusedBorderColor = LedgerBorder,
                focusedLabelColor = LedgerGreen
              )
            )

            if (previewConvertedBtc > 0) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Conversão: ≈ ${String.format(Locale.US, "%.8f", previewConvertedBtc)} BTC",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = LedgerGreen
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                if (parsedBrl > 0) {
                  onAddBrlAsBtc(parsedBrl)
                  customBrlStr = ""
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("dev_convert_brl_button"),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
            ) {
              Text(
                text = "Converter Reais e Adicionar BTC",
                color = LedgerDarkBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }
        }
      }

      // 2. Add & Subtract BTC Section
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
            Text(text = "Adicione ou diminua o saldo de Bitcoin diretamente", fontSize = 12.sp, color = LedgerTextSecondary)
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
                  contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
                ) {
                  Text(text = "+$value", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
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
                  contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
                ) {
                  Text(text = "-$value", color = LedgerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
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
                  .height(48.dp)
                  .testTag("dev_add_custom_btc_button"),
                colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Text("Adicionar", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, maxLines = 1)
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
                  .height(48.dp)
                  .testTag("dev_sub_custom_btc_button"),
                colors = ButtonDefaults.buttonColors(containerColor = LedgerRed),
                contentPadding = PaddingValues(horizontal = 8.dp)
              ) {
                Text("Diminuir", color = LedgerTextPrimary, fontWeight = FontWeight.Bold, maxLines = 1)
              }
            }
          }
        }
      }

      // 3. Hardware Location Editor
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Localização do Hardware Ledger", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LedgerTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = locationInput,
              onValueChange = { locationInput = it },
              label = { Text("Endereço do Hardware") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LedgerGreen,
                unfocusedBorderColor = LedgerBorder,
                focusedLabelColor = LedgerGreen
              )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { onUpdateLocation(locationInput) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerGreen)
            ) {
              Text("Salvar Localização", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, maxLines = 1)
            }
          }
        }
      }

      // 4. Advance 1 Hour Cycle
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
                .height(48.dp)
                .testTag("dev_fast_forward_button"),
              colors = ButtonDefaults.buttonColors(containerColor = LedgerOrange)
            ) {
              Icon(Icons.Default.FastForward, contentDescription = null, tint = LedgerDarkBackground)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Avançar 1 Hora", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, maxLines = 1)
            }
          }
        }
      }

      // 5. Manage Statement Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Extrato (Falha do Banco / Editar)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = LedgerTextPrimary
            )
            Text(
              text = "Aplique falha bancária ou edite comprovantes",
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
            Text("Novo", color = LedgerDarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
          }
        }
      }

      // Statement items editable list with quick Bank Failure button
      items(transactions, key = { it.id }) { tx ->
        val isSend = tx.type == "SEND"
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LedgerCard),
          border = BorderStroke(1.dp, LedgerBorder.copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
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

              Row {
                IconButton(
                  onClick = { bankFailureTargetTx = tx },
                  modifier = Modifier.testTag("bank_failure_${tx.id}")
                ) {
                  Icon(Icons.Default.Warning, contentDescription = "Falha por causa do banco", tint = LedgerRed)
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

  bankFailureTargetTx?.let { tx ->
    BankFailureDialog(
      tx = tx,
      onDismiss = { bankFailureTargetTx = null },
      onConfirm = { bankName ->
        onMarkBankFailure(tx, bankName)
        bankFailureTargetTx = null
      }
    )
  }
}

@Composable
private fun BankFailureDialog(
  tx: TransactionEntity,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var bankName by remember { mutableStateOf(tx.institution.ifBlank { "Mercado Pago" }) }
  val presetBanks = listOf("Mercado Pago", "Binance", "Nubank", "Banco do Brasil", "Itaú", "Bradesco", "Santander", "Caixa")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = LedgerCard,
    title = {
      Text("Modificar Extrato: Falha do Banco", color = LedgerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Escolha ou digite o banco responsável pela retenção/falha no extrato:",
          fontSize = 12.sp,
          color = LedgerTextSecondary
        )
        OutlinedTextField(
          value = bankName,
          onValueChange = { bankName = it },
          label = { Text("Nome do Banco / Instituição") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LedgerRed,
            unfocusedBorderColor = LedgerBorder,
            focusedLabelColor = LedgerRed
          )
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetBanks.take(4).forEach { b ->
            FilterChip(
              selected = bankName == b,
              onClick = { bankName = b },
              label = { Text(b, fontSize = 10.sp, maxLines = 1) }
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetBanks.drop(4).forEach { b ->
            FilterChip(
              selected = bankName == b,
              onClick = { bankName = b },
              label = { Text(b, fontSize = 10.sp, maxLines = 1) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(bankName) },
        colors = ButtonDefaults.buttonColors(containerColor = LedgerRed)
      ) {
        Text("Aplicar Falha do Banco", color = LedgerTextPrimary, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = LedgerTextSecondary)
      }
    }
  )
}
