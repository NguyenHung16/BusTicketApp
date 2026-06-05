package com.busapp.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.RouteResponse
import com.busapp.app.databinding.ItemRouteBinding

class RouteAdapter(
    private var routes: List<RouteResponse>,
    private val onEdit: (RouteResponse) -> Unit,
    private val onDelete: (RouteResponse) -> Unit,
    private val onClick: (RouteResponse) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    class RouteViewHolder(val binding: ItemRouteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.binding.apply {
            tvRouteTitle.text = "${route.departureProvince} → ${route.destinationProvince}"
            tvRouteDetails.text = "Khoảng cách: ${route.distanceKm}km - Thời gian: ${route.durationHours}h"
            
            btnEdit.setOnClickListener { onEdit(route) }
            btnDelete.setOnClickListener { onDelete(route) }
            root.setOnClickListener { onClick(route) }
        }
    }

    override fun getItemCount(): Int = routes.size

    fun updateData(newList: List<RouteResponse>) {
        routes = newList
        notifyDataSetChanged()
    }
}
