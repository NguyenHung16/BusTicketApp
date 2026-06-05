package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityBookingConfirmBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class BookingConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingConfirmBinding
    private lateinit var sessionManager: SessionManager
    private var tripId: Int = -1
    private var seatId: Int = -1
    private var seatCode: String? = null
    
    private var pickupPoints: List<PickupDropoffPointResponse> = emptyList()
    private var dropoffPoints: List<PickupDropoffPointResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        tripId = intent.getIntExtra("TRIP_ID", -1)
        seatId = intent.getIntExtra("SEAT_ID", -1)
        seatCode = intent.getStringExtra("SEAT_CODE")

        binding.tvSeatSummary.text = "Ghế đã chọn: $seatCode"

        fetchTripDetails()
        fetchPoints()

        binding.btnConfirmBooking.setOnClickListener {
            confirmBooking()
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun fetchTripDetails() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getTripDetails(tripId).enqueue(object : Callback<ApiResponse<TripResponse>> {
            override fun onResponse(call: Call<ApiResponse<TripResponse>>, response: Response<ApiResponse<TripResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val trip = response.body()?.data
                    trip?.let {
                        binding.tvTripSummary.text = "${it.operatorName}\n${it.departureProvince} → ${it.destinationProvince}"
                        binding.tvTimeSummary.text = "Khởi hành: ${it.departureTime} ngày ${it.departureDate}"
                        
                        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        binding.tvPriceSummary.text = "Giá vé: ${currencyFormat.format(it.price)}"
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<TripResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchPoints() {
        RetrofitClient.instance.getPickupPoints(tripId).enqueue(object : Callback<ApiResponse<List<PickupDropoffPointResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<PickupDropoffPointResponse>>>, response: Response<ApiResponse<List<PickupDropoffPointResponse>>>) {
                if (response.isSuccessful) {
                    pickupPoints = response.body()?.data ?: emptyList()
                    val names = pickupPoints.map { it.name }
                    val adapter = ArrayAdapter(this@BookingConfirmActivity, android.R.layout.simple_spinner_dropdown_item, names)
                    binding.spinnerPickup.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<PickupDropoffPointResponse>>>, t: Throwable) {}
        })

        RetrofitClient.instance.getDropoffPoints(tripId).enqueue(object : Callback<ApiResponse<List<PickupDropoffPointResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<PickupDropoffPointResponse>>>, response: Response<ApiResponse<List<PickupDropoffPointResponse>>>) {
                if (response.isSuccessful) {
                    dropoffPoints = response.body()?.data ?: emptyList()
                    val names = dropoffPoints.map { it.name }
                    val adapter = ArrayAdapter(this@BookingConfirmActivity, android.R.layout.simple_spinner_dropdown_item, names)
                    binding.spinnerDropoff.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<PickupDropoffPointResponse>>>, t: Throwable) {}
        })
    }

    private fun confirmBooking() {
        val name = binding.etPassengerName.text.toString().trim()
        val phone = binding.etPassengerPhone.text.toString().trim()
        val pickupIdx = binding.spinnerPickup.selectedItemPosition
        val dropoffIdx = binding.spinnerDropoff.selectedItemPosition

        if (name.isEmpty() || phone.isEmpty() || pickupIdx == -1 || dropoffIdx == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = if (binding.rbOnline.isChecked) "online" else "cod"
        val token = sessionManager.fetchAuthToken() ?: return
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnConfirmBooking.isEnabled = false

        val request = BookingRequest(
            tripId = tripId,
            seatId = seatId, // GỬI ĐÚNG SEAT_ID LÊN BACKEND
            seatCode = seatCode,
            pickupPointId = pickupPoints[pickupIdx].id,
            dropoffPointId = dropoffPoints[dropoffIdx].id,
            passengerName = name,
            passengerPhone = phone,
            passengerEmail = sessionManager.getUserName() ?: "customer@busapp.com",
            paymentMethod = paymentMethod,
            ticketType = "one_way"
        )

        RetrofitClient.instance.createBooking("Bearer $token", request).enqueue(object : Callback<ApiResponse<BookingResponse>> {
            override fun onResponse(call: Call<ApiResponse<BookingResponse>>, response: Response<ApiResponse<BookingResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val bookingData = response.body()?.data
                    if (paymentMethod == "online") {
                        val intent = Intent(this@BookingConfirmActivity, PaymentActivity::class.java)
                        intent.putExtra("BOOKING_CODE", bookingData?.bookingCode)
                        intent.putExtra("AMOUNT", bookingData?.ticketPrice)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@BookingConfirmActivity, "Đặt vé thành công!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@BookingConfirmActivity, MyBookingsActivity::class.java))
                    }
                    finishAffinity()
                } else {
                    binding.btnConfirmBooking.isEnabled = true
                    val errorMsg = response.body()?.message ?: "Lỗi hệ thống khi đặt vé"
                    Toast.makeText(this@BookingConfirmActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<BookingResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnConfirmBooking.isEnabled = true
                Toast.makeText(this@BookingConfirmActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
