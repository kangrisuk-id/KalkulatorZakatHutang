package com.laskarfkapp.zakathutang.data.repository

import com.laskarfkapp.zakathutang.data.dao.DebtDao
import com.laskarfkapp.zakathutang.data.entity.DebtEntity
import com.laskarfkapp.zakathutang.model.DebtRecord
import com.laskarfkapp.zakathutang.model.DebtType
import com.laskarfkapp.zakathutang.security.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DebtRepository(private val debtDao: DebtDao) {

    fun getDecryptedDebts(pin: String): Flow<List<DebtRecord>> {
        return debtDao.getAllDebts().map { list ->
            list.map { entity ->
                val name = CryptoUtils.decrypt(entity.personNameEncrypted, pin)
                val amountStr = CryptoUtils.decrypt(entity.amountEncrypted, pin)
                val paidStr = CryptoUtils.decrypt(entity.paidAmountEncrypted, pin)
                val notes = CryptoUtils.decrypt(entity.notesEncrypted, pin)
                val contact = CryptoUtils.decrypt(entity.contactEncrypted, pin)

                val amount = amountStr.toDoubleOrNull() ?: 0.0
                val paidAmount = paidStr.toDoubleOrNull() ?: 0.0
                val type = if (entity.type == "HUTANG") DebtType.HUTANG else DebtType.PIUTANG

                DebtRecord(
                    id = entity.id,
                    type = type,
                    personName = name,
                    amount = amount,
                    paidAmount = paidAmount,
                    notes = notes,
                    contact = contact,
                    dueDate = entity.dueDate,
                    createdAt = entity.createdAt,
                    isPaid = entity.isPaid || (paidAmount >= amount && amount > 0)
                )
            }
        }
    }

    suspend fun saveDebtRecord(record: DebtRecord, pin: String): Long {
        val nameEnc = CryptoUtils.encrypt(record.personName, pin)
        val amountEnc = CryptoUtils.encrypt(record.amount.toString(), pin)
        val paidEnc = CryptoUtils.encrypt(record.paidAmount.toString(), pin)
        val notesEnc = CryptoUtils.encrypt(record.notes, pin)
        val contactEnc = CryptoUtils.encrypt(record.contact, pin)

        val entity = DebtEntity(
            id = record.id,
            type = record.type.name,
            personNameEncrypted = nameEnc,
            amountEncrypted = amountEnc,
            paidAmountEncrypted = paidEnc,
            notesEncrypted = notesEnc,
            contactEncrypted = contactEnc,
            dueDate = record.dueDate,
            createdAt = if (record.createdAt > 0) record.createdAt else System.currentTimeMillis(),
            isPaid = record.isPaid
        )

        return debtDao.insertDebt(entity)
    }

    suspend fun updatePayment(debtId: Long, currentRecord: DebtRecord, newPaidTotal: Double, pin: String) {
        val isCompleted = newPaidTotal >= currentRecord.amount
        val updatedRecord = currentRecord.copy(
            paidAmount = newPaidTotal,
            isPaid = isCompleted
        )
        saveDebtRecord(updatedRecord, pin)
    }

    suspend fun deleteDebtById(id: Long) {
        debtDao.deleteDebtById(id)
    }
}
