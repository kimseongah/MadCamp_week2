package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class MapStartDemoActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var tvLat: TextView? = null
    private var tvLng: TextView? = null
    private var tvZoomLevel: TextView? = null
    private val startZoomLevel = 15
    private val startPosition = LatLng.from(37.394660, 127.111182) // 판교역

    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            Toast.makeText(applicationContext, "Map Start!", Toast.LENGTH_SHORT).show()
            tvLat!!.text = startPosition.getLatitude().toString()
            tvLng!!.text = startPosition.getLongitude().toString()
            tvZoomLevel!!.text = startZoomLevel.toString()
            Log.i(
                "k3f", "startPosition: "
                        + kakaoMap.cameraPosition!!.position.toString()
            )
            Log.i(
                "k3f", "startZoomLevel: "
                        + kakaoMap.zoomLevel
            )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_auth_start)
        title = "Map Start"
        tvLat = findViewById<TextView>(R.id.tvLat)
        tvLng = findViewById<TextView>(R.id.tvLng)
        tvZoomLevel = findViewById<TextView>(R.id.tvZoomLevel)
        mapView = findViewById(R.id.map_view)
        mapView?.run { start(lifeCycleCallback, readyCallback) }
    }
}