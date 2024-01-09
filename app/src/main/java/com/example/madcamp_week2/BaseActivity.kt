package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    private var currentPageId: Int = R.id.page_1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", "DefaultName")
    }

    private fun setupAccountIconClickListener() {
        val accountIcon = findViewById<ImageView>(R.id.accountCircle)
        accountIcon.setOnClickListener {
            // MypageActivity로 이동하는 Intent 생성
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            // 필요하다면 애니메이션 추가
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    protected fun setupBottomNavigation(currentMenuId: Int) {
        setupAccountIconClickListener()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = currentMenuId // 현재 액티비티의 탭 활성화

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> navigateTo(MainActivity::class.java, R.id.page_1)
                R.id.page_2 -> navigateTo(NoneActivity::class.java, R.id.page_2)
                R.id.page_3 -> navigateTo(NoneActivity::class.java, R.id.page_3)
                R.id.page_4 -> navigateTo(Register::class.java, R.id.page_4)
            }
            true
        }
    }

    private fun navigateTo(activityClass: Class<*>, targetPageId: Int) {
        val intent = Intent(this, activityClass)
        if (this::class.java != activityClass) {
            startActivity(intent)

            // 페이지 번호에 따라 애니메이션 결정
            if (currentPageId < targetPageId) {
                // 현재 페이지가 목표 페이지보다 작은 경우 (오른쪽으로 이동)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                // 현재 페이지가 목표 페이지보다 큰 경우 (왼쪽으로 이동)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            currentPageId = targetPageId
        }
    }

}