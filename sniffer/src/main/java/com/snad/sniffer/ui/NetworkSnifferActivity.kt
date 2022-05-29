package com.snad.sniffer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.snad.sniffer.NetworkSniffer
import com.snad.sniffer.feed.FeedViewModel
import com.snad.sniffer.feed.navigation.FeedDestination
import com.snad.sniffer.feed.navigation.feedGraph

internal class NetworkSnifferActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = NetworkSniffer.Factory.repository
        val feedViewModel by viewModels<FeedViewModel> { FeedViewModel.Factory(repository) }

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = FeedDestination.route,
            ) {
                feedGraph(
                    viewModel = feedViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
