package com.example.moiroom.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moiroom.NewCardDetailDialogFragment
import com.example.moiroom.R
import com.example.moiroom.data.CharacteristicType
import com.example.moiroom.data.MatchedMember
import com.example.moiroom.data.Member
import com.example.moiroom.data.RadarChartData
import com.example.moiroom.databinding.CardLayoutBinding
import com.example.moiroom.databinding.CardLayoutSeveralBinding
import com.example.moiroom.utils.getBGColorCharacter
import com.example.moiroom.utils.getCharacterDescription
import com.example.moiroom.utils.getCharacterIcon
import com.example.moiroom.utils.getColorCharacter
import com.example.moiroom.view.RadarChartView
import java.text.DecimalFormat
import kotlin.math.roundToInt

interface CardItemClickListener {
    fun onCardDetailClick(cardInfo: MatchedMember)
}

class CardAdapter(
    private val context: Context,
    private val cardInfoList: List<MatchedMember>,
    private val myInfo: Member,
    private val isToggleButtonChecked: Boolean,
    private val cardItemClickListener: CardItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (isToggleButtonChecked) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = CardLayoutBinding.inflate(inflater, parent, false)
            CardViewHolder1(binding)
        } else {
            val binding = CardLayoutSeveralBinding.inflate(inflater, parent, false)
            CardViewHolder2(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cardInfo = cardInfoList[position]
        val myInfo = myInfo
        if (holder is CardViewHolder1) {
            holder.bind(cardInfo)
        } else if (holder is CardViewHolder2) {
            holder.bind(cardInfo)
        }
    }

    inner class CardViewHolder1(private val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val chartView = RadarChartView(context, null)

        fun bind(cardInfo: MatchedMember) {
            binding.apply {
                val dataList = arrayListOf(
                    RadarChartData(CharacteristicType.socialbility, cardInfo.socialbility.toFloat() / 100),
                    RadarChartData(CharacteristicType.positivity, cardInfo.positivity.toFloat() / 100),
                    RadarChartData(CharacteristicType.activity, cardInfo.activity.toFloat() / 100),
                    RadarChartData(CharacteristicType.communion, cardInfo.communion.toFloat() / 100),
                    RadarChartData(CharacteristicType.altruism, cardInfo.altruism.toFloat() / 100),
                    RadarChartData(CharacteristicType.empathy, cardInfo.empathy.toFloat() / 100),
                    RadarChartData(CharacteristicType.humor, cardInfo.humor.toFloat() / 100),
                    RadarChartData(CharacteristicType.generous, cardInfo.generous.toFloat() / 100)
                )
                val myDataList = arrayListOf(
                    RadarChartData(CharacteristicType.socialbility, myInfo.socialbility.toFloat() / 100),
                    RadarChartData(CharacteristicType.positivity, myInfo.positivity.toFloat() / 100),
                    RadarChartData(CharacteristicType.activity, myInfo.activity.toFloat() / 100),
                    RadarChartData(CharacteristicType.communion, myInfo.communion.toFloat() / 100),
                    RadarChartData(CharacteristicType.altruism, myInfo.altruism.toFloat() / 100),
                    RadarChartData(CharacteristicType.empathy, myInfo.empathy.toFloat() / 100),
                    RadarChartData(CharacteristicType.humor, myInfo.humor.toFloat() / 100),
                    RadarChartData(CharacteristicType.generous, myInfo.generous.toFloat() / 100)
                )

                chartView.setDataList(myDataList, dataList)

                radarChartContainer.removeAllViews()
                radarChartContainer.addView(chartView)

                recyclerView.layoutManager = GridLayoutManager(context, 4)
                val characterAdapter = CharacterAdapter(context, dataList) { clickedData, position ->
                    characterIcon.setImageResource(getCharacterIcon(clickedData.type))
                    characterIcon.setColorFilter(getColorCharacter(clickedData.type.value, context))
                    characterDetailName.text = clickedData.type.value
                    characterDetailDescription.text = getCharacterDescription(clickedData.type)
                    characterLocation.setColorFilter(getColorCharacter(clickedData.type.value, context))
                    pinBase.setCardBackgroundColor(getBGColorCharacter(clickedData.type.value, context))

                    val decimalFormat = DecimalFormat("#.##")
                    myCharacterDescription.text = "상위 ${decimalFormat.format(100 - clickedData.value)}%의 ${clickedData.type.value} 성향을 가지고 있어요"
                    performAnimation(clickedData, binding)
                }
                recyclerView.adapter = characterAdapter

                matchRate.text = "${cardInfo.matchRate}%"
                matchIntroduction.text = cardInfo.matchIntroduction
                nickname.text = cardInfo.memberNickname
                location.text = "${cardInfo.metropolitanName} ${cardInfo.cityName}"
                introduction.text = cardInfo.memberIntroduction

                Glide.with(binding.root.context).load(cardInfo.memberProfileImageUrl).into(binding.profileImage)

                // 밑줄 뷰의 너비 조정
                matchIntroduction.post {
                    val layoutParams = underline.layoutParams
                    Log.d("in CardAdapter", "bind: ${layoutParams.width}, ${matchIntroduction.width}")
                    layoutParams.width = (matchIntroduction.width * 1.1).roundToInt()
                    underline.layoutParams = layoutParams
                    underline.visibility = View.VISIBLE
                }

                scrollView.viewTreeObserver.addOnScrollChangedListener {
                    val scrollY = scrollView.scrollY
                    val headerProfileHeight = headerProfile.height
                    val screenHeight = context.resources.displayMetrics.heightPixels
                    Log.d("in CardAdapter", "bind:!!!!!!!!!!!!!!!!!!!!!1$screenHeight, $headerProfileHeight, $scrollY")

                    // 스크롤이 maxScrollY를 넘으면 headerProfile를 상단에 고정
                    if (scrollY >= headerProfileHeight / 2) {
                        Log.d("in CardAdapter", "bind:!!!!!!!!!!!!!!!!!!!!!1")
                        introductionContainer.visibility = View.GONE
                        matchIntroductionContainer.visibility = View.GONE

                    } else {
                        Log.d("in CardAdapter", "bind:???????????????????????")
                        introductionContainer.visibility = View.VISIBLE
                        matchIntroductionContainer.visibility = View.VISIBLE
                    }
                }
                // clickListener 추가
//                detailButton.setOnClickListener {
//                    cardItemClickListener.onCardDetailClick(cardInfo)
//                }
            }
        }
    }

    inner class CardViewHolder2(private val binding: CardLayoutSeveralBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardInfo: MatchedMember) {
            binding.apply {
                matchingRate.text = "${cardInfo.matchRate}%"
                introduction.text = cardInfo.matchIntroduction
                name.text = cardInfo.memberNickname
                location.text = "${cardInfo.metropolitanName} ${cardInfo.cityName}"

                Glide.with(binding.root.context).load(cardInfo.memberProfileImageUrl).into(binding.profileImage)
            }
        }
    }

    override fun getItemCount() = cardInfoList.size

    fun performAnimation(clickedData: RadarChartData, binding: CardLayoutBinding) {
        val newValue = clickedData.value.coerceIn(0f, 100f)

        // 레이아웃이 로딩되지 않았을 때, 애니메이션 재 시작
        if (binding == null || binding.pinWrapper.width == 0) {
            binding?.characterLocation?.post {
                performAnimation(clickedData, binding)
            }
        }

        val currentMargin = (binding.characterLocation.layoutParams as ViewGroup.MarginLayoutParams).leftMargin
        val newMargin = (newValue / 100 * binding.pinWrapper.width).toInt()

        Log.d("MYTAG", "performAnimation: $newValue, $currentMargin, $newMargin")

        ValueAnimator.ofInt(currentMargin, newMargin).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val params = binding.characterLocation.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = animator.animatedValue as Int
                binding.characterLocation.layoutParams = params
            }
            start()
        }
    }

//    abstract class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val matchingRate: TextView = view.findViewById(R.id.matchingRate)
//        val name: TextView = view.findViewById(R.id.name)
//        val location: TextView = view.findViewById(R.id.location)
//    }
//
//    inner class CardViewHolder1(view: View) : CardViewHolder(view) {
//        val summary: TextView = view.findViewById(R.id.summary)
//        val introduction: TextView = view.findViewById(R.id.introduction)
//        val profileImage: ImageView = view.findViewById(R.id.profileImage)
//        val underline: View = view.findViewById(R.id.underline)
//        val detailButton: Button = view.findViewById(R.id.detailButton)
//    }
//
//    class CardViewHolder2(view: View) : CardViewHolder(view) {
//        val introduction: TextView = view.findViewById(R.id.introduction)
//        val profileImage: ImageView = view.findViewById(R.id.profileImage)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val cardInfo = cardInfoList[position]
//        if (holder is CardViewHolder1) {
//            holder.matchingRate.text = "${cardInfo.matchRate}%"
//            holder.summary.text = cardInfo.matchIntroduction
////            holder.profileImage.setImageResource(cardInfo.memberProfileImageUrl)
//            holder.name.text = cardInfo.memberNickname
//            holder.location.text = cardInfo.metropolitanName + cardInfo.cityName
//            holder.introduction.text = cardInfo.memberIntroduction
//
//            // 밑줄 뷰의 너비를 요약 텍스트뷰의 너비와 같게 설정
//            holder.summary.post {
//                val layoutParams = holder.underline.layoutParams
//                layoutParams.width = holder.summary.width
//                holder.underline.layoutParams = layoutParams
//            }
//
//            // 기존 리스너 제거
//            holder.summary.viewTreeObserver.removeOnGlobalLayoutListener(holder.summary.tag as? ViewTreeObserver.OnGlobalLayoutListener)
//
//            val summaryObserver = holder.summary.viewTreeObserver
//            val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    val layoutParams = holder.underline.layoutParams
//                    layoutParams.width = holder.summary.width
//                    holder.underline.layoutParams = layoutParams
//
//                    // 무한 루프를 방지하기 위해 콜백 제거
//                    holder.summary.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            }
//
//            // 리스너를 태그에 저장
//            holder.summary.tag = globalLayoutListener
//            summaryObserver.addOnGlobalLayoutListener(globalLayoutListener)
//
//            holder.detailButton.setOnClickListener {
//                cardItemClickListener.onCardDetailClick(cardInfo) // 버튼 클릭 이벤트 처리
//            }
//
//            holder.detailButton.setOnClickListener {
//                val fragmentManager = (holder.detailButton.context as AppCompatActivity).supportFragmentManager
//
//                // NewCardDetailDialogFragment 인스턴스 생성
//                val newCardDetailDialogFragment = NewCardDetailDialogFragment.newInstance(cardInfo)
//
//                // DialogFragment를 보여주는 일반적인 방법을 사용
//                newCardDetailDialogFragment.show(fragmentManager, "cardDetail")
//            }
//
//        } else if (holder is CardViewHolder2) {
//            holder.matchingRate.text = "${cardInfo.matchRate}%"
//            holder.introduction.text = cardInfo.matchIntroduction
//            holder.profileImage.setImageResource(cardInfo.profileImage)
//            holder.name.text = cardInfo.memberNickname
//            holder.location.text = cardInfo.metropolitanName + cardInfo.cityName
//        }
//    }
//    override fun getItemCount() = cardInfoList.size
}



