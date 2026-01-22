package com.example.tagpaw.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

object NfcUtils {

    fun getTagUidHex(tag: Tag?): String? {
        val id = tag?.id ?: return null
        return id.joinToString(separator = "") { byte -> "%02X".format(byte) }
    }

    fun readNdefText(tag: Tag?): String? {
        val ndef = Ndef.get(tag) ?: return null
        return try {
            ndef.connect()
            val message = ndef.ndefMessage ?: return null
            val record = message.records.firstOrNull() ?: return null
            val payload = record.payload

            val languageCodeLength = payload[0].toInt() and 0x3F
            String(payload, 1 + languageCodeLength, payload.size - 1 - languageCodeLength)
        } catch (e: Exception) {
            null
        } finally {
            try { ndef.close() } catch (_: Exception) {}
        }
    }

    fun writeNdefText(tag: Tag?, text: String): Boolean {
        tag ?: return false

        val lang = "en"
        val langBytes = lang.toByteArray(Charsets.US_ASCII)
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val payload = ByteArray(1 + langBytes.size + textBytes.size)

        payload[0] = langBytes.size.toByte()
        System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

        val record = NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            ByteArray(0),
            payload
        )
        val message = NdefMessage(arrayOf(record))

        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) return false
                ndef.writeNdefMessage(message)
                ndef.close()
                true
            } else {
                val formatable = NdefFormatable.get(tag) ?: return false
                formatable.connect()
                formatable.format(message)
                formatable.close()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    // 사람이 읽기 좋은 형식으로 변경 (PIN 제외)
    fun buildEmergencyText(
        name: String,
        sex: String,
        age: String,
        phone: String,
        note: String
    ): String {
        val formattedPhone = if (phone.length == 11) {
            "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}"
        } else phone

        return """
            [반려동물 비상 정보]
            이름: $name
            성별: $sex
            나이: ${age}살
            비상 연락처: $formattedPhone
            주의 사항: $note
        """.trimIndent()
    }
}
