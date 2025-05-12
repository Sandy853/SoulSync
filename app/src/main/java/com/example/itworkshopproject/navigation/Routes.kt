package com.example.itworkshopproject.navigation

sealed class Routes(val route: String) {
    object SplashScreen : Routes("splash")
    object SignIn : Routes("sign_in")
    object SignUp : Routes("sign_up")
    object ForgotPassword : Routes("forgot_password")
    object Home : Routes("home")
    object MoodAnalysis : Routes("mood_analysis")
    object Diary : Routes("diary")
    object Profile : Routes("profile")
    object MemoryCapsule : Routes("memory_capsule")
    object HabitHub : Routes("habit_hub")
    object StudentNotes : Routes("student_notes_screen") // ✅ Correct
    object TimeTableScheduler : Routes("timetable_scheduler")
    object DeadlineDominator : Routes("deadline_dominator")
    object DeveloperScreen : Routes("developer_screen")


}
