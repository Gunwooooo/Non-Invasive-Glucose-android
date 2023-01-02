package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.pavlospt.roundedletterview.RoundedLetterView
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
        private lateinit var protectorRlv: RoundedLetterView
        private lateinit var protectorPhoneNumber: TextView
        private lateinit var protectorDeleteBtn : TextView
        private lateinit var protectorInfoBtn: LinearLayout
        @SuppressLint("SetTextI18n")
        fun setTextView(protectorData: ProtectorData, position: Int) {
            protectorRlv = itemView.findViewById(R.id.homeProtectorItem_rlv)
            protectorPhoneNumber = itemView.findViewById(R.id.homeProtectorItem_textView_phoneNumber)
            protectorDeleteBtn = itemView.findViewById(R.id.homeProtectorItem_btn_delete)
            protectorInfoBtn = itemView.findViewById(R.id.homeProtectorItem_layout_Info)

            //라운드 텍스트 이미지 넣기
            if(position % 2 ==0)
                protectorRlv.backgroundColor = ContextCompat.getColor(context, R.color.iphone_yellow)

            //text넣기
            protectorRlv.titleText = protectorData.nickname[0].toString()
            protectorPhoneNumber.text = protectorData.nickname + " 님"

            //삭제 클릭 리스너
            protectorDeleteBtn.setOnClickListener {
                mListener?.onDeleteItem(it, position)
            }
            //보호 대상자 정보 보기 리스너
            protectorInfoBtn.setOnClickListener {
                mListener?.onInfoItem(it, position)
            }
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