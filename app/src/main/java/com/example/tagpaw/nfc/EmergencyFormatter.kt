package com.example.tagpaw.nfc

data class EmergencyInfo(
    val phone: String,
    val note: String,
    val pin: String
)

fun buildEmergencyText(info: EmergencyInfo): String =
    "phone=${info.phone};note=${info.note};pin=${info.pin}"

fun parseEmergencyText(text: String): EmergencyInfo? {
    // "key=value;key2=value2" 형태를 파싱
    val map = text.split(";")
        .mapNotNull {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else null
        }
        .toMap()

    val phone = map["phone"] ?: return null
    val note = map["note"] ?: ""
    val pin = map["pin"] ?: return null

    return EmergencyInfo(phone, note, pin)
}
