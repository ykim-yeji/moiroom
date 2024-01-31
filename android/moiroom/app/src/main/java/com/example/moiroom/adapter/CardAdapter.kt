package com.example.moiroom.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.moiroom.CardDetailFragment
import com.example.moiroom.R
import com.example.moiroom.data.CardInfo

interface CardItemClickListener {
    fun onCardDetailClick(cardInfo: CardInfo)
}

class CardAdapter(
    private val cardInfoList: List<CardInfo>,
    private val isToggleButtonChecked: Boolean,
    private val cardItemClickListener: CardItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matchingRate: TextView = view.findViewById(R.id.matchingRate)
        val name: TextView = view.findViewById(R.id.name)
        val location: TextView = view.findViewById(R.id.location)
    }

    inner class CardViewHolder1(view: View) : CardViewHolder(view) {
        val summary: TextView = view.findViewById(R.id.summary)
        val introduction: TextView = view.findViewById(R.id.introduction)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val underline: View = view.findViewById(R.id.underline)
        val detailButton: Button = view.findViewById(R.id.detailButton)
    }

    class CardViewHolder2(view: View) : CardViewHolder(view) {
        val introduction: TextView = view.findViewById(R.id.introduction)
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isToggleButtonChecked) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val view = inflater.inflate(R.layout.card_layout, parent, false)
            CardViewHolder1(view)
        } else {
            val view = inflater.inflate(R.layout.card_layout_several, parent, false)
            CardViewHolder2(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cardInfo = cardInfoList[position]
        if (holder is CardViewHolder1) {
            holder.matchingRate.text = "${cardInfo.matchingRate}%"
            holder.summary.text = cardInfo.summary
            holder.profileImage.setImageResource(cardInfo.profileImage)
            holder.name.text = cardInfo.name
            holder.location.text = cardInfo.location
            holder.introduction.text = cardInfo.introduction

            // 밑줄 뷰의 너비를 요약 텍스트뷰의 너비와 같게 설정
            holder.summary.post {
                val layoutParams = holder.underline.layoutParams
                layoutParams.width = holder.summary.width
                holder.underline.layoutParams = layoutParams
            }

            // 기존 리스너 제거
            holder.summary.viewTreeObserver.removeOnGlobalLayoutListener(holder.summary.tag as? ViewTreeObserver.OnGlobalLayoutListener)

            val summaryObserver = holder.summary.viewTreeObserver
            val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val layoutParams = holder.underline.layoutParams
                    layoutParams.width = holder.summary.width
                    holder.underline.layoutParams = layoutParams

                    // 무한 루프를 방지하기 위해 콜백 제거
                    holder.summary.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }

            // 리스너를 태그에 저장
            holder.summary.tag = globalLayoutListener
            summaryObserver.addOnGlobalLayoutListener(globalLayoutListener)

            holder.detailButton.setOnClickListener {
                cardItemClickListener.onCardDetailClick(cardInfo) // 버튼 클릭 이벤트 처리
            }

            holder.detailButton.setOnClickListener {
                val fragmentManager = (holder.detailButton.context as AppCompatActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                // 애니메이션 설정
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                // 프래그먼트 교체
                fragmentTransaction.replace(R.id.mainFrameLayout, CardDetailFragment.newInstance(cardInfo))

                fragmentTransaction.commit()
            }
        } else if (holder is CardViewHolder2) {
            holder.matchingRate.text = "${cardInfo.matchingRate}%"
            holder.introduction.text = cardInfo.introduction
            holder.profileImage.setImageResource(cardInfo.profileImage)
            holder.name.text = cardInfo.name
            holder.location.text = cardInfo.location
        }
    }


    override fun getItemCount() = cardInfoList.size
}

