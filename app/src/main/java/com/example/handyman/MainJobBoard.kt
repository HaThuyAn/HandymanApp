package com.example.handyman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainJobBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_board_main)

        // Set corresponding nav graph based on user type
        val userType = intent.getStringExtra("user_type")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph)

        val startDestination = when(userType) {
            "customer" -> R.id.serviceCategoryFragment
            "handyman" -> R.id.handymanJobBoardFragment
            else -> R.id.handymanJobBoardFragment
        }

        navGraph.setStartDestination(startDestination)
        navHostFragment.navController.graph = navGraph
    }
}