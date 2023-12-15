package com.example.reddit.presentation.utils

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import com.example.reddit.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun hideAppbarAndBottomView(requireActivity: Activity) {
    requireActivity.findViewById<Toolbar>(R.id.toolbar).isGone = true
    val bottomNavigationView = requireActivity.findViewById<BottomNavigationView>(R.id.bottomView)
    bottomNavigationView.isGone = true
}
fun hideAppbar(requireActivity: Activity) {
    requireActivity.findViewById<Toolbar>(R.id.toolbar).isGone = true
}

fun showBottomView(requireActivity: Activity) {
    val bottomNavigationView = requireActivity.findViewById<BottomNavigationView>(R.id.bottomView)
    bottomNavigationView.isGone = false
}