package com.busapp.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.BookingResponse
import com.busapp.app.databinding.ItemBookingBinding
import java.text.NumberFormat
import java.util.*

class BookingAdapter(
    private var bookings: List<BookingResponse>,
    private val onBookingClick: (BookingResponse) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        
        holder.binding.apply {
            tvBookingCode.text = "Mã vé: ${booking.bookingCode}"
            tvBookingStatus.text = booking.bookingStatus.replaceFirstChar { it.uppercase() }
            
            // Sử dụng các trường dữ liệu đã được làm phẳng từ BookingResponse
            val operator = booking.operatorName ?: "N/A"
            val time = booking.departureTime ?: ""
            val date = booking.departureDate ?: ""
            tvTripInfo.text = "$operator - $time $date"
            
            val from = booking.departureProvince ?: "N/A"
            val to = booking.destinationProvince ?: "N/A"
            tvRouteInfo.text = "$from → $to"

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            tvPrice.text = currencyFormat.format(booking.ticketPrice)
            tvPaymentStatus.text = "Thanh toán: ${booking.paymentStatus}"

            root.setOnClickListener { onBookingClick(booking) }
        }
    }

    override fun getItemCount(): Int = bookings.size

    fun updateData(newBookings: List<BookingResponse>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
