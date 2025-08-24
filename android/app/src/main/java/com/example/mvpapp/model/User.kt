package com.example.mvpapp.model

data class User(
    val firstName: String,
    val middleName: String = "",
    val lastName: String,
    val suffix: String = "",
    val address: String,
    val birthdate: String,
    val email: String,
    val password: String
) {
    val fullName: String
        get() = listOf(firstName, middleName, lastName, suffix)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    
    val username: String
        get() = email
}

