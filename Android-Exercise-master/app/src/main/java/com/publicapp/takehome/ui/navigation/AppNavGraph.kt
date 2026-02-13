package com.publicapp.takehome.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.publicapp.takehome.ui.addtask.AddTaskRoute
import com.publicapp.takehome.ui.tasklist.TaskListRoute

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.TaskList.route
    ) {
        composable(Routes.TaskList.route) {
            TaskListRoute(
                onAddClick = { navController.navigate(Routes.AddTask.route) }
            )
        }

        composable(Routes.AddTask.route) {
            AddTaskRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Routes(val route: String) {
    data object TaskList : Routes("task_list")
    data object AddTask : Routes("add_task")
}
