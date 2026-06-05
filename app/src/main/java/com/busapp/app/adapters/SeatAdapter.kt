package com.busapp.app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.SeatResponse
import com.busapp.app.databinding.ItemSeatBinding

class SeatAdapter(
    private var seats: List<SeatResponse>,
    private val onSeatClick: (SeatResponse) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private var selectedSeat: SeatResponse? = null

    class SeatViewHolder(val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seats[position]
        holder.binding.tvSeatCode.text = seat.seatCode

        // UI state based on status
        when {
            seat.status != "available" -> {
                holder.binding.cardSeat.setCardBackgroundColor(Color.LTGRAY)
                holder.binding.root.isEnabled = false
            }
            seat.seatCode == selectedSeat?.seatCode -> {
                holder.binding.cardSeat.setCardBackgroundColor(Color.parseColor("#2196F3"))
                holder.binding.tvSeatCode.setTextColor(Color.WHITE)
            }
            else -> {
                holder.binding.cardSeat.setCardBackgroundColor(Color.WHITE)
                holder.binding.tvSeatCode.setTextColor(Color.BLACK)
                holder.binding.root.isEnabled = true
            }
        }

        holder.binding.root.setOnClickListener {
            if (seat.status == "available") {
                selectedSeat = if (selectedSeat?.seatCode == seat.seatCode) null else seat
                notifyDataSetChanged()
                onSeatClick(seat)
            }
        }
    }

    override fun getItemCount(): Int = seats.size

    fun updateData(newSeats: List<SeatResponse>) {
        seats = newSeats
        notifyDataSetChanged()
    }
    
    fun getSelectedSeat(): SeatResponse? = selectedSeat
}
