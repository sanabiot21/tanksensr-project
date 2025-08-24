package com.example.mvpapp.presenter

import com.example.mvpapp.View.IHomeView
import com.example.mvpapp.model.UserRepository

class HomePresenter(
    private val view: IHomeView,
    private val repository: UserRepository
) : IHomePresenter {

    override fun loadUsers() {
        val users = repository.getAllUsers()
        view.showUsers(users)
    }

    override fun loadSensorData() {
        // For MVP demo purposes, we'll just load mock data via showUsers
        // In a real implementation, this would fetch sensor data from IoT devices
        val users = repository.getAllUsers()
        view.showUsers(users)
    }
}
