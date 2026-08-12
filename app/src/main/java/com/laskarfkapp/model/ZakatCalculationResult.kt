package com.laskarfkapp.zakathutang.model

enum class ZakatType(val displayName: String) {
    PROFESI("Zakat Profesi / Penghasilan"),
    MAAL("Zakat Maal / Harta"),
    FITRAH("Zakat Fitrah"),
    PERDAGANGAN("Zakat Perdagangan"),
    PERTANIAN("Zakat Pertanian & Hasil Tani"),
    PETERNAKAN("Zakat Peternakan"),
    TABUNGAN_SAHAM("Zakat Tabungan & Saham")
}

data class ZakatCalculationResult(
    val type: ZakatType,
    val totalAssets: Double,
    val nisabThreshold: Double,
    val isNisabReached: Boolean,
    val zakatAmount: Double,
    val percentageRate: String,
    val explanationText: String,
    val breakdownSteps: List<Pair<String, String>>
)
