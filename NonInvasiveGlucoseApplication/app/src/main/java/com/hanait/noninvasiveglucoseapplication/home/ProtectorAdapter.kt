package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData

class ProtectorAdapter(var context: Context, var data: ArrayList<ProtectorData>) : RecyclerView.Adapter<ProtectorAdapter.VH>() {

    //adapter 클릭 리스너 외부 처리를 위한 인터페이스
    private var mListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onInfoItem(v: View, pos:Int)
        fun onDeleteItem(v:View, pos:Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var protectingPhoneNumber: TextView
        private lateinit var protectingDeleteBtn : TextView
        private lateinit var protectingInfoBtn: LinearLayout
        @SuppressLint("SetTextI18n")
        fun setTextView(protectorData: ProtectorData, position: Int) {
            protectingPhoneNumber = itemView.findViewById(R.id.homeProtectingItem_textView_phoneNumber)
            protectingDeleteBtn = itemView.findViewById(R.id.homeProtectingItem_btn_delete)
            protectingInfoBtn = itemView.findViewById(R.id.homeProtectingItem_layout_Info)

            //text넣기
            protectingPhoneNumber.text = protectorData.nickname + " 님"

            //삭제 클릭 리스너
            protectingDeleteBtn.setOnClickListener {
                mListener?.onDeleteItem(it, position)
            }
            //보호 대상자 정보 보기 리스너
            protectingInfoBtn.setOnClickListener {
                mListener?.onInfoItem(it, position)
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