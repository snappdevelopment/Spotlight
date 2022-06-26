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
    viewModelFactory: FeedViewModel.Factory,
    onBackClick: () -> Unit,
    onRequestClick: (Long) -> Unit
) {
    composable(route = FeedDestination.route) {
        FeedUi(viewModelFactory, onBackClick, onRequestClick)
    }
}