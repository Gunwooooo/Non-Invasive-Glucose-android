package com.example.newnoninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.model.ProtectorData
import com.example.newnoninvasiveglucoseapplication.retrofit.API.PHR_PROFILE_BASE_URL
import com.example.newnoninvasiveglucoseapplication.util.Constants.PROFILE_IMAGE_NAME

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
        private lateinit var protectorProfile: ImageView
        private lateinit var protectorNickname: TextView
        private lateinit var protectorDeleteBtn : TextView
        private lateinit var protectorInfoBtn: LinearLayout
        @SuppressLint("SetTextI18n")
        fun setTextView(protectorData: ProtectorData, position: Int) {
            protectorProfile = itemView.findViewById(R.id.homeProtectorItem_imageView_profile)
            protectorNickname = itemView.findViewById(R.id.homeProtectorItem_textView_nickname)
            protectorDeleteBtn = itemView.findViewById(R.id.homeProtectorItem_btn_delete)
            protectorInfoBtn = itemView.findViewById(R.id.homeProtectorItem_layout_Info)

            //프로필 이미지 넣기
            val glide = Glide.with(context)
            val sb = StringBuilder()
            sb.append(PHR_PROFILE_BASE_URL).append(protectorData.phoneNumber).append(PROFILE_IMAGE_NAME)
            glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .placeholder(R.drawable.icon_color_profile).circleCrop().into(protectorProfile)
            protectorNickname.text = protectorData.nickname

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