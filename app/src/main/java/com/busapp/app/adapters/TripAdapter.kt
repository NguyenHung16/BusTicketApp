package com.busapp.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.TripResponse
import com.busapp.app.databinding.ItemTripBinding
import java.text.NumberFormat
import java.util.*

class TripAdapter(
    private var trips: List<TripResponse>,
    private val onEdit: ((TripResponse) -> Unit)? = null,
    private val onDelete: ((TripResponse) -> Unit)? = null,
    private val onTripClick: (TripResponse) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        
        holder.binding.apply {
            tvOperatorName.text = trip.operatorName
            tvRoute.text = "${trip.departureProvince} → ${trip.destinationProvince}"
            
            val arrivalTime = trip.arrivalTime ?: "--:--"
            tvTime.text = "${trip.departureTime} - $arrivalTime"
            
            tvAvailableSeats.text = "Còn ${trip.availableSeats} ghế"
            tvVehicleType.text = trip.vehicleTypeName ?: "Xe chất lượng cao"
            
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = currencyFormat.format(trip.price)

            // Ẩn các nút Edit/Delete nếu callback là null (thường dùng cho dashboard của User)
            if (onEdit == null && onDelete == null) {
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE
            } else {
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                btnEdit.setOnClickListener { onEdit?.invoke(trip) }
                btnDelete.setOnClickListener { onDelete?.invoke(trip) }
            }
            
            root.setOnClickListener { onTripClick(trip) }
        }
    }

    override fun getItemCount(): Int = trips.size

    fun updateData(newTrips: List<TripResponse>) {
        trips = newTrips
        notifyDataSetChanged()
    }
}
