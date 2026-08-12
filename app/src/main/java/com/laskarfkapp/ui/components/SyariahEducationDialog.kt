package com.laskarfkapp.zakathutang.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyariahEducationDialog(
    onDismiss: () -> Unit
) {
    var selectedSubTab by remember { mutableStateOf(0) }
    val subTabs = listOf("1. Al-Qur'an & Hadits", "2. Adab Berhutang", "3. Tabel Nisab")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edukasi & Dalil Syariah",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedSubTab,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    subTabs.forEachIndexed { idx, title ->
                        Tab(
                            selected = selectedSubTab == idx,
                            onClick = { selectedSubTab = idx },
                            text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedSubTab == idx) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    when (selectedSubTab) {
                        0 -> {
                            item {
                                EdukasiCard(
                                    title = "QS. At-Taubah : 60 (8 Penerima Zakat / Asnaf)",
                                    content = "Sesungguhnya zakat-zakat itu hanyalah untuk orang-orang fakir, miskin, pengurus zakat, mualaf, budak, orang yang berhutang (Gharimin), fi sabilillah dan ibnu sabil."
                                )
                            }
                            item {
                                EdukasiCard(
                                    title = "QS. Al-Baqarah : 282 (Pencatatan Hutang)",
                                    content = "Wahai orang-orang yang beriman! Apabila kamu melakukan utang piutang untuk waktu yang ditentukan, hendaklah kamu menuliskannya secara tertulis."
                                )
                            }
                            item {
                                EdukasiCard(
                                    title = "QS. Al-Baqarah : 280 (Kemudahan Orang Kesulitan)",
                                    content = "Dan jika (orang yang berutang itu) dalam kesulitan, maka berilah tenggang waktu sampai dia memperoleh kelapangan."
                                )
                            }
                            item {
                                EdukasiCard(
                                    title = "HR. Bukhari No. 2287 (Ancaman Menunda Hutang)",
                                    content = "Penundaan (pembayaran utang) oleh orang yang mampu adalah suatu zalim."
                                )
                            }
                        }
                        1 -> {
                            item {
                                EdukasiCard(
                                    title = "1. Niatkan untuk Membayar",
                                    content = "Siapa saja yang mengambil harta orang lain (berutang) dengan niat untuk mengembalikannya, maka Allah akan membantu melunasinya. (HR. Bukhari)"
                                )
                            }
                            item {
                                EdukasiCard(
                                    title = "2. Menagih dengan Cara Santun",
                                    content = "Barangsiapa memberi penangguhan kepada orang yang kesulitan atau membebaskan utangnya, maka Allah akan menaunginya pada hari kiamat. (HR. Muslim)"
                                )
                            }
                            item {
                                EdukasiCard(
                                    title = "3. Segera Melunasi Saat Ada Rezeki",
                                    content = "Jangan menunda pembayaran hutang apabila sudah memiliki dana, karena menunda hutang bagi orang mampu adalah kezaliman."
                                )
                            }
                        }
                        2 -> {
                            item {
                                EdukasiCard(
                                    title = "Ringkasan Nisab & Kadar Zakat",
                                    content = "• Zakat Emas: Nisab 85g Emas, Haul 1 thn, Kadar 2.5%\n" +
                                            "• Zakat Profesi: Nisab ekuivalen 85g Emas/thn, Kadar 2.5%\n" +
                                            "• Zakat Fitrah: 2.5 kg beras per jiwa\n" +
                                            "• Zakat Pertanian: Nisab 5 Wasaq (653 kg beras), Kadar 5% / 10%\n" +
                                            "• Zakat Perdagangan: Modal + Piutang - Hutang ≥ 85g Emas, Kadar 2.5%"
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_syariah_edu_dialog")
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun EdukasiCard(title: String, content: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
            )
        }
    }
}
