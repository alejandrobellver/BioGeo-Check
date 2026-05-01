package com.example.biogeo_check.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biogeo_check.R
import com.example.biogeo_check.databinding.ActivityHistoryBinding
import com.example.biogeo_check.ui.admin.AdminActivity
import com.example.biogeo_check.ui.home.HomeActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter = FichajeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupBottomNav()
    }

    private fun setupRecyclerView() {
        binding.rvFichajes.layoutManager = LinearLayoutManager(this)
        binding.rvFichajes.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.fichajes.observe(this) { list ->
            adapter.updateItems(list)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(this) { empty ->
            binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
            binding.rvFichajes.visibility = if (empty) View.GONE else View.VISIBLE
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_history
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_history -> true
                R.id.nav_admin -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
