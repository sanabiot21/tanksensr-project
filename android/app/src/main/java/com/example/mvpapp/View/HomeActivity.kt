package com.example.mvpapp.View

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mvpapp.R
import com.example.mvpapp.model.User
import com.example.mvpapp.model.UserRepository
import com.example.mvpapp.presenter.HomePresenter

class HomeActivity : Activity(), IHomeView {

    private lateinit var presenter: HomePresenter
    private lateinit var tvWaterQualityScore: TextView
    private lateinit var tvTurbidity: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvPH: TextView
    
    // Action buttons
    private lateinit var btnViewTrends: Button
    private lateinit var btnExportData: Button
    
    // Navigation
    private lateinit var btnHome: LinearLayout
    private lateinit var btnData: LinearLayout
    private lateinit var btnAlerts: LinearLayout
    private lateinit var btnSettings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val repository = UserRepository()
        presenter = HomePresenter(this, repository)

        initializeViews()
        setupClickListeners()
        presenter.loadSensorData()
    }

    private fun initializeViews() {
        tvWaterQualityScore = findViewById(R.id.tvWaterQualityScore)
        tvTurbidity = findViewById(R.id.tvTurbidity)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvPH = findViewById(R.id.tvPH)
        
        btnViewTrends = findViewById(R.id.btnViewTrends)
        btnExportData = findViewById(R.id.btnExportData)
        
        btnHome = findViewById(R.id.btnHome)
        btnData = findViewById(R.id.btnData)
        btnAlerts = findViewById(R.id.btnAlerts)
        btnSettings = findViewById(R.id.btnSettings)
    }

    private fun setupClickListeners() {
        btnViewTrends.setOnClickListener {
            Toast.makeText(this, "View Trends - Feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnExportData.setOnClickListener {
            Toast.makeText(this, "Export Data - Feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnData.setOnClickListener {
            Toast.makeText(this, "Data View - Feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnAlerts.setOnClickListener {
            Toast.makeText(this, "Alerts - Feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(this, "Settings - Feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showUsers(users: List<User>) {
        // Display mock sensor data based on the home.png mockup
        tvWaterQualityScore.text = "98.2%"
        tvTurbidity.text = "2.1"
        tvTemperature.text = "22.5"
        tvPH.text = "6.8"
    }
}
