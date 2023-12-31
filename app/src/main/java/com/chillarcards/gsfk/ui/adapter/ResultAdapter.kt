package com.chillarcards.gsfk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.data.model.RpData
import com.chillarcards.gsfk.interfaces.IAdapterViewUtills
import com.chillarcards.gsfk.utills.CommonDBaseModel

class ResultAdapter(
    private val items: List<RpData>,
    private val context: Context?,
    private val getAdapterUtil: IAdapterViewUtills
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.checkBox.isChecked = position == selectedPosition

        holder.checkBox.setOnClickListener {
            if (selectedPosition != position) {
                selectedPosition = position
                notifyDataSetChanged()

                val commonDObj = CommonDBaseModel()
                commonDObj.mastIDs = item.event_transaction_item_id.toString()
                commonDObj.itmName = item.sub_package_name
                commonDObj.valueStr1 = item.sub_package_count
                val sCommonDAry: ArrayList<CommonDBaseModel> = ArrayList()
                sCommonDAry.add(commonDObj)
                getAdapterUtil.getAdapterPosition(position, sCommonDAry, "CheckIn")
            }
        }

    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val BookingView: CardView = itemView.findViewById(R.id.book_frm)
        val CustomNameTextView: TextView = itemView.findViewById(R.id.event_name)
        val GuestCount: TextView = itemView.findViewById(R.id.event_guest)
        val PayStatus: ImageView = itemView.findViewById(R.id.report_status)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(item: RpData) {
            CustomNameTextView.text = item.sub_package_name
            GuestCount.text = item.sub_package_count
            if (item.event_transaction_item_status.toInt() == 1) {
                PayStatus.visibility = View.VISIBLE
                checkBox.visibility = View.GONE
            }
        }
    }
}
