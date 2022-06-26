package com.snad.sniffer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.snad.sniffer.NetworkSniffer
import com.snad.sniffer.details.DetailsViewModel
import com.snad.sniffer.details.navigation.DetailsDestination
import com.snad.sniffer.details.navigation.detailsGraph
import com.snad.sniffer.feed.FeedViewModel
import com.snad.sniffer.feed.navigation.FeedDestination
import com.snad.sniffer.feed.navigation.feedGraph
import com.snad.sniffer.util.LocalDateTimeFormatter

internal class NetworkSnifferActivity : ComponentActivity() {

    private val repository = NetworkSniffer.Factory.repository
    private val dateTimeFormatter = LocalDateTimeFormatter()
    private val feedViewModelFactory = FeedViewModel.Factory(repository, dateTimeFormatter)
    private val detailsViewModelFactory = DetailsViewModel.Factory(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = FeedDestination.route,
            ) {
                feedGraph(
                    viewModelFactory = feedViewModelFactory,
                    onBackClick = { navController.popBackStack() },
                    onRequestClick = { navController.navigate("${DetailsDestination.route}/$it") }
                )
                detailsGraph(
                    viewModelFactory = detailsViewModelFactory,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
