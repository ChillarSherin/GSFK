package com.chillarcards.gsfk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.utills.CommonDBaseModel
import com.chillarcards.gsfk.ui.Dummy
import com.chillarcards.gsfk.interfaces.IAdapterViewUtills

class ReportAdapter(private val items: List<Dummy>,
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
        val BookingView: CardView = itemView.findViewById(R.id.book_frm)
        val guestName: TextView = itemView.findViewById(R.id.guest_name)
        val eventGuest: TextView = itemView.findViewById(R.id.event_guest)
        val eventPack: TextView = itemView.findViewById(R.id.event_pack)

        fun bind(item: Dummy) {
            guestName.text = item.custname
            eventGuest.text = item.id.toString()
            eventPack.text = item.name


        }

    }

    fun getFirstLetterAfterSpace(inputText: String): String {
        val words = inputText.split(" ")
        val result = StringBuilder()

        for (word in words) {
            if (word.isNotEmpty()) {
                val firstChar = word[0]
                result.append(firstChar)
            }
        }

        return result.toString()
    }

}
