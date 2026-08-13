package com.laskarfkapp.zakathutang.util

import android.content.Context
import android.content.Intent
import com.laskarfkapp.zakathutang.data.entity.ZakatRecordEntity
import com.laskarfkapp.zakathutang.model.DebtRecord
import com.laskarfkapp.zakathutang.model.DebtType

object ExportUtils {

    fun generateReportText(
        zakatRecords: List<ZakatRecordEntity>,
        debts: List<DebtRecord>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("=======================================")
        sb.appendLine("     LAPORAN ZAKAT & HUTANG PIUTANG    ")
        sb.appendLine("=======================================")
        sb.appendLine("Tanggal Ekspor: ${FormatUtils.formatDate(System.currentTimeMillis())}")
        sb.appendLine()

        sb.appendLine("--- REKAPITULASI ZAKAT ---")
        if (zakatRecords.isEmpty()) {
            sb.appendLine("Belum ada riwayat zakat.")
        } else {
            val totalZakatPaid = zakatRecords.filter { it.isPaid }.sumOf { it.zakatAmount }
            val totalZakatUnpaid = zakatRecords.filter { !it.isPaid }.sumOf { it.zakatAmount }
            sb.appendLine("Total Zakat Sudah Ditunaikan : ${FormatUtils.formatRupiah(totalZakatPaid)}")
            sb.appendLine("Total Zakat Belum Ditunaikan : ${FormatUtils.formatRupiah(totalZakatUnpaid)}")
            sb.appendLine()
            sb.appendLine("Rincian Zakat:")
            zakatRecords.forEachIndexed { i, record ->
                val status = if (record.isPaid) "[LUNAS/SUDAH DITUNAIKAN]" else "[BELUM DITUNAIKAN]"
                sb.appendLine("${i + 1}. ${record.title} - ${FormatUtils.formatRupiah(record.zakatAmount)} $status")
            }
        }

        sb.appendLine()
        sb.appendLine("--- REKAPITULASI HUTANG & PIUTANG ---")
        val totalHutang = debts.filter { it.type == DebtType.HUTANG && !it.isPaid }.sumOf { it.remainingAmount }
        val totalPiutang = debts.filter { it.type == DebtType.PIUTANG && !it.isPaid }.sumOf { it.remainingAmount }
        sb.appendLine("Sisa Tanggungan Hutang : ${FormatUtils.formatRupiah(totalHutang)}")
        sb.appendLine("Sisa Tagihan Piutang   : ${FormatUtils.formatRupiah(totalPiutang)}")
        sb.appendLine()
        sb.appendLine("Rincian Transaksi:")
        if (debts.isEmpty()) {
            sb.appendLine("Belum ada catatan hutang/piutang.")
        } else {
            debts.forEachIndexed { i, debt ->
                val jenis = if (debt.type == DebtType.HUTANG) "HUTANG KE" else "PIUTANG DARI"
                val status = if (debt.isPaid) "[LUNAS]" else "[SISA: ${FormatUtils.formatRupiah(debt.remainingAmount)}]"
                sb.appendLine("${i + 1}. $jenis ${debt.personName} - Total: ${FormatUtils.formatRupiah(debt.amount)} $status")
            }
        }

        sb.appendLine()
        sb.appendLine("=======================================")
        sb.appendLine("Dicetak dari Aplikasi Zakat & Vault Hutang")
        return sb.toString()
    }

    fun shareReport(context: Context, reportText: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Ekspor & Bagikan Laporan Keuangan")
        context.startActivity(shareIntent)
    }
}
