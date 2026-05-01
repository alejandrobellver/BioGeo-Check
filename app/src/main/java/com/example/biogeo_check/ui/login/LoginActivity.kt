package com.example.biogeo_check.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.biogeo_check.databinding.ActivityLoginBinding
import com.example.biogeo_check.ui.home.HomeActivity
import com.example.biogeo_check.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.etEmail.addTextChangedListener { viewModel.setEmail(it.toString()) }
        binding.etPassword.addTextChangedListener { viewModel.setPassword(it.toString()) }

        binding.btnLogin.setOnClickListener {
            viewModel.validateAndLogin()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !loading
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}
