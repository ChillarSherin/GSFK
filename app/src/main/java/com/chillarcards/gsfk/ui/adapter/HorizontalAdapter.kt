package com.chillarcards.gsfk.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.ui.Org
import de.hdodenhof.circleimageview.CircleImageView

class HorizontalAdapter(private val items: List<Org>, val context: Context?) : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.org_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orgName: TextView = itemView.findViewById(R.id.org_name)
        private val orgImage: CircleImageView = itemView.findViewById(R.id.org_img)

        fun bind(item: Org) {
        //    orgName.text = item.orgName
            orgImage.setImageDrawable(context!!.getDrawable(item.imageId))
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
