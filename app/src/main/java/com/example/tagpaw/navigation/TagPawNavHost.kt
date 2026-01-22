package com.example.tagpaw.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tagpaw.Ui.addpet.AddPetScreen
import com.example.tagpaw.Ui.emergency.EmergencyEditScreen
import com.example.tagpaw.Ui.home.HomeScreen
import com.example.tagpaw.Ui.detail.PetDetailScreen
import com.example.tagpaw.Ui.tag.TagRegisterScreen

@Composable
fun TagPawApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TagPawRoutes.HOME,
        modifier = modifier
    ) {
        composable(TagPawRoutes.HOME) {
            HomeScreen(
                onAddPetClick = { navController.navigate(TagPawRoutes.ADD_PET) },
                onPetClick = { petId ->
                    navController.navigate(TagPawRoutes.petDetail(petId))
                }
            )
        }

        composable(TagPawRoutes.ADD_PET) {
            AddPetScreen(
                onPetSaved = { newPetId ->
                    navController.navigate(TagPawRoutes.tagRegister(newPetId)) {
                        popUpTo(TagPawRoutes.HOME) { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = TagPawRoutes.TAG_REGISTER,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: 0L
            TagRegisterScreen(
                petId = petId,
                onDone = {
                    navController.navigate(TagPawRoutes.petDetail(petId)) {
                        popUpTo(TagPawRoutes.HOME) { inclusive = false }
                    }
                },
                onBackClick = { 
                    navController.navigate(TagPawRoutes.petDetail(petId)) {
                        popUpTo(TagPawRoutes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = TagPawRoutes.PET_DETAIL,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: 0L
            PetDetailScreen(
                petId = petId,
                onEditEmergencyClick = {
                    navController.navigate(TagPawRoutes.emergencyEdit(petId))
                },
                onRegisterTagClick = {
                    navController.navigate(TagPawRoutes.tagRegister(petId))
                },
                onBackClick = { 
                    navController.navigate(TagPawRoutes.HOME) {
                        popUpTo(TagPawRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = TagPawRoutes.EMERGENCY_EDIT,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: 0L
            EmergencyEditScreen(
                petId = petId,
                onDone = {
                    // 이제 정보 수정 완료 후 강제로 태그 화면에 가지 않고 상세 화면으로 돌아갑니다.
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
