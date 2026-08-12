package com.laskarfkapp.zakathutang.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laskarfkapp.zakathutang.admob.AdManager
import com.laskarfkapp.zakathutang.model.DebtRecord
import com.laskarfkapp.zakathutang.model.DebtType
import com.laskarfkapp.zakathutang.ui.components.AddDebtDialog
import com.laskarfkapp.zakathutang.ui.components.DebtItemCard
import com.laskarfkapp.zakathutang.ui.components.DebtSimulatorDialog
import com.laskarfkapp.zakathutang.ui.components.PayInstallmentDialog
import com.laskarfkapp.zakathutang.ui.components.PinLockScreen
import com.laskarfkapp.zakathutang.ui.components.SyariahEducationDialog
import com.laskarfkapp.zakathutang.util.ExportUtils
import com.laskarfkapp.zakathutang.util.FormatUtils
import com.laskarfkapp.zakathutang.viewmodel.MainViewModel

@Composable
fun DebtVaultScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val hasPinSet by viewModel.hasPinSet.collectAsState()
    val pinErrorMessage by viewModel.pinErrorMessage.collectAsState()
    val debts by viewModel.debts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSimulatorDialog by remember { mutableStateOf(false) }
    var showSyariahEduDialog by remember { mutableStateOf(false) }
    var selectedDebtForPay by remember { mutableStateOf<DebtRecord?>(null) }
    var filterTypeIndex by remember { mutableStateOf(0) } // 0: Semua, 1: Hutang, 2: Piutang, 3: Belum Lunas
    var searchQuery by remember { mutableStateOf("") }

    if (!isVaultUnlocked) {
        PinLockScreen(
            isFirstTime = !hasPinSet,
            errorMessage = pinErrorMessage,
            onPinSubmit = { pinAttempt ->
                viewModel.unlockVault(pinAttempt)
            },
            onResetError = {
                viewModel.resetPinErrorMessage()
            }
        )
        return
    }

    val totalHutang = debts.filter { it.type == DebtType.HUTANG && !it.isPaid }.sumOf { it.remainingAmount }
    val totalPiutang = debts.filter { it.type == DebtType.PIUTANG && !it.isPaid }.sumOf { it.remainingAmount }

    val filteredDebts = debts.filter { item ->
        val matchesSearch = item.personName.contains(searchQuery, ignoreCase = true) ||
                item.notes.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterTypeIndex) {
            1 -> item.type == DebtType.HUTANG
            2 -> item.type == DebtType.PIUTANG
            3 -> !item.isPaid
            4 -> item.isPaid
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_debt_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Catatan"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Lock Vault Header & Totals Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vault Terenkripsi Aktif",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.lockVault() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("lock_vault_button")
                        ) {
                            Text("🔒 Kunci Vault", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Sisa Hutang Saya (Wajib Bayar):",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = FormatUtils.formatRupiah(totalHutang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD32F2F)
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Sisa Piutang Saya (Akan Diterima):",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = FormatUtils.formatRupiah(totalPiutang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00796B)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val activity = context as? Activity
                    Button(
                        onClick = {
                            if (activity != null) {
                                Toast.makeText(context, "Menyiapkan Rewarded Video AdMob...", Toast.LENGTH_SHORT).show()
                                AdManager.showRewardedAd(
                                    activity = activity,
                                    onRewardEarned = {
                                        val reportText = ExportUtils.generateReportText(viewModel.zakatRecords.value, viewModel.debts.value)
                                        ExportUtils.shareReport(context, reportText)
                                        Toast.makeText(context, "🎁 Reward Fitur Premium! Laporan Berhasil Diekspor.", Toast.LENGTH_LONG).show()
                                    },
                                    onClosedOrFailed = {
                                        // Fallback allow export if ad fails to show or closed
                                        val reportText = ExportUtils.generateReportText(viewModel.zakatRecords.value, viewModel.debts.value)
                                        ExportUtils.shareReport(context, reportText)
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_report_rewarded_ad_button")
                    ) {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ekspor Laporan PDF/Text (Rewarded Video Ad)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showSimulatorDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_simulator_button")
                        ) {
                            Text("📊 Simulator Cicilan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = { showSyariahEduDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_syariah_edu_button")
                        ) {
                            Text("📖 Dalil Syariah", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari berdasarkan nama atau catatan...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_debt_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            val filters = listOf("Semua", "Hutang", "Piutang", "Belum Lunas", "Lunas")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters.size) { index ->
                    FilterChip(
                        selected = filterTypeIndex == index,
                        onClick = { filterTypeIndex = index },
                        label = { Text(filters[index]) },
                        modifier = Modifier.testTag("filter_chip_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of Debts
            if (filteredDebts.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum Ada Catatan Transaksi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tekan tombol '+' di bawah untuk membuat catatan hutang piutang terenkripsi.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(filteredDebts, key = { it.id }) { record ->
                        DebtItemCard(
                            record = record,
                            onPayClick = { debt -> selectedDebtForPay = debt },
                            onDeleteClick = { debt ->
                                viewModel.deleteDebt(debt.id)
                                Toast.makeText(context, "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        val activity = context as? Activity
        AddDebtDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, name, amount, notes, contact, dueDate ->
                viewModel.addOrUpdateDebt(
                    type = type,
                    personName = name,
                    amount = amount,
                    notes = notes,
                    contact = contact,
                    dueDate = dueDate
                )
                showAddDialog = false
                Toast.makeText(context, "Catatan tersimpan secara terenkripsi!", Toast.LENGTH_SHORT).show()
                activity?.let { act ->
                    AdManager.showInterstitialIfAllowed(act) {}
                }
            }
        )
    }

    selectedDebtForPay?.let { debt ->
        val activity = context as? Activity
        PayInstallmentDialog(
            record = debt,
            onDismiss = { selectedDebtForPay = null },
            onConfirmPay = { amountPaid ->
                viewModel.addPaymentInstallment(debt, amountPaid)
                selectedDebtForPay = null
                Toast.makeText(context, "Pembayaran cicilan berhasil dicatat!", Toast.LENGTH_SHORT).show()
                activity?.let { act ->
                    AdManager.showInterstitialIfAllowed(act) {}
                }
            }
        )
    }

    if (showSimulatorDialog) {
        DebtSimulatorDialog(
            onDismiss = { showSimulatorDialog = false }
        )
    }

    if (showSyariahEduDialog) {
        SyariahEducationDialog(
            onDismiss = { showSyariahEduDialog = false }
        )
    }
}
