package com.example.tagpaw.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import androidx.compose.runtime.NoLiveLiterals
import java.security.MessageDigest

@NoLiveLiterals
object NfcUtils {

    // TAG UID 읽기
    fun getTagUidHex(tag: Tag?): String? {
        val id = tag?.id ?: return null
        return id.joinToString(separator = "") { byte -> "%02X".format(byte) }
    }

    // PIN 해시 생성 (SHA-256 앞 8자리)
    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }.take(8)
    }

    // NFC 태그 쓰기 - connect/close 내부에서 처리
    fun writeNdefRecords(
        tag: Tag?,
        phone: String,
        name: String,
        age: String,
        note: String,
        pin: String
    ): Boolean {
        tag ?: return false

        val formattedPhone = if (phone.length == 11) {
            "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}"
        } else phone

        // 레코드 1 - SMS URI
        val smsBody = "$name(${age}살) / $note"
        val smsUri = "sms:$formattedPhone?body=$smsBody"
        val smsRecord = NdefRecord.createUri(smsUri)

        // 레코드 2 - PIN 해시
        val pinHash = hashPin(pin)
        val lang = "en"
        val langBytes = lang.toByteArray(Charsets.US_ASCII)
        val pinBytes = pinHash.toByteArray(Charsets.UTF_8)
        val pinPayload = ByteArray(1 + langBytes.size + pinBytes.size)
        pinPayload[0] = langBytes.size.toByte()
        System.arraycopy(langBytes, 0, pinPayload, 1, langBytes.size)
        System.arraycopy(pinBytes, 0, pinPayload, 1 + langBytes.size, pinBytes.size)

        val pinRecord = NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            ByteArray(0),
            pinPayload
        )

        val message = NdefMessage(arrayOf(smsRecord, pinRecord))

        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) return false
                ndef.writeNdefMessage(message)
                true
            } else {
                val formatable = NdefFormatable.get(tag) ?: return false
                formatable.connect()
                formatable.format(message)
                true
            }
        } catch (e: Exception) {
            false
        } finally {
            try { Ndef.get(tag)?.close() } catch (_: Exception) {}
        }
    }
}