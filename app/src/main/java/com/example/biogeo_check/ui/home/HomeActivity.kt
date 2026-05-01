package com.example.biogeo_check.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.biogeo_check.R
import com.example.biogeo_check.databinding.ActivityHomeBinding
import com.example.biogeo_check.domain.model.FichajeState
import com.example.biogeo_check.ui.admin.AdminActivity
import com.example.biogeo_check.ui.history.HistoryActivity
import com.example.biogeo_check.ui.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDate()
        setupListeners()
        observeViewModel()
        setupBottomNav()

        // Simulate location check
        viewModel.onLocationResult(true)
    }

    private fun setupDate() {
        val sdf = SimpleDateFormat("d 'de' MMMM, yyyy", Locale("es", "ES"))
        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupListeners() {
        binding.btnFichajeContainer.setOnClickListener {
            viewModel.realizarFichaje()
        }

        binding.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, getString(R.string.nav_logout))
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        // TODO: Clear session
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun observeViewModel() {
        viewModel.userName.observe(this) { name ->
            binding.tvGreeting.text = getString(R.string.home_greeting, name)
        }

        viewModel.fichajeState.observe(this) { state ->
            when (state) {
                FichajeState.SIN_FICHAR -> {
                    binding.tvCurrentStatus.text = getString(R.string.status_sin_fichar)
                    binding.tvFichajeLabel.text = getString(R.string.btn_fichar_entrada)
                    binding.btnFichajeCircle.setImageResource(R.drawable.bg_circle_entrada)
                    binding.tvFichajeIcon.text = "👆"
                    binding.btnFichajeContainer.isEnabled = true
                }
                FichajeState.ENTRADA_FICHADA -> {
                    binding.tvCurrentStatus.text = getString(R.string.status_fichado_entrada)
                    binding.tvFichajeLabel.text = getString(R.string.btn_fichar_salida)
                    binding.btnFichajeCircle.setImageResource(R.drawable.bg_circle_salida)
                    binding.tvFichajeIcon.text = "👋"
                    binding.btnFichajeContainer.isEnabled = true
                }
                FichajeState.SALIDA_FICHADA -> {
                    binding.tvCurrentStatus.text = getString(R.string.status_fichado_salida)
                    binding.tvFichajeLabel.text = "Completado"
                    binding.tvFichajeIcon.text = "✅"
                    binding.btnFichajeContainer.isEnabled = false
                    binding.btnFichajeContainer.alpha = 0.5f
                }
                null -> {}
            }
        }

        viewModel.lastFichajeTime.observe(this) { time ->
            binding.tvLastFichaje.text = "Último fichaje: $time"
        }

        viewModel.isLocationLoading.observe(this) { loading ->
            binding.progressLocation.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.isLocationValid.observe(this) { valid ->
            when (valid) {
                true -> binding.tvLocationStatus.text = getString(R.string.location_status_ok)
                false -> binding.tvLocationStatus.text = getString(R.string.location_status_error)
                null -> binding.tvLocationStatus.text = getString(R.string.location_status_loading)
            }
        }

        viewModel.fichajeConfirmation.observe(this) { msg ->
            if (msg != null) {
                binding.tvFichajeConfirmation.text = msg
                binding.tvFichajeConfirmation.visibility = View.VISIBLE
            } else {
                binding.tvFichajeConfirmation.visibility = View.GONE
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_admin -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
