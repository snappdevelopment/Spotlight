package com.snad.sniffer.feed.navigation

import androidx.navigation.NavGraphBuilder
import com.snad.sniffer.navigation.NavigationDestination
import androidx.navigation.compose.composable
import com.snad.sniffer.feed.FeedUi
import com.snad.sniffer.feed.FeedViewModel

internal object FeedDestination : NavigationDestination {
    override val route = "feed_route"
}

internal fun NavGraphBuilder.feedGraph(
    viewModel: FeedViewModel,
    onBackClick: () -> Unit
) {
    composable(route = FeedDestination.route) {
        FeedUi(viewModel, onBackClick)
    }
}