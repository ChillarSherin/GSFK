package com.chillarcards.gsfk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.data.model.RDetails

class ReportAdapter(private val items: List<RDetails>,
                    private val context: Context?,
)
    : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val guestName: TextView = itemView.findViewById(R.id.guest_name)
        private val eventGuest: TextView = itemView.findViewById(R.id.event_guest)
        private val eventPack: TextView = itemView.findViewById(R.id.event_pack)

        fun bind(item: RDetails) {
            if ( item.full_name == null){
                guestName.text = item.mobile_number
            }else{
                guestName.text = item.full_name +"( "+item.mobile_number+" )"
            }

            eventGuest.text = item.sub_package_count
            eventPack.text = item.sub_package_name
        }
    }

}
