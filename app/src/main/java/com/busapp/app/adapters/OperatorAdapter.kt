package com.busapp.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.OperatorResponse
import com.busapp.app.databinding.ItemOperatorBinding

class OperatorAdapter(
    private var operators: List<OperatorResponse>,
    private val onEdit: (OperatorResponse) -> Unit,
    private val onDelete: (OperatorResponse) -> Unit,
    private val onClick: (OperatorResponse) -> Unit
) : RecyclerView.Adapter<OperatorAdapter.OperatorViewHolder>() {

    class OperatorViewHolder(val binding: ItemOperatorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorViewHolder {
        val binding = ItemOperatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperatorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperatorViewHolder, position: Int) {
        val op = operators[position]
        holder.binding.apply {
            tvOpName.text = op.name
            tvOpPhone.text = "SĐT: ${op.phone}"
            tvOpEmail.text = "Email: ${op.email}"
            
            btnEdit.setOnClickListener { onEdit(op) }
            btnDelete.setOnClickListener { onDelete(op) }
            root.setOnClickListener { onClick(op) }
        }
    }

    override fun getItemCount(): Int = operators.size

    fun updateData(newList: List<OperatorResponse>) {
        operators = newList
        notifyDataSetChanged()
    }
}
