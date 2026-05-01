package com.example.biogeo_check.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.biogeo_check.R
import com.example.biogeo_check.data.model.FichajeEvent
import com.example.biogeo_check.data.model.FichajeType
import com.example.biogeo_check.databinding.ItemFichajeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FichajeAdapter(
    private var items: List<FichajeEvent> = emptyList()
) : RecyclerView.Adapter<FichajeAdapter.FichajeViewHolder>() {

    fun updateItems(newItems: List<FichajeEvent>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FichajeViewHolder {
        val binding = ItemFichajeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FichajeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FichajeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class FichajeViewHolder(
        private val binding: ItemFichajeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

        fun bind(event: FichajeEvent) {
            val context = binding.root.context

            binding.tvType.text = when (event.type) {
                FichajeType.ENTRADA -> context.getString(R.string.history_type_entrada)
                FichajeType.SALIDA -> context.getString(R.string.history_type_salida)
            }

            binding.tvTimestamp.text = dateFormat.format(Date(event.timestamp))
            binding.tvLocation.text = "📍 ${event.sedeName.ifEmpty { "Sin sede" }}"

            val indicatorColor = when (event.type) {
                FichajeType.ENTRADA -> R.color.status_entrada
                FichajeType.SALIDA -> R.color.status_salida
            }
            binding.viewTypeIndicator.setBackgroundColor(
                ContextCompat.getColor(context, indicatorColor)
            )
        }
    }
}
