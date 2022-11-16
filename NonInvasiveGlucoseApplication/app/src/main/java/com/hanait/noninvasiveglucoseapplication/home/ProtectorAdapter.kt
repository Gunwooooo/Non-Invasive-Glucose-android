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

    //adapter 클릭 리스너 외부 처리를 위한 인터페이스
    private var mListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onDeleteItem(v:View, pos:Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var protectorPhoneNumber: TextView
        private lateinit var protectorDeleteBtn : TextView
        fun setTextView(protectorData: ProtectorData, position: Int) {
            protectorPhoneNumber = itemView.findViewById(R.id.homeProtectingItem_textView_phoneNumber)
            protectorDeleteBtn = itemView.findViewById(R.id.homeProtectorItem_btn_delete)

            protectorPhoneNumber.text = protectorData.phoneNumber

            //삭제 클릭 리스너
            protectorDeleteBtn.setOnClickListener {
                mListener?.onDeleteItem(it, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.home_protecting_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tmpData = data[position]
        holder.setTextView(tmpData, position + 1)
    }

    override fun getItemCount(): Int {
        return data.size
    }


}