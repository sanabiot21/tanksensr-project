package com.example.mvpapp.presenter

import com.example.mvpapp.View.IRegisterView
import com.example.mvpapp.model.User
import com.example.mvpapp.model.UserRepository

class RegisterPresenter(
    private val view: IRegisterView,
    private val repository: UserRepository
) : IRegisterPresenter {

    override fun handleRegister(
        firstName: String,
        middleName: String,
        lastName: String,
        suffix: String,
        address: String,
        birthdate: String,
        email: String,
        password: String
    ) {
        // Create User object
        val user = User(
            firstName = firstName.trim(),
            middleName = middleName.trim(),
            lastName = lastName.trim(),
            suffix = if (suffix == "Select suffix (optional)") "" else suffix.trim(),
            address = address.trim(),
            birthdate = birthdate.trim(),
            email = email.trim().toLowerCase(),
            password = password
        )

        val success = repository.registerUser(user)
        if (success) {
            view.showRegisterSuccess("User registered successfully!")
        } else {
            view.showRegisterError("Email already exists")
        }
    }
}
