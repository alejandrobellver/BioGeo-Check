package com.example.biogeo_check.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.biogeo_check.databinding.ActivityRegisterBinding
import com.example.biogeo_check.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.etName.addTextChangedListener { viewModel.setName(it.toString()) }
        binding.etEmail.addTextChangedListener { viewModel.setEmail(it.toString()) }
        binding.etPassword.addTextChangedListener { viewModel.setPassword(it.toString()) }
        binding.etConfirmPassword.addTextChangedListener { viewModel.setConfirmPassword(it.toString()) }

        binding.btnRegister.setOnClickListener {
            viewModel.validateAndRegister()
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Vuelve a login
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !loading
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
