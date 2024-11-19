package com.example.project5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = NotificationHelper(this)
        notificationHelper.scheduleDailyNotification()

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragment1: Fragment = DashboardFragment()
        val fragment2: Fragment = EntryFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.action_dashboard -> fragment = fragment1
                R.id.action_entry -> fragment = fragment2
            }
            // Replace fragment dynamically
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            true
        }

        // Set default selection to DashboardFragment
        bottomNavigationView.selectedItemId = R.id.action_dashboard
    }
}
