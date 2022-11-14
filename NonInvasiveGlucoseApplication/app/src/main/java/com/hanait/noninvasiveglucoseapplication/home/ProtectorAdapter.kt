package com.hanait.noninvasiveglucoseapplication.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData

class ProtectorAdapter(var context: Context, var data: ArrayList<ProtectorData>) : RecyclerView.Adapter<ProtectorAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var protectorPhoneNumber: TextView
        fun setTextView(protectorData: ProtectorData, position: Int) {
            protectorPhoneNumber = itemView.findViewById(R.id.homeProtectorItem_textView_phoneNumber)
            protectorPhoneNumber.text = protectorData.phoneNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.home_protector_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tmpData = data[position]
        holder.setTextView(tmpData, position + 1)
    }

    override fun getItemCount(): Int {
        return data.size
    }


}