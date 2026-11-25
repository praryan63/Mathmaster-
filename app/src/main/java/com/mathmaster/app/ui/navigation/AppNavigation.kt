package com.mathmaster.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.di.ServiceLocator
import com.mathmaster.app.domain.model.PracticeMode
import com.mathmaster.app.ui.screens.AnalyticsScreen
import com.mathmaster.app.ui.screens.HomeScreen
import com.mathmaster.app.ui.screens.IbpsScreen
import com.mathmaster.app.ui.screens.LearnScreen
import com.mathmaster.app.ui.screens.MistakesScreen
import com.mathmaster.app.ui.screens.PracticeScreen
import com.mathmaster.app.ui.screens.QuizSetupScreen
import com.mathmaster.app.ui.screens.ResultScreen
import com.mathmaster.app.ui.screens.SettingsScreen
import com.mathmaster.app.ui.screens.SpeedScreen
import com.mathmaster.app.ui.screens.TableDetailScreen
import com.mathmaster.app.ui.screens.TablesScreen
import com.mathmaster.app.viewmodel.AnalyticsViewModel
import com.mathmaster.app.viewmodel.HomeViewModel
import com.mathmaster.app.viewmodel.MistakesViewModel
import com.mathmaster.app.viewmodel.PracticeViewModel
import com.mathmaster.app.viewmodel.QuizSetupViewModel
import com.mathmaster.app.viewmodel.ResultViewModel
import com.mathmaster.app.viewmodel.SettingsViewModel
import com.mathmaster.app.viewmodel.SpeedViewModel
import com.mathmaster.app.viewmodel.TableDetailViewModel
import com.mathmaster.app.viewmodel.TablesViewModel
import com.mathmaster.app.viewmodel.viewModelFactory
import kotlinx.coroutines.launch

@Composable
fun MathMasterNavHost(navController: NavHostController, settings: SettingsState) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME
    val scope = rememberCoroutineScope()

    val bottomScreens = listOf(Routes.HOME, Routes.LEARN, Routes.TABLES, Routes.QUIZ_SETUP,
        Routes.ANALYTICS)
    val showBottomBar = currentRoute in bottomScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple("Home", Icons.Filled.Home, Routes.HOME),
                        Triple("Learn", Icons.Filled.MenuBook, Routes.LEARN),
                        Triple("Tables", Icons.Filled.GridView, Routes.TABLES),
                        Triple("Test", Icons.Filled.Quiz, Routes.QUIZ_SETUP),
                        Triple("Analytics", Icons.Filled.BarChart, Routes.ANALYTICS)
                    )
                    items.forEach { (label, icon, route) ->
                        val baseRoute = route.substringBefore("?")
                        val selected = currentRoute.substringBefore("?") == baseRoute
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(route) {
                                        popUpTo(Routes.HOME) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = Routes.HOME) {

                composable(Routes.HOME) {
                    val viewModel: HomeViewModel = viewModel(
                        factory = viewModelFactory {
                            HomeViewModel(ServiceLocator.repository, ServiceLocator.settings)
                        }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        settings = settings,
                        onNavigate = { route -> navController.navigate(route) },
                        onPractice = { topicId, mode ->
                            scope.launch { ServiceLocator.settings.setLastTopic(topicId) }
                            navController.navigate(Routes.practice(topicId, mode))
                        }
                    )
                }

                composable(Routes.LEARN) {
                    LearnScreen(
                        onNavigate = { route -> navController.navigate(route) },
                        onPractice = { topicId, mode ->
                            scope.launch { ServiceLocator.settings.setLastTopic(topicId) }
                            navController.navigate(Routes.practice(topicId, mode))
                        }
                    )
                }

                composable(Routes.TABLES) {
                    val viewModel: TablesViewModel = viewModel(
                        factory = viewModelFactory { TablesViewModel(ServiceLocator.repository) }
                    )
                    val tables by viewModel.uiState.collectAsStateWithLifecycle()
                    TablesScreen(
                        tables = tables,
                        onNavigate = { route -> navController.navigate(route) },
                        onPractice = { topicId, mode -> navController.navigate(Routes.practice(topicId, mode)) }
                    )
                }

                composable(
                    route = Routes.TABLE_DETAIL,
                    arguments = listOf(navArgument("number") { type = NavType.IntType })
                ) { entry ->
                    val number = entry.arguments?.getInt("number") ?: 1
                    val viewModel: TableDetailViewModel = viewModel(
                        factory = viewModelFactory { TableDetailViewModel(number, ServiceLocator.repository) }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    TableDetailScreen(
                        state = state,
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) },
                        onPractice = { topicId, mode -> navController.navigate(Routes.practice(topicId, mode)) }
                    )
                }

                composable(Routes.IBPS) {
                    IbpsScreen(
                        onNavigate = { route -> navController.navigate(route) },
                        onPractice = { topicId, mode ->
                            scope.launch { ServiceLocator.settings.setLastTopic(topicId) }
                            navController.navigate(Routes.practice(topicId, mode))
                        }
                    )
                }

                composable(Routes.MISTAKES) {
                    val viewModel: MistakesViewModel = viewModel(
                        factory = viewModelFactory { MistakesViewModel(ServiceLocator.repository) }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    MistakesScreen(
                        state = state,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onPractice = { topicId, mode -> navController.navigate(Routes.practice(topicId, mode)) }
                    )
                }

                composable(Routes.SPEED) {
                    val viewModel: SpeedViewModel = viewModel(
                        factory = viewModelFactory { SpeedViewModel(ServiceLocator.repository) }
                    )
                    val topics by viewModel.uiState.collectAsStateWithLifecycle()
                    SpeedScreen(
                        topics = topics,
                        onBack = { navController.popBackStack() },
                        onPractice = { topicId, mode -> navController.navigate(Routes.practice(topicId, mode, 20, true)) }
                    )
                }

                composable(
                    route = Routes.QUIZ_SETUP,
                    arguments = listOf(navArgument("topic") {
                        type = NavType.StringType
                        defaultValue = "mixed"
                    })
                ) { entry ->
                    val topicId = entry.arguments?.getString("topic") ?: "mixed"
                    val viewModel: QuizSetupViewModel = viewModel(
                        factory = viewModelFactory { QuizSetupViewModel(topicId, ServiceLocator.settings) }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    QuizSetupScreen(
                        state = state,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onStart = { selectedTopic, count, timer ->
                            scope.launch { ServiceLocator.settings.setLastTopic(selectedTopic) }
                            navController.navigate(Routes.practice(selectedTopic, PracticeMode.QUIZ, count, timer))
                        }
                    )
                }

                composable(
                    route = Routes.PRACTICE,
                    arguments = listOf(
                        navArgument("topicId") { type = NavType.StringType },
                        navArgument("mode") { type = NavType.StringType },
                        navArgument("count") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("timer") { type = NavType.BoolType; defaultValue = false }
                    )
                ) { entry ->
                    val topicId = entry.arguments?.getString("topicId") ?: "addition"
                    val mode = runCatching { PracticeMode.valueOf(entry.arguments?.getString("mode") ?: "PRACTICE") }
                        .getOrDefault(PracticeMode.PRACTICE)
                    val count = entry.arguments?.getInt("count") ?: 0
                    val timer = entry.arguments?.getBoolean("timer") ?: false
                    val viewModel: PracticeViewModel = viewModel(
                        factory = viewModelFactory {
                            PracticeViewModel(
                                topicId = topicId,
                                mode = mode,
                                questionCount = count,
                                timerRequested = timer,
                                repository = ServiceLocator.repository,
                                settingsRepository = ServiceLocator.settings,
                                questionBank = ServiceLocator.questionBank,
                                feedbackManager = ServiceLocator.feedbackManager
                            )
                        }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    PracticeScreen(
                        state = state,
                        settings = settings,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onQuizFinished = { sessionId ->
                            navController.navigate(Routes.result(sessionId)) {
                                popUpTo(Routes.practice(topicId, mode, count, timer)) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Routes.RESULT,
                    arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
                ) { entry ->
                    val sessionId = entry.arguments?.getString("sessionId").orEmpty()
                    val viewModel: ResultViewModel = viewModel(
                        factory = viewModelFactory { ResultViewModel(sessionId, ServiceLocator.repository) }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    ResultScreen(
                        state = state,
                        onBack = { navController.popBackStack() },
                        onHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                        onRetry = { topicId ->
                            navController.navigate(Routes.practice(topicId, PracticeMode.QUIZ, 10, true))
                        }
                    )
                }

                composable(Routes.ANALYTICS) {
                    val viewModel: AnalyticsViewModel = viewModel(
                        factory = viewModelFactory {
                            AnalyticsViewModel(ServiceLocator.repository, ServiceLocator.settings)
                        }
                    )
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    AnalyticsScreen(state = state, onBack = { navController.popBackStack() })
                }

                composable(Routes.SETTINGS) {
                    val viewModel: SettingsViewModel = viewModel(
                        factory = viewModelFactory {
                            SettingsViewModel(ServiceLocator.repository, ServiceLocator.settings)
                        }
                    )
                    SettingsScreen(
                        state = settings,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
