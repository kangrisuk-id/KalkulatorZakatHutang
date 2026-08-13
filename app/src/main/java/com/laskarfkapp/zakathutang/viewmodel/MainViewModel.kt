package com.laskarfkapp.zakathutang.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.laskarfkapp.zakathutang.data.AppDatabase
import com.laskarfkapp.zakathutang.data.entity.ZakatRecordEntity
import com.laskarfkapp.zakathutang.data.repository.DebtRepository
import com.laskarfkapp.zakathutang.data.repository.NisabRates
import com.laskarfkapp.zakathutang.data.repository.ZakatRepository
import com.laskarfkapp.zakathutang.model.AppLanguage
import com.laskarfkapp.zakathutang.model.DebtRecord
import com.laskarfkapp.zakathutang.model.DebtType
import com.laskarfkapp.zakathutang.model.ZakatCalculationResult
import com.laskarfkapp.zakathutang.model.ZakatType
import com.laskarfkapp.zakathutang.security.CryptoUtils
import com.laskarfkapp.zakathutang.ui.theme.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val debtRepo = DebtRepository(db.debtDao())
    private val zakatRepo = ZakatRepository(db.zakatRecordDao(), db.nisabConfigDao())

    private val prefs = application.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    // Security & Lock State
    private val _hasPinSet = MutableStateFlow(prefs.contains("pin_hash"))
    val hasPinSet: StateFlow<Boolean> = _hasPinSet.asStateFlow()

    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _currentPin = MutableStateFlow("")
    val currentPin: StateFlow<String> = _currentPin.asStateFlow()

    private val _pinErrorMessage = MutableStateFlow<String?>(null)
    val pinErrorMessage: StateFlow<String?> = _pinErrorMessage.asStateFlow()

    // Navigation State
    private val _selectedTab = MutableStateFlow(0) // 0: Dashboard, 1: Zakat, 2: Hutang/Piutang, 3: History & Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Global Theme Mode State
    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun toggleThemeMode() {
        val next = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
        setThemeMode(next)
    }

    // Language State & RTL Support
    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("app_language", AppLanguage.INDONESIAN.code) ?: AppLanguage.INDONESIAN.code)
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.code).apply()
    }

    // Nisab Rates
    val nisabRates: StateFlow<NisabRates> = zakatRepo.nisabRates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NisabRates()
    )

    // Decrypted Debts State Flow (reactive to unlock PIN)
    @OptIn(ExperimentalCoroutinesApi::class)
    val debts: StateFlow<List<DebtRecord>> = combine(isVaultUnlocked, currentPin) { unlocked, pin ->
        Pair(unlocked, pin)
    }.flatMapLatest { (unlocked, pin) ->
        if (unlocked && pin.isNotEmpty()) {
            debtRepo.getDecryptedDebts(pin)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Zakat History Records
    val zakatRecords: StateFlow<List<ZakatRecordEntity>> = zakatRepo.allZakatRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Zakat Calculator Form State
    var zakatTabSelected = MutableStateFlow(0) // 0: Profesi, 1: Maal, 2: Fitrah, 3: Perdagangan

    // Inputs for Profesi
    val salaryInput = MutableStateFlow("10000000")
    val bonusInput = MutableStateFlow("0")
    val expenseInput = MutableStateFlow("0")
    val isNetProfesi = MutableStateFlow(false)

    // Inputs for Maal
    val savingsInput = MutableStateFlow("120000000")
    val goldValueInput = MutableStateFlow("0")
    val investmentInput = MutableStateFlow("0")
    val shortDebtInput = MutableStateFlow("0")

    // Inputs for Fitrah
    val familyMembersCount = MutableStateFlow("4")

    // Inputs for Perdagangan
    val tradeCapitalInput = MutableStateFlow("200000000")
    val tradeReceivableInput = MutableStateFlow("15000000")
    val tradePayableInput = MutableStateFlow("20000000")

    // Inputs for Pertanian
    val harvestAmountInput = MutableStateFlow("1000") // in kg gabah/beras
    val isIrigasiBerbiaya = MutableStateFlow(true) // true = 5% (irigasi/pompa berbiaya), false = 10% (hujan/alami)

    // Inputs for Peternakan
    val cowCountInput = MutableStateFlow("35") // Sapi/Kerbau
    val goatCountInput = MutableStateFlow("45") // Kambing/Domba

    // Inputs for Tabungan & Saham
    val bankSavingsInput = MutableStateFlow("150000000")
    val stockMarketInput = MutableStateFlow("50000000")

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // Security Methods
    fun changePin(oldPinAttempt: String, newPin: String): Boolean {
        val storedHash = prefs.getString("pin_hash", null)
        if (storedHash != null && CryptoUtils.hashPin(oldPinAttempt) != storedHash) {
            _pinErrorMessage.value = "PIN lama tidak sesuai"
            return false
        }
        if (newPin.length < 4) {
            _pinErrorMessage.value = "PIN baru minimal 4 digit angka"
            return false
        }
        val newHash = CryptoUtils.hashPin(newPin)
        prefs.edit().putString("pin_hash", newHash).apply()
        _currentPin.value = newPin
        _pinErrorMessage.value = null
        return true
    }

    // Security Methods
    fun setupPin(newPin: String) {
        if (newPin.length < 4) {
            _pinErrorMessage.value = "PIN minimal 4 digit angka"
            return
        }
        val hash = CryptoUtils.hashPin(newPin)
        prefs.edit().putString("pin_hash", hash).apply()
        _hasPinSet.value = true
        _currentPin.value = newPin
        _isVaultUnlocked.value = true
        _pinErrorMessage.value = null
    }

    fun unlockVault(pinAttempt: String): Boolean {
        val storedHash = prefs.getString("pin_hash", null)
        if (storedHash == null) {
            // First time, treat attempt as setup
            setupPin(pinAttempt)
            return true
        }

        val attemptHash = CryptoUtils.hashPin(pinAttempt)
        if (attemptHash == storedHash) {
            _currentPin.value = pinAttempt
            _isVaultUnlocked.value = true
            _pinErrorMessage.value = null
            return true
        } else {
            _pinErrorMessage.value = "PIN salah, silakan coba lagi"
            return false
        }
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
        _currentPin.value = ""
        _pinErrorMessage.value = null
    }

    fun resetPinErrorMessage() {
        _pinErrorMessage.value = null
    }

    // Zakat Calculations
    fun calculateZakatProfesi(): ZakatCalculationResult {
        val rates = nisabRates.value
        val salary = salaryInput.value.toDoubleOrNull() ?: 0.0
        val bonus = bonusInput.value.toDoubleOrNull() ?: 0.0
        val expense = expenseInput.value.toDoubleOrNull() ?: 0.0

        val totalIncome = salary + bonus
        val netIncome = if (isNetProfesi.value) (totalIncome - expense).coerceAtLeast(0.0) else totalIncome

        val monthlyNisab = rates.monthlyGoldNisab
        val isReached = netIncome >= monthlyNisab
        val zakatDue = if (isReached) netIncome * 0.025 else 0.0

        val steps = listOf(
            Pair("Total Penghasilan Bulan Ini", "Rp ${salary + bonus}"),
            Pair("Metode Perhitungan", if (isNetProfesi.value) "Penghasilan Bersih (dikurangi kebutuhan pokok)" else "Penghasilan Bruto"),
            Pair("Penghasilan Kena Zakat", "Rp $netIncome"),
            Pair("Nisab Bulanan (Emas 85g / 12)", "Rp ${monthlyNisab.toLong()}"),
            Pair("Status Nisab", if (isReached) "Wajib Zakat (Mencapai Nisab)" else "Belum Mencapai Nisab")
        )

        return ZakatCalculationResult(
            type = ZakatType.PROFESI,
            totalAssets = netIncome,
            nisabThreshold = monthlyNisab,
            isNisabReached = isReached,
            zakatAmount = zakatDue,
            percentageRate = "2.5%",
            explanationText = if (isReached)
                "Penghasilan bulanan Anda melebihi nisab bulanan ekuivalen 85 gram emas."
            else
                "Penghasilan Anda belum mencapai nisab bulanan (Rp ${monthlyNisab.toLong()}). Namun sangat dianjurkan untuk memperbanyak sedekah.",
            breakdownSteps = steps
        )
    }

    fun calculateZakatMaal(): ZakatCalculationResult {
        val rates = nisabRates.value
        val savings = savingsInput.value.toDoubleOrNull() ?: 0.0
        val gold = goldValueInput.value.toDoubleOrNull() ?: 0.0
        val investment = investmentInput.value.toDoubleOrNull() ?: 0.0
        val shortDebt = shortDebtInput.value.toDoubleOrNull() ?: 0.0

        val netWealth = (savings + gold + investment - shortDebt).coerceAtLeast(0.0)
        val annualNisab = rates.goldNisab85g
        val isReached = netWealth >= annualNisab
        val zakatDue = if (isReached) netWealth * 0.025 else 0.0

        val steps = listOf(
            Pair("Total Tabungan & Kas", "Rp $savings"),
            Pair("Nilai Emas & Perhiasan", "Rp $gold"),
            Pair("Nilai Investasi/Surat Berharga", "Rp $investment"),
            Pair("Hutang Jatuh Tempo (Pengurang)", "Rp $shortDebt"),
            Pair("Bersih Harta (Haul 1 Tahun)", "Rp $netWealth"),
            Pair("Nisab Tahunan (85g Emas)", "Rp ${annualNisab.toLong()}")
        )

        return ZakatCalculationResult(
            type = ZakatType.MAAL,
            totalAssets = netWealth,
            nisabThreshold = annualNisab,
            isNisabReached = isReached,
            zakatAmount = zakatDue,
            percentageRate = "2.5%",
            explanationText = if (isReached)
                "Total harta bersih milik Anda telah mencapai nisab 85g emas untuk haul 1 tahun."
            else
                "Harta bersih belum mencapai nisab 85 gram emas (Rp ${annualNisab.toLong()}).",
            breakdownSteps = steps
        )
    }

    fun calculateZakatFitrah(): ZakatCalculationResult {
        val rates = nisabRates.value
        val members = familyMembersCount.value.toIntOrNull() ?: 1
        val pricePerKg = rates.ricePricePerKg
        val kgPerPerson = 2.5
        val totalKg = members * kgPerPerson
        val totalMoney = totalKg * pricePerKg

        val steps = listOf(
            Pair("Jumlah Tanggungan Keluarga", "$members jiwa"),
            Pair("Kadar Beras per Jiwa", "$kgPerPerson kg (atau 3.5 liter)"),
            Pair("Total Beras Diperlukan", "$totalKg kg beras"),
            Pair("Harga Beras per Kg", "Rp ${pricePerKg.toLong()}")
        )

        return ZakatCalculationResult(
            type = ZakatType.FITRAH,
            totalAssets = members.toDouble(),
            nisabThreshold = 1.0,
            isNisabReached = true,
            zakatAmount = totalMoney,
            percentageRate = "$totalKg kg beras",
            explanationText = "Zakat Fitrah wajib ditunaikan sebelum shalat Idul Fitri untuk setiap anggota keluarga.",
            breakdownSteps = steps
        )
    }

    fun calculateZakatPerdagangan(): ZakatCalculationResult {
        val rates = nisabRates.value
        val capital = tradeCapitalInput.value.toDoubleOrNull() ?: 0.0
        val receivables = tradeReceivableInput.value.toDoubleOrNull() ?: 0.0
        val payables = tradePayableInput.value.toDoubleOrNull() ?: 0.0

        val netTradeAssets = (capital + receivables - payables).coerceAtLeast(0.0)
        val annualNisab = rates.goldNisab85g
        val isReached = netTradeAssets >= annualNisab
        val zakatDue = if (isReached) netTradeAssets * 0.025 else 0.0

        val steps = listOf(
            Pair("Modal Diputar / Stok Barang", "Rp $capital"),
            Pair("Piutang Dagang Lancar", "Rp $receivables"),
            Pair("Hutang Dagang Jatuh Tempo", "Rp $payables"),
            Pair("Aset Perdagangan Bersih", "Rp $netTradeAssets"),
            Pair("Nisab Perdagangan (85g Emas)", "Rp ${annualNisab.toLong()}")
        )

        return ZakatCalculationResult(
            type = ZakatType.PERDAGANGAN,
            totalAssets = netTradeAssets,
            nisabThreshold = annualNisab,
            isNisabReached = isReached,
            zakatAmount = zakatDue,
            percentageRate = "2.5%",
            explanationText = if (isReached)
                "Aset perdagangan lancar Anda sudah mencapai nisab ekuivalen 85g emas."
            else
                "Aset perdagangan belum mencapai nisab.",
            breakdownSteps = steps
        )
    }

    fun calculateZakatPertanian(): ZakatCalculationResult {
        val rates = nisabRates.value
        val harvestKg = harvestAmountInput.value.toDoubleOrNull() ?: 0.0
        val ricePrice = rates.ricePricePerKg
        val harvestValue = harvestKg * ricePrice

        // Nisab Tani: 5 Wasaq = 653 kg beras / gabah
        val nisabKg = 653.0
        val isReached = harvestKg >= nisabKg
        val ratePercent = if (isIrigasiBerbiaya.value) 0.05 else 0.10
        val rateLabel = if (isIrigasiBerbiaya.value) "5% (Irigasi Berbiaya / Pompa)" else "10% (Tadah Hujan / Alami)"
        val zakatDueValue = if (isReached) harvestValue * ratePercent else 0.0

        val steps = listOf(
            Pair("Hasil Panen", "$harvestKg kg (Nilai Rp ${harvestValue.toLong()})"),
            Pair("Nisab Pertanian (5 Wasaq)", "$nisabKg kg beras"),
            Pair("Sistem Pengairan", rateLabel),
            Pair("Status Nisab", if (isReached) "Wajib Zakat (Panen ≥ 653 kg)" else "Belum Mencapai Nisab (Panen < 653 kg)")
        )

        return ZakatCalculationResult(
            type = ZakatType.PERTANIAN,
            totalAssets = harvestValue,
            nisabThreshold = nisabKg * ricePrice,
            isNisabReached = isReached,
            zakatAmount = zakatDueValue,
            percentageRate = if (isIrigasiBerbiaya.value) "5%" else "10%",
            explanationText = if (isReached)
                "Hasil panen Anda mencapai nisab 5 Wasaq (653 kg beras). Wajib dikeluarkan zakatnya saat panen."
            else
                "Hasil panen belum mencapai nisab 653 kg beras.",
            breakdownSteps = steps
        )
    }

    fun calculateZakatPeternakan(): ZakatCalculationResult {
        val cows = cowCountInput.value.toIntOrNull() ?: 0
        val goats = goatCountInput.value.toIntOrNull() ?: 0

        val cowNisabReached = cows >= 30
        val goatNisabReached = goats >= 40

        val cowZakatNote = when {
            cows < 30 -> "Belum nisab (minimal 30 ekor)"
            cows in 30..39 -> "1 ekor sapi Tabi' (umur 1 tahun)"
            cows in 40..59 -> "1 ekor sapi Musinnah (umur 2 tahun)"
            else -> "${cows / 30} ekor sapi Tabi'"
        }

        val goatZakatNote = when {
            goats < 40 -> "Belum nisab (minimal 40 ekor)"
            goats in 40..120 -> "1 ekor kambing / domba"
            goats in 121..200 -> "2 ekor kambing / domba"
            else -> "3 ekor kambing / domba"
        }

        val isReached = cowNisabReached || goatNisabReached

        val steps = listOf(
            Pair("Jumlah Sapi / Kerbau", "$cows ekor -> Kadar Zakat: $cowZakatNote"),
            Pair("Jumlah Kambing / Domba", "$goats ekor -> Kadar Zakat: $goatZakatNote"),
            Pair("Nisab Ternak", "Sapi ≥ 30 ekor, Kambing ≥ 40 ekor (Syarat: Digembalakan/Saimah & 1 Tahun)")
        )

        return ZakatCalculationResult(
            type = ZakatType.PETERNAKAN,
            totalAssets = (cows + goats).toDouble(),
            nisabThreshold = 30.0,
            isNisabReached = isReached,
            zakatAmount = 0.0, // Zakat ternak ditunaikan dalam bentuk hewan ternak
            percentageRate = if (cowNisabReached) cowZakatNote else goatZakatNote,
            explanationText = if (isReached)
                "Hewan ternak Anda telah memenuhi nisab. Rincian zakat: Sapi ($cowZakatNote), Kambing ($goatZakatNote)."
            else
                "Jumlah ternak belum mencapai nisab (Sapi min 30 ekor, Kambing min 40 ekor).",
            breakdownSteps = steps
        )
    }

    fun calculateZakatTabunganSaham(): ZakatCalculationResult {
        val rates = nisabRates.value
        val savings = bankSavingsInput.value.toDoubleOrNull() ?: 0.0
        val stocks = stockMarketInput.value.toDoubleOrNull() ?: 0.0

        val total = savings + stocks
        val annualNisab = rates.goldNisab85g
        val isReached = total >= annualNisab
        val zakatDue = if (isReached) total * 0.025 else 0.0

        val steps = listOf(
            Pair("Saldo Tabungan / Deposito", "Rp $savings"),
            Pair("Nilai Pasar Saham / Reksadana", "Rp $stocks"),
            Pair("Total Aset Finansial", "Rp $total"),
            Pair("Nisab (Setara 85g Emas)", "Rp ${annualNisab.toLong()}")
        )

        return ZakatCalculationResult(
            type = ZakatType.TABUNGAN_SAHAM,
            totalAssets = total,
            nisabThreshold = annualNisab,
            isNisabReached = isReached,
            zakatAmount = zakatDue,
            percentageRate = "2.5%",
            explanationText = if (isReached)
                "Aset tabungan & saham tersimpan telah mencapai nisab 85 gram emas."
            else
                "Total aset tabungan & saham belum mencapai nisab 85 gram emas.",
            breakdownSteps = steps
        )
    }

    fun saveZakatCalculationToHistory(calc: ZakatCalculationResult) {
        viewModelScope.launch {
            val record = ZakatRecordEntity(
                type = calc.type.name,
                title = calc.type.displayName,
                totalAssets = calc.totalAssets,
                zakatAmount = calc.zakatAmount,
                nisabUsed = calc.nisabThreshold,
                isPaid = false,
                notes = "Diperhitungkan pada nisab Rp ${calc.nisabThreshold.toLong()}"
            )
            zakatRepo.saveZakatRecord(record)
        }
    }

    fun toggleZakatPaid(record: ZakatRecordEntity) {
        viewModelScope.launch {
            zakatRepo.togglePaidStatus(record)
        }
    }

    fun deleteZakatRecord(id: Long) {
        viewModelScope.launch {
            zakatRepo.deleteRecordById(id)
        }
    }

    // Debt Operations
    fun addOrUpdateDebt(
        id: Long = 0,
        type: DebtType,
        personName: String,
        amount: Double,
        notes: String,
        contact: String,
        dueDate: Long
    ) {
        val pin = _currentPin.value
        if (pin.isEmpty()) return

        viewModelScope.launch {
            val record = DebtRecord(
                id = id,
                type = type,
                personName = personName,
                amount = amount,
                paidAmount = 0.0,
                notes = notes,
                contact = contact,
                dueDate = dueDate,
                createdAt = System.currentTimeMillis(),
                isPaid = false
            )
            debtRepo.saveDebtRecord(record, pin)
        }
    }

    fun addPaymentInstallment(debtRecord: DebtRecord, amountPaid: Double) {
        val pin = _currentPin.value
        if (pin.isEmpty()) return

        viewModelScope.launch {
            val newTotalPaid = debtRecord.paidAmount + amountPaid
            debtRepo.updatePayment(debtRecord.id, debtRecord, newTotalPaid, pin)
        }
    }

    fun deleteDebt(id: Long) {
        viewModelScope.launch {
            debtRepo.deleteDebtById(id)
        }
    }

    // Nisab Configuration Updates
    fun updateGoldPrice(newPrice: Double) {
        viewModelScope.launch {
            zakatRepo.updateNisabRate("GOLD_PRICE_PER_GRAM", newPrice)
        }
    }

    fun updateSilverPrice(newPrice: Double) {
        viewModelScope.launch {
            zakatRepo.updateNisabRate("SILVER_PRICE_PER_GRAM", newPrice)
        }
    }

    fun updateRicePrice(newPrice: Double) {
        viewModelScope.launch {
            zakatRepo.updateNisabRate("RICE_PRICE_PER_KG", newPrice)
        }
    }
}
