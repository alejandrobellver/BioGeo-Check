package com.example.biogeo_check.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biogeo_check.R
import com.example.biogeo_check.databinding.ActivityAdminBinding
import com.example.biogeo_check.ui.history.FichajeAdapter
import com.example.biogeo_check.ui.home.HomeActivity
import com.example.biogeo_check.ui.history.HistoryActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val viewModel: AdminViewModel by viewModels()
    private val adapter = FichajeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        setupBottomNav()
    }

    private fun setupRecyclerView() {
        binding.rvAllFichajes.layoutManager = LinearLayoutManager(this)
        binding.rvAllFichajes.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnExportCsv.setOnClickListener {
            viewModel.exportCsv()
            Toast.makeText(this, "Exportando CSV...", Toast.LENGTH_SHORT).show()
        }

        binding.btnExportPdf.setOnClickListener {
            viewModel.exportPdf()
            Toast.makeText(this, "Exportando PDF...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.allFichajes.observe(this) { list ->
            adapter.updateItems(list)
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_admin
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_admin -> true
                else -> false
            }
        }
    }
}
