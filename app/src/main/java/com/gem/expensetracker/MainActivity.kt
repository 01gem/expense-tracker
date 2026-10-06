package com.gem.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gem.expensetracker.ui.components.AppNavigationBar
import com.gem.expensetracker.ui.components.NavigationTab
import com.gem.expensetracker.ui.screens.*
import com.gem.expensetracker.ui.theme.ExpenseTrackerTheme
import com.gem.expensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val viewModel: ExpenseViewModel = viewModel(
                    factory = ExpenseViewModel.Factory(LocalContext.current)
                )
                var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
                var detailMonth by remember { mutableStateOf<String?>(null) }

                BackHandler(enabled = detailMonth != null) {
                    detailMonth = null
                }
                BackHandler(enabled = detailMonth == null && selectedTab != NavigationTab.HOME) {
                    selectedTab = NavigationTab.HOME
                }

                val topBarTitle = when (selectedTab) {
                    NavigationTab.HOME -> if (detailMonth != null) "Month Details" else "Gem's Expense Tracker"
                    NavigationTab.CALENDAR -> "Calendar"
                    NavigationTab.ADD -> "Add Expense"
                    NavigationTab.LEADERBOARD -> "Leaderboard"
                    NavigationTab.EXPORT -> "Export Data"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .statusBarsPadding(),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 4.dp,
                            shadowElevation = 8.dp
                        ) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        topBarTitle,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            )
                        }
                    },
                    bottomBar = {
                        AppNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { 
                                selectedTab = it
                                detailMonth = null // Reset detail when switching tabs
                            }
                        )
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = selectedTab to detailMonth,
                        transitionSpec = {
                            fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                        },
                        label = "screenTransition"
                    ) { (tab, month) ->
                        when (tab) {
                            NavigationTab.HOME -> {
                                if (month != null) {
                                    MonthDetailScreen(
                                        viewModel = viewModel,
                                        yearMonth = month,
                                        onBack = { detailMonth = null },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                } else {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(innerPadding),
                                        onMonthClick = { detailMonth = it }
                                    )
                                }
                            }
                            NavigationTab.ADD -> {
                                AddExpenseScreen(
                                    viewModel = viewModel,
                                    onDone = { selectedTab = NavigationTab.HOME },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            NavigationTab.EXPORT -> {
                                ExportScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            NavigationTab.CALENDAR -> {
                                CalendarScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            NavigationTab.LEADERBOARD -> {
                                LeaderboardScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
