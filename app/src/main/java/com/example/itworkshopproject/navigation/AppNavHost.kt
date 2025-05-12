package com.example.itworkshopproject.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.itworkshopproject.model.Mood
import com.example.itworkshopproject.screens.auth.*
import com.example.itworkshopproject.screens.habit.HabitHubScreen
import com.example.itworkshopproject.screens.home.*
import com.example.itworkshopproject.screens.home.components.getNickname
import com.example.itworkshopproject.screens.home.components.saveNickname
import com.example.itworkshopproject.screens.splash.NicknameDialog
import com.example.itworkshopproject.screens.splash.SplashScreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showNicknameDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }

    val taskDataStore = TaskDataStore(context)

    NavHost(
        navController = navController,
        startDestination = Routes.SplashScreen.route
    ) {
        // Splash Screen
        composable(Routes.SplashScreen.route) {
            SplashScreen(navController)
        }

        // Sign In Screen
        composable(Routes.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    coroutineScope.launch {
                        val savedName = getNickname(context)
                        if (savedName.isNullOrBlank()) {
                            showNicknameDialog = true
                        } else {
                            nickname = savedName
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.SignIn.route) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Routes.SignUp.route) },
                onNavigateToForgotPassword = { navController.navigate(Routes.ForgotPassword.route) }
            )
        }

        // Sign Up Screen
        composable(Routes.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    showNicknameDialog = true
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.popBackStack() }
            )
        }

        // Forgot Password Screen
        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetLinkSent = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Home Screen
        composable(Routes.Home.route) {
            HomeScreen(
                username = nickname.ifBlank { "Buddy" },
                navController = navController
            )
        }

        // Mood Analysis Screen
        composable(Routes.MoodAnalysis.route) {
            val moodCounts = remember { mutableStateOf<Map<Mood, Int>>(emptyMap()) }
            LaunchedEffect(Unit) {
                moodCounts.value = MoodDataStore.getMoodCounts(context)
            }
            MoodAnalysisScreen(
                moodCounts = moodCounts.value,
                onJourneyClick = { /* Optional click logic */ }
            )
        }

        // Profile Screen
        composable(Routes.Profile.route) {
            PremiumProfileScreen(navController = navController)
        }

        // Diary Screen
        composable(Routes.Diary.route) {
            DiaryScreen(navController = navController)
        }

        // Habit Hub Screen
        composable(Routes.HabitHub.route) {
            HabitHubScreen(navController = navController)
        }

        // Memory Capsule Screen
        composable(Routes.MemoryCapsule.route) {
            MemoryCapsuleScreen(navController = navController)
        }

        // Deadline Dominator Screen
        composable(Routes.DeadlineDominator.route) {
            DeadlineDominatorScreen(
                navController = navController,
                taskDataStore = taskDataStore
            )
        }

        // Student Notes Screen
        composable(Routes.StudentNotes.route) {
            StudentNotesScreen(navController = navController)
        }

        // Time Table Scheduler Screen
        composable(Routes.TimeTableScheduler.route) {
            TimeTableScreen(navController)
        }

        // Developer Screen
        composable(Routes.DeveloperScreen.route) {
            DeveloperScreen()
        }
    }

    // Show Nickname Dialog if Needed
    if (showNicknameDialog) {
        NicknameDialog(
            onSave = { enteredName ->
                coroutineScope.launch {
                    saveNickname(context, enteredName)
                    nickname = enteredName
                    showNicknameDialog = false
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        )
    }
}
