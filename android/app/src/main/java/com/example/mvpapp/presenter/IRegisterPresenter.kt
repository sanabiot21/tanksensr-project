package com.example.mvpapp.presenter

interface IRegisterPresenter {
    fun handleRegister(
        firstName: String,
        middleName: String,
        lastName: String,
        suffix: String,
        address: String,
        birthdate: String,
        email: String,
        password: String
    )
}
