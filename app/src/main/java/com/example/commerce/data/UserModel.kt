package com.example.commerce.data

data class UserModel(
    val id : String?,
    val email: String?,
    val password:String?,
    var position: String? = "Owner",)

    {
        constructor(): this("","","","")
    }





