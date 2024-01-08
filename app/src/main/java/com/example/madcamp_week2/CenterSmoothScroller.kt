package com.example.madcamp_week2

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
        val layoutManager = layoutManager as? LinearLayoutManager
        if (layoutManager != null) {
            val childCenter = (layoutManager.getDecoratedLeft(view) +
                    layoutManager.getDecoratedRight(view)) / 2
            val containerCenter = layoutManager.width / 2
            return containerCenter - childCenter // 아이템을 중앙에 배치
        }
        return super.calculateDxToMakeVisible(view, snapPreference)
    }
}