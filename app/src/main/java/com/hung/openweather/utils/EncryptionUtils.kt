package com.hung.openweather.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

class EncryptionUtils private constructor(appContext: Context) {

    private var keyStore: KeyStore? = null
    private var context: Context = appContext

    init {
        initialiseKeys()
    }

    @Throws(Exception::class)
    private fun initialiseKeys() {
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore)
            keyStore!!.load(null)
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 30)
                val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(X500Principal("CN=${KEY_ALIAS}"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
                val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore)
                kpg.initialize(spec)
                kpg.generateKeyPair()
            }
            val pref: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            var encryptedKeyB64 = pref.getString(TOKEN_AES, null)
            if (encryptedKeyB64 == null) {
                val key = ByteArray(16)
                val secureRandom = SecureRandom()
                secureRandom.nextBytes(key)
                val encryptedKey: ByteArray = rsaEncrypt(key)
                encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
                val edit = pref.edit()
                edit.putString(TOKEN_AES, encryptedKeyB64)
                edit.commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun getSecretKey(context: Context): Key {
        val pref = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        var encryptedKeyB64 = pref.getString(TOKEN_AES, null)
        if (encryptedKeyB64 == null || encryptedKeyB64.isEmpty()) {
            initialiseKeys()
            encryptedKeyB64 = pref.getString(TOKEN_AES, null)
        }
        val encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT)
        val key: ByteArray = rsaDecrypt(encryptedKey)
        return SecretKeySpec(key, "AES")
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray): ByteArray {
        val inputCipher = getCipher()

        inputCipher!!.init(Cipher.ENCRYPT_MODE, keyStore!!.getCertificate(KEY_ALIAS).publicKey)
        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()
        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
        val privateKey = keyStore!!.getKey(KEY_ALIAS, null) as PrivateKey
        val output: Cipher? = getCipher()
        output?.init(Cipher.DECRYPT_MODE, privateKey)
        val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), output)
        val values = ArrayList<Byte>()
        var nextByte: Int
        while (cipherInputStream.read().also { nextByte = it } != -1) {
            values.add(nextByte.toByte())
        }
        cipherInputStream.close()
        val bytes = ByteArray(values.size)
        for (i in bytes.indices) {
            bytes[i] = values[i]
        }
        return bytes
    }

    @Throws(Exception::class)
    private fun getCipher(): Cipher? {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
            } else {
                Cipher.getInstance(RSA_MODE, "AndroidKeyStoreBCWorkaround")
            }
        } catch (exception: Exception) {
            throw RuntimeException("getCipher: Failed to get an instance of Cipher", exception)
        }
    }

    @Throws(Exception::class)
    fun encrypt(input: ByteArray?, key: Key?): String {
        val c = Cipher.getInstance(AES_MODE)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encodedBytes = c.doFinal(input)
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decrypt(encrypted: ByteArray?, key: Key?): ByteArray {
        val c = Cipher.getInstance(AES_MODE)
        c.init(Cipher.DECRYPT_MODE, key)
        return c.doFinal(encrypted)
    }

    @Throws(Exception::class)
    fun encrypt(plaintext: String): String {
        var key = getSecretKey(context)
        return encrypt(plaintext.toByteArray(Charset.defaultCharset()), key)
    }

    @Throws(Exception::class)
    fun decrypt(ciphertext: String?): String {
        var key = getSecretKey(context)
        return String(decrypt(Base64.decode(ciphertext, Base64.DEFAULT), key))
    }

    companion object {
        private const val TOKEN_AES = "token_aes"
        private const val AndroidKeyStore = "AndroidKeyStore"
        private const val KEY_ALIAS = "OPENWEATHERKEY"
        private const val RSA_MODE = "RSA/ECB/PKCS1Padding"
        private const val AES_MODE = "AES/ECB/PKCS5Padding"
        private const val SHARED_PREFERENCE_NAME = "open_weather"

        private var encryptionUtil: EncryptionUtils? = null
        fun getInstance(appContext: Context): EncryptionUtils {
            if (encryptionUtil == null) {
                encryptionUtil = EncryptionUtils(appContext)
            }
            return encryptionUtil!!
        }
    }
}