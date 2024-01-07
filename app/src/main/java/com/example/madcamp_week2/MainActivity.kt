package com.example.madcamp_week2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.prolificinteractive.materialcalendarview.CalendarDay


class MainActivity : BaseActivity() {
    private lateinit var events: List<Event>
    private val duration = 500

    private var recyclerView: RecyclerView? = null
    private var adapter: MainAdapter? = null

    private var mapView: MapView? = null
    private val startZoomLevel = 16
    private val startPosition = LatLng.from(36.3721, 127.3604)
    private var labelLayer: LabelLayer? = null
    private var kakaoMap: KakaoMap? = null // KakaoMap 객체를 저장할 변수 추가


    val latlngMap = mapOf(
        "카이마루" to LatLng.from(36.3736111, 127.3588611),
        "스포츠 컴플렉스" to LatLng.from(36.3726, 127.3614),
        "울림홀" to LatLng.from(36.3731, 127.3601),
    )

    private var labelToEventMap = hashMapOf<String, Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation(R.id.page_1)

        val myMutableList: MutableList<String> = mutableListOf()
        myMutableList.add("adfad - adfaafd")
        events  = listOf(Event("fdsa","Rock!", "https://image.ytn.co.kr/general/jpg/2023/0202/202302021006048611_t.jpg", CalendarDay.today(),  "스포츠 컴플렉스", myMutableList, "16:00 ~ 18:00"),
            Event("asdf","Rock!", "https://image.ytn.co.kr/general/jpg/2023/0202/202302021006048611_t.jpg", CalendarDay.today(),  "카이마루", myMutableList, "16:00 ~ 18:00"),
            Event("qwer","Rock!", "https://image.ytn.co.kr/general/jpg/2023/0202/202302021006048611_t.jpg", CalendarDay.today(),  "울림홀", myMutableList, "16:00 ~ 18:00"))

        // 데이터 로드 및 어댑터에 데이터 설정
        mapView = findViewById<MapView>(R.id.map_view)
        mapView?.run { start(lifeCycleCallback, readyCallback) }

        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView?
        recyclerView?.layoutManager = LinearLayoutManager(this)

        adapter = MainAdapter(events)
        recyclerView?.adapter = adapter
    }

    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            this@MainActivity.kakaoMap = kakaoMap
            labelLayer = kakaoMap.labelManager!!.layer

            kakaoMap.setOnLabelClickListener { kakaomap, labellayer, label ->
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
            events.forEach { event ->
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

    private fun setPing(event: Event){
        val pos = latlngMap[event.location]
        val styles = kakaoMap!!.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.ping)))
        val labelId = "label_${event.title}_${event.location}"
        labelLayer?.addLabel(LabelOptions.from(labelId, pos).setStyles(styles))

        labelToEventMap[labelId] = event
    }

    private fun onLabelClicked(labelId: String) {
        val event = labelToEventMap[labelId]
        val eventIndex = events.indexOf(event)
        if (eventIndex != -1) {
            recyclerView?.scrollToPosition(eventIndex)
        }
    }

}
