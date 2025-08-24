package com.example.mvpapp.View

import com.example.mvpapp.model.User

interface IHomeView {
    fun showUsers(users: List<User>)
}
