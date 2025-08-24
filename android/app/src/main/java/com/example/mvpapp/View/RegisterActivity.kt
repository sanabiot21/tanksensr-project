package com.example.mvpapp.View

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.mvpapp.R
import com.example.mvpapp.model.UserRepository
import com.example.mvpapp.presenter.RegisterPresenter
import java.util.*

class RegisterActivity : Activity(), IRegisterView {

    private lateinit var presenter: RegisterPresenter
    
    // Page 1 fields
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var spinnerSuffix: Spinner
    private lateinit var btnNext: Button
    private lateinit var llPage1: LinearLayout
    
    // Page 2 fields
    private lateinit var etAddress: EditText
    private lateinit var etBirthdate: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var llPage2: LinearLayout
    
    // Navigation
    private lateinit var btnLogin: Button
    private lateinit var tvAlreadyHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val repository = UserRepository()
        presenter = RegisterPresenter(this, repository)

        initializeViews()
        setupClickListeners()
        setupSuffixSpinner()
    }

    private fun initializeViews() {
        // Page 1
        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)
        spinnerSuffix = findViewById(R.id.spinnerSuffix)
        btnNext = findViewById(R.id.btnNext)
        llPage1 = findViewById(R.id.llPage1)
        
        // Page 2
        etAddress = findViewById(R.id.etAddress)
        etBirthdate = findViewById(R.id.etBirthdate)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        llPage2 = findViewById(R.id.llPage2)
        
        // Navigation
        btnLogin = findViewById(R.id.btnLogin)
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount)
    }

    private fun setupClickListeners() {
        btnNext.setOnClickListener {
            if (validatePage1()) {
                showPage2()
            }
        }

        btnRegister.setOnClickListener {
            if (validatePage2()) {
                presenter.handleRegister(
                    etFirstName.text.toString(),
                    etMiddleName.text.toString(),
                    etLastName.text.toString(),
                    spinnerSuffix.selectedItem.toString(),
                    etAddress.text.toString(),
                    etBirthdate.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        etBirthdate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupSuffixSpinner() {
        val suffixes = arrayOf("Select suffix (optional)", "Jr.", "Sr.", "II", "III", "IV", "V")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, suffixes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSuffix.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear)
                etBirthdate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validatePage1(): Boolean {
        if (etFirstName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etLastName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validatePage2(): Boolean {
        if (etAddress.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etBirthdate.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Birthdate is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etPassword.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etPassword.text.toString().length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "You must agree to the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showPage2() {
        llPage1.visibility = View.GONE
        llPage2.visibility = View.VISIBLE
    }

    override fun showRegisterSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showRegisterError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
