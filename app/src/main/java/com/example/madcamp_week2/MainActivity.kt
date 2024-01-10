package com.example.madcamp_week2

import android.content.pm.LabeledIntent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.NonCancellable.start
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface gettodaybusking {
    @GET("/get_today_busking") // Replace with your actual endpoint
    fun getTodayBusking(): Call<List<Busking>>
}

class MainActivity : BaseActivity() {
    private var todayBuskingList: List<Busking>? = null
    private val duration = 500

    private var recyclerView: RecyclerView? = null
    private var adapter: MainAdapter? = null

    private var mapView: MapView? = null
    private val startZoomLevel = 16
    private val startPosition = LatLng.from(36.3721, 127.3604)
    private var labelLayer: LabelLayer? = null
    private var kakaoMap: KakaoMap? = null // KakaoMap 객체를 저장할 변수 추가
    private var currentSelectedLabelId: String? = null


    val latlngMap = mapOf(
        "카이마루 앞" to LatLng.from(36.3736111, 127.3588611),
        "스포츠 컴플렉스" to LatLng.from(36.3726, 127.3614),
        "신학관 울림홀" to LatLng.from(36.3731, 127.3601),
    )

    private var labelToEventMap = hashMapOf<String, Busking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation(R.id.page_1)

        val myMutableList: MutableList<String> = mutableListOf()
        myMutableList.add("adfad - adfaafd")
//        events  = listOf(Event("fdsa","Rock!", "https://postfiles.pstatic.net/MjAyNDAxMDhfMjQ5/MDAxNzA0NjkyODU3MDAz.bxWdedrxmfNoM8IC1TgIgL2UuggV2pkPh2tecBdkhYQg.k_tnwjfJGGSBRIr_tMesywmxJy-QsLKBNVk7MSJysl4g.JPEG.kimsa0322/profile1.jpg?type=w773", CalendarDay.today(),  "스포츠 컴플렉스", myMutableList, "16:00 ~ 18:00"),
//            Event("asdf","Rock!", "https://postfiles.pstatic.net/MjAyNDAxMDhfNjIg/MDAxNzA0NjkzMzkyNjM4.ol_MRQcg2GCvob34H5YXbp_T6b1v73Hn6jrsdt5krecg.4K0cjo4-fqH_z1kkc-JwJFnUH7cods_CORELMYW4Ga0g.JPEG.kimsa0322/b0e32d07-40ba-4ccd-8786-5385f68bb90b.jpg?type=w773", CalendarDay.today(),  "카이마루", myMutableList, "16:00 ~ 18:00"),
//            Event("qwer","Rock!", "https://postfiles.pstatic.net/MjAyNDAxMDhfMTc1/MDAxNzA0NjkzMTQ0NTc4.aEmGN2J50RK9ZQnSvr2GsZWxAAX5O2bZtSlZOvmuFtQg.r1w5ndrnEV_PXjKVrbgnwrurVEzlECJZ8X_pXDIjA58g.JPEG.kimsa0322/9886ceb0-252e-4ec4-a2d3-ecd7a7671c17.jpg?type=w773", CalendarDay.today(),  "울림홀", myMutableList, "16:00 ~ 18:00"))
        //today busking list get from server
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(gettodaybusking::class.java)

        val call = apiService.getTodayBusking()
        call.enqueue(object : Callback<List<Busking>> {
            /*todo*/
            override fun onResponse(call: Call<List<Busking>>, response: Response<List<Busking>>) {
                if (response.isSuccessful) {
                    todayBuskingList = response.body()
                    Log.d("todayBuskingList", todayBuskingList.toString())
                    if (todayBuskingList != null && todayBuskingList!!.isNotEmpty()) {
                        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
                        adapter = MainAdapter(todayBuskingList!!)
                        scrollToCenter(1)
                        recyclerView!!.adapter = adapter
                        recyclerView?.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    } else {
                        // Handle the case when the buskingList is null or empty
                        // For instance, you can show a message or handle this scenario accordingly
                        Log.d("Response", "Empty or null buskingList")
                    }
                }else {
                    Log.d("Failed to fetch busking data:", "${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Busking>>, t: Throwable) {
                println("Failed to fetch busking data: ${t.message}")
            }
        })
        // 데이터 로드 및 어댑터에 데이터 설정
        mapView = findViewById<MapView>(R.id.map_view)
        mapView?.run { start(lifeCycleCallback, readyCallback) }

    }

    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            this@MainActivity.kakaoMap = kakaoMap
            labelLayer = kakaoMap.labelManager!!.layer

            kakaoMap.setOnLabelClickListener { kakaomap, labellayer, label ->
                // 기존 레이블 제거
                onLabelClicked(label.labelId)
                true
            }
        }


        override fun getPosition(): LatLng {
            return startPosition
        }

        override fun getZoomLevel(): Int {
            return startZoomLevel
        }
    }
    // MapLifeCycleCallback 을 통해 지도의 LifeCycle 관련 이벤트를 수신할 수 있다.
    private val lifeCycleCallback: MapLifeCycleCallback = object : MapLifeCycleCallback() {
        override fun onMapResumed() {
            super.onMapResumed()
            todayBuskingList!!.forEach { event ->
                setPing(event)
            }
        }

        override fun onMapPaused() {
            super.onMapPaused()
        }

        override fun onMapDestroy() {
            Toast.makeText(
                applicationContext, "onMapDestroy",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onMapError(error: Exception) {
            Toast.makeText(
                applicationContext, error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setPing(event: Busking){
        val pos = latlngMap[event.location]
        val styles = kakaoMap!!.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.blue_ping)))
        val labelId = "label_${event.title}_${event.location}"
        labelLayer?.addLabel(LabelOptions.from(labelId, pos).setStyles(styles))

        labelToEventMap[labelId] = event
    }

    private fun onLabelClicked(labelId: String) {
        if (currentSelectedLabelId != null) {
            updateLabelStyle(currentSelectedLabelId!!, R.drawable.blue_ping)
        }

        // 새로운 레이블을 빨간색으로 변경
        updateLabelStyle(labelId, R.drawable.red_ping)
        currentSelectedLabelId = labelId

        val event = labelToEventMap[labelId]
        val eventIndex = event?.let { todayBuskingList!!.indexOf(it) }

        val locationKey = eventIndex?.let { todayBuskingList?.get(it)?.location ?: event }
        val latLng = latlngMap[locationKey]

        latLng?.let {
            kakaoMap!!.moveCamera(
                CameraUpdateFactory.newCenterPosition(latLng, startZoomLevel),
                CameraAnimation.from(duration)
            )
        }

        if (eventIndex != null) {
            scrollToCenter(eventIndex)
        }
    }

    private fun scrollToCenter(position: Int) {
        val smoothScroller = recyclerView?.let { CenterSmoothScroller(it.context) }
        if (smoothScroller != null) {
            smoothScroller.targetPosition = position
        }
        recyclerView?.layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun updateLabelStyle(labelId: String, iconResId: Int) {
        labelLayer?.remove(labelLayer!!.getLabel(labelId))
        val event = labelToEventMap[labelId]
        val pos = latlngMap[event?.location]
        val styles = kakaoMap!!.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(iconResId)))
        labelLayer?.addLabel(LabelOptions.from(labelId, pos).setStyles(styles))
    }

}
