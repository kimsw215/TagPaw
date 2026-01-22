package com.example.tagpaw.navigation

object TagPawRoutes {
    const val HOME = "home"
    const val ADD_PET = "add_pet"
    const val TAG_REGISTER = "tag_register/{petId}"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val EMERGENCY_EDIT = "emergency_edit/{petId}"

    fun tagRegister(petId: Long) = "tag_register/$petId"
    fun petDetail(petId: Long) = "pet_detail/$petId"
    fun emergencyEdit(petId: Long) = "emergency_edit/$petId"
}