package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", "DefaultName")

    }
    protected fun setupBottomNavigation(currentMenuId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = currentMenuId // 현재 액티비티의 탭 활성화

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> navigateTo(MainActivity::class.java)
                R.id.page_2 -> navigateTo(NoneActivity::class.java)
                R.id.page_3 -> navigateTo(NoneActivity::class.java)
                R.id.page_4 -> navigateTo(Register::class.java)
            }
            true
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        // 아래 코드는 현재 액티비티와 목적지 액티비티가 같지 않을 경우에만 인텐트를 시작합니다.
        if (this::class.java != activityClass) {
            startActivity(intent)
        }
    }
}