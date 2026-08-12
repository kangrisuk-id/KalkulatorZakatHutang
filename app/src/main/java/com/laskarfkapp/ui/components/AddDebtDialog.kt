package com.laskarfkapp.zakathutang.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.laskarfkapp.zakathutang.model.DebtType

@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        type: DebtType,
        personName: String,
        amount: Double,
        notes: String,
        contact: String,
        dueDate: Long
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(DebtType.HUTANG) }
    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var contactInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var dueDaysInput by remember { mutableStateOf("30") } // Default 30 hari lagi
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Catatan Transaksi",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Type Radio Group
                Text(
                    text = "Jenis Transaksi:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedType == DebtType.HUTANG,
                        onClick = { selectedType = DebtType.HUTANG },
                        modifier = Modifier.testTag("radio_type_hutang")
                    )
                    Text(
                        text = "Hutang (Saya Berhutang)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedType == DebtType.PIUTANG,
                        onClick = { selectedType = DebtType.PIUTANG },
                        modifier = Modifier.testTag("radio_type_piutang")
                    )
                    Text(
                        text = "Piutang (Orang Berhutang)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nama Orang / Kontak") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_person_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal Total (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_amount_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactInput,
                    onValueChange = { contactInput = it },
                    label = { Text("No. HP / Kontak (Opsional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Keterangan / Catatan (Opsional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dueDaysInput,
                    onValueChange = { dueDaysInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Jatuh Tempo (Berapa Hari Lagi)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_due_days_input")
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    val days = dueDaysInput.toLongOrNull() ?: 30L
                    val calculatedDueDate = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                    if (nameInput.isBlank()) {
                        errorText = "Nama kontak wajib diisi"
                    } else if (amount <= 0) {
                        errorText = "Nominal harus lebih besar dari 0"
                    } else {
                        onConfirm(selectedType, nameInput.trim(), amount, notesInput.trim(), contactInput.trim(), calculatedDueDate)
                    }
                },
                modifier = Modifier.testTag("save_debt_confirm_button")
            ) {
                Text("Simpan (Terenkripsi)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
