package com.laskarfkapp.zakathutang.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"
    private const val HASH_ALGORITHM = "SHA-256"

    /**
     * Hash PIN with salt for authentication.
     */
    fun hashPin(pin: String, salt: String = "ZAKAT_HUTANG_SALT_2026"): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val combined = "$salt:$pin:$salt"
        val bytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Generate 16-byte key spec from PIN.
     */
    private fun deriveKey(pin: String): SecretKeySpec {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val keyBytes = digest.digest("KEY_SALT_$pin".toByteArray(Charsets.UTF_8))
        // Take first 16 bytes for AES-128 or 32 bytes for AES-256
        val secretKeyBytes = keyBytes.copyOf(16)
        return SecretKeySpec(secretKeyBytes, KEY_ALGORITHM)
    }

    /**
     * Encrypt text string with user PIN.
     * Returns Base64 encoded IV + CipherText.
     */
    fun encrypt(plainText: String, pin: String): String {
        if (plainText.isEmpty()) return ""
        try {
            val key = deriveKey(pin)
            val cipher = Cipher.getInstance(AES_ALGORITHM)
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Combine IV and encrypted bytes
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return plainText // fallback if crypto error occurs
        }
    }

    /**
     * Decrypt Base64 encoded IV + CipherText using user PIN.
     */
    fun decrypt(encryptedText: String, pin: String): String {
        if (encryptedText.isEmpty()) return ""
        try {
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)
            if (combined.size < 17) return encryptedText // invalid format fallback

            val iv = ByteArray(16)
            System.arraycopy(combined, 0, iv, 0, 16)
            val ivSpec = IvParameterSpec(iv)

            val encryptedBytes = ByteArray(combined.size - 16)
            System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.size)

            val key = deriveKey(pin)
            val cipher = Cipher.getInstance(AES_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            // Decryption failed (wrong PIN or corrupted)
            return "***[Terenkripsi]***"
        }
    }
}
