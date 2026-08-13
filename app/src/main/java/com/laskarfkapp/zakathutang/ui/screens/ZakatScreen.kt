package com.laskarfkapp.zakathutang.ui.screens

import android.app.Activity
import android.widget.Toast
import com.laskarfkapp.zakathutang.admob.AdManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.laskarfkapp.zakathutang.ui.components.ZakatSummaryCard
import com.laskarfkapp.zakathutang.viewmodel.MainViewModel

@Composable
fun ZakatScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val selectedTab by viewModel.zakatTabSelected.collectAsState()

    val tabTitles = listOf(
        "1. Profesi",
        "2. Maal",
        "3. Fitrah",
        "4. Perdagangan",
        "5. Pertanian",
        "6. Peternakan",
        "7. Tabungan & Saham"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.zakatTabSelected.value = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("zakat_tab_$index")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        ZakatProfesiForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatProfesi()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Profesi", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                1 -> {
                    item {
                        ZakatMaalForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatMaal()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Maal", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                2 -> {
                    item {
                        ZakatFitrahForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatFitrah()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Fitrah", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                3 -> {
                    item {
                        ZakatPerdaganganForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatPerdagangan()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Perdagangan", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                4 -> {
                    item {
                        ZakatPertanianForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatPertanian()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Pertanian", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                5 -> {
                    item {
                        ZakatPeternakanForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatPeternakan()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Peternakan", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
                6 -> {
                    item {
                        ZakatTabunganSahamForm(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        val result = viewModel.calculateZakatTabunganSaham()
                        val activity = context as? Activity
                        ZakatSummaryCard(
                            result = result,
                            onSaveRecord = { res ->
                                viewModel.saveZakatCalculationToHistory(res)
                                Toast.makeText(context, "Berhasil menyimpan catatan Zakat Tabungan & Saham", Toast.LENGTH_SHORT).show()
                                activity?.let { act ->
                                    AdManager.showInterstitialIfAllowed(act) {}
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ZakatProfesiForm(viewModel: MainViewModel) {
    val salary by viewModel.salaryInput.collectAsState()
    val bonus by viewModel.bonusInput.collectAsState()
    val expense by viewModel.expenseInput.collectAsState()
    val isNet by viewModel.isNetProfesi.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Penghasilan / Profesi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = salary,
                onValueChange = { viewModel.salaryInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Gaji / Penghasilan Rutin Bulanan (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salary_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bonus,
                onValueChange = { viewModel.bonusInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Bonus / Tunjangan / Lain-lain (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isNet,
                    onCheckedChange = { viewModel.isNetProfesi.value = it },
                    modifier = Modifier.testTag("net_profesi_checkbox")
                )
                Text(
                    text = "Kurangi dengan pengeluaran kebutuhan pokok bulanan (Netto)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isNet) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = expense,
                    onValueChange = { viewModel.expenseInput.value = it.filter { c -> c.isDigit() } },
                    label = { Text("Pengeluaran Kebutuhan Pokok Bulanan (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ZakatMaalForm(viewModel: MainViewModel) {
    val savings by viewModel.savingsInput.collectAsState()
    val gold by viewModel.goldValueInput.collectAsState()
    val investment by viewModel.investmentInput.collectAsState()
    val debt by viewModel.shortDebtInput.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Maal (Tabungan & Harta)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Nisab: 85 gram emas dengan masa kepemilikan (haul) 1 tahun",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = savings,
                onValueChange = { viewModel.savingsInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Uang Tabungan / Kas / Deposito (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gold,
                onValueChange = { viewModel.goldValueInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Emas / Perak / Perhiasan Tersimpan (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = investment,
                onValueChange = { viewModel.investmentInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Saham / Reksadana / Surat Berharga (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = debt,
                onValueChange = { viewModel.shortDebtInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Hutang Jatuh Tempo (Pengurang Harta) (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ZakatFitrahForm(viewModel: MainViewModel) {
    val members by viewModel.familyMembersCount.collectAsState()
    val nisabRates by viewModel.nisabRates.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Fitrah Keluarga",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Kadar standar: 2.5 kg beras (atau 3.5 liter) per jiwa",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = members,
                onValueChange = { viewModel.familyMembersCount.value = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Anggota Keluarga (Jiwa)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("family_members_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Harga Beras Acuan: Rp ${nisabRates.ricePricePerKg.toLong()} / kg (Setara Rp ${nisabRates.fitrahPerPersonMoney.toLong()} / jiwa)",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun ZakatPerdaganganForm(viewModel: MainViewModel) {
    val capital by viewModel.tradeCapitalInput.collectAsState()
    val receivables by viewModel.tradeReceivableInput.collectAsState()
    val payables by viewModel.tradePayableInput.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Perdagangan / Usaha",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = capital,
                onValueChange = { viewModel.tradeCapitalInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Modal Diputar / Stok Aset Dagang (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = receivables,
                onValueChange = { viewModel.tradeReceivableInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Piutang Dagang Lancar (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = payables,
                onValueChange = { viewModel.tradePayableInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Hutang Dagang Jatuh Tempo (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ZakatPertanianForm(viewModel: MainViewModel) {
    val harvest by viewModel.harvestAmountInput.collectAsState()
    val isIrigasi by viewModel.isIrigasiBerbiaya.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Pertanian & Panen",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Nisab: 5 Wasaq (653 kg beras/gabah bersih) dikeluarkan saat panen.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = harvest,
                onValueChange = { viewModel.harvestAmountInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Hasil Panen Bersih (kg Beras / Gabah)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isIrigasi,
                    onCheckedChange = { viewModel.isIrigasiBerbiaya.value = it }
                )
                Text(
                    text = if (isIrigasi) "Pengairan Berbiaya (Irigasi/Pompa 5%)" else "Pengairan Alami (Tadah Hujan/Sungai 10%)",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun ZakatPeternakanForm(viewModel: MainViewModel) {
    val cows by viewModel.cowCountInput.collectAsState()
    val goats by viewModel.goatCountInput.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Peternakan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Syarat: Digembalakan di padang rumput bebas (saimah) & haul 1 tahun.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cows,
                onValueChange = { viewModel.cowCountInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Sapi / Kerbau (Ekor - Nisab 30)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = goats,
                onValueChange = { viewModel.goatCountInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Kambing / Domba (Ekor - Nisab 40)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ZakatTabunganSahamForm(viewModel: MainViewModel) {
    val savings by viewModel.bankSavingsInput.collectAsState()
    val stocks by viewModel.stockMarketInput.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hitung Zakat Tabungan & Saham / Reksadana",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Nisab: Setara 85 gram emas dengan haul 1 tahun (Tarif 2.5%).",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = savings,
                onValueChange = { viewModel.bankSavingsInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Saldo Tabungan / Deposito (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stocks,
                onValueChange = { viewModel.stockMarketInput.value = it.filter { c -> c.isDigit() } },
                label = { Text("Nilai Pasar Saham / Reksadana Syariah (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
