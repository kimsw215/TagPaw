package com.example.tagpaw.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val sex: String,              // "M", "F" 또는 "수컷" 등 문자열
    val age: String,              // "3살", "2년" 같은 형식
    val emergencyPhone: String,
    val emergencyNote: String,    // "심장병, 흥분 주의" 정도의 짧은 메모
    val pin: String,              // 4~6자리 숫자 비밀번호

    // 앱 내부에서만 쓰는 필드들
    val photoUri: String? = null, // 나중에 넣을 수 있게만 해두고 지금은 안 써도 됨
    val tagUid: String? = null    // NFC 태그 UID, 태그 연결 전에는 null
)