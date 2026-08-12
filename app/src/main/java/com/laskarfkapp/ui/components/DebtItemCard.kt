package com.laskarfkapp.zakathutang.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laskarfkapp.zakathutang.model.DebtRecord
import com.laskarfkapp.zakathutang.model.DebtType
import com.laskarfkapp.zakathutang.util.FormatUtils

@Composable
fun DebtItemCard(
    record: DebtRecord,
    onPayClick: (DebtRecord) -> Unit,
    onDeleteClick: (DebtRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isHutang = record.type == DebtType.HUTANG

    val badgeBg = if (isHutang) Color(0xFFD32F2F) else Color(0xFF00796B)
    val badgeLabel = if (isHutang) "Hutang (Saya Berhutang)" else "Piutang (Orang Berhutang)"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (record.isPaid) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Type Badge & Action Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = badgeBg
                ) {
                    Text(
                        text = badgeLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { shareDebtSummary(context, record) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Ringkasan",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onDeleteClick(record) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contact Name & Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = record.personName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (record.contact.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = record.contact,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                if (record.isPaid) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LUNAS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount & Installment Progress
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Total Nominal:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = FormatUtils.formatRupiah(record.amount),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Sisa Wajib Bayar:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = FormatUtils.formatRupiah(record.remainingAmount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (record.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { record.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isHutang) Color(0xFFD32F2F) else Color(0xFF00796B),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = "Tercicil: ${FormatUtils.formatRupiah(record.paidAmount)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                )
                if (record.dueDate > 0) {
                    Text(
                        text = "Jatuh Tempo: ${FormatUtils.formatDate(record.dueDate)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            if (record.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${record.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (!record.isPaid) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { onPayClick(record) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_installment_button_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cicil / Lunas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { sendIslamicReminder(context, record) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("send_reminder_button_${record.id}")
                    ) {
                        Text(
                            text = "💬 Pengingat WA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun sendIslamicReminder(context: Context, record: DebtRecord) {
    val dueStr = if (record.dueDate > 0) FormatUtils.formatDate(record.dueDate) else "-"
    val isHutang = record.type == DebtType.HUTANG

    val message = if (isHutang) {
        """
            Assalamu'alaikum Warahmatullahi Wabarakatuh ${record.personName},
            
            Sekadar konfirmasi terkait catatan hutang saya sebesar ${FormatUtils.formatRupiah(record.remainingAmount)} (Jatuh tempo: $dueStr). Insya Allah saya berkomitmen melunasi sesuai kesepakatan. Semoga Allah mempermudah rezeki kita. Aamiin.
            
            _Dikirim via Aplikasi Zakat & Hutang_
        """.trimIndent()
    } else {
        """
            Assalamu'alaikum Warahmatullahi Wabarakatuh ${record.personName},
            
            Semoga ${record.personName} dan keluarga senantiasa sehat dan penuh keberkahan. Sekadar mengingatkan terkait catatan piutang sebesar ${FormatUtils.formatRupiah(record.remainingAmount)} yang jatuh tempo pada $dueStr. Jika ada kendala, silakan kabari ya. Semoga Allah mempermudah segala urusan kita. Aamiin.
            
            _Dikirim via Aplikasi Zakat & Hutang_
        """.trimIndent()
    }

    // Direct WhatsApp sending if phone number is present in record.contact
    val rawContact = record.contact.trim().filter { it.isDigit() || it == '+' }
    val formattedPhone = when {
        rawContact.startsWith("+62") -> rawContact.substring(1)
        rawContact.startsWith("08") -> "62" + rawContact.substring(1)
        rawContact.startsWith("628") -> rawContact
        else -> null
    }

    if (!formattedPhone.isNullOrBlank()) {
        try {
            val encodedMsg = Uri.encode(message)
            val waUri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMsg")
            val waIntent = Intent(Intent.ACTION_VIEW, waUri)
            context.startActivity(waIntent)
            return
        } catch (_: Exception) {
            // Fallback to standard chooser if WhatsApp deep link fails
        }
    }

    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Kirim Pengingat Syariah via WhatsApp/Pesan"))
}

private fun shareDebtSummary(context: Context, record: DebtRecord) {
    val typeTitle = if (record.type == DebtType.HUTANG) "Catatan Hutang Saya" else "Catatan Piutang"
    val summary = """
        📌 *$typeTitle*
        Nama Kontak: ${record.personName}
        Total Nominal: ${FormatUtils.formatRupiah(record.amount)}
        Sudah Dibayar: ${FormatUtils.formatRupiah(record.paidAmount)}
        Sisa Pembayaran: ${FormatUtils.formatRupiah(record.remainingAmount)}
        Status: ${if (record.isPaid) "LUNAS" else "BELUM LUNAS"}
        ${if (record.dueDate > 0) "Jatuh Tempo: " + FormatUtils.formatDate(record.dueDate) else ""}
        ${if (record.notes.isNotEmpty()) "Catatan: " + record.notes else ""}
        
        _Dicatat secara terenkripsi via Aplikasi Zakat & Hutang_
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, summary)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Catatan Transaksi")
    context.startActivity(shareIntent)
}
