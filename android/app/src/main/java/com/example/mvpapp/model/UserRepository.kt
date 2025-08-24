package com.example.mvpapp.model

class UserRepository {
    private val users = mutableListOf<User>()

    init {
        // Add a default user for testing
        users.add(User(
            firstName = "Test",
            middleName = "",
            lastName = "User",
            suffix = "",
            address = "123 Test Street",
            birthdate = "01/01/1990",
            email = "test@example.com",
            password = "password123"
        ))
    }

    fun registerUser(user: User): Boolean {
        if (users.any { it.email.toLowerCase() == user.email.toLowerCase() }) {
            return false // email already exists
        }
        users.add(user)
        return true
    }

    fun validateUser(email: String, password: String): Boolean {
        return users.any { it.email.toLowerCase() == email.toLowerCase() && it.password == password }
    }

    fun getAllUsers(): List<User> {
        return users
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email.toLowerCase() == email.toLowerCase() }
    }
}
