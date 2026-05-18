package com.example.tagpaw.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,             // 최대 3자 (예: 화멍이)
    val age: String,              // "3살"
    val emergencyPhone: String,
    val emergencyNote: String,    // 최대 30자
    val pin: String,              // 4자리 숫자 비밀번호
    val tagUid: String? = null    // NFC 태그 UID, 태그 연결 전에는 null
)