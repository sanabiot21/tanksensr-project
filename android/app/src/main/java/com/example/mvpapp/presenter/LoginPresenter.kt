package com.example.mvpapp.presenter

import com.example.mvpapp.View.ILoginView
import com.example.mvpapp.model.UserRepository

class LoginPresenter(
    private val view: ILoginView,
    private val repository: UserRepository
) : ILoginPresenter {

    override fun handleLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showLoginError("Fields cannot be empty")
            return
        }

        val isValid = repository.validateUser(email, password)
        if (isValid) {
            val user = repository.getUserByEmail(email)
            val welcomeName = user?.firstName ?: "User"
            view.showLoginSuccess("Welcome, $welcomeName!")
        } else {
            view.showLoginError("Invalid credentials")
        }
    }
}
