package com.caos.planahorrofamiliar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caos.planahorrofamiliar.data.repository.AhorroRepository
import com.caos.planahorrofamiliar.ui.screens.CrearPlanScreen
import com.caos.planahorrofamiliar.ui.screens.DetallePlanScreen
import com.caos.planahorrofamiliar.ui.screens.PlanesScreen
import com.caos.planahorrofamiliar.ui.screens.RegistrarPagoScreen
import com.caos.planahorrofamiliar.ui.viewmodel.AhorroViewModel
import com.caos.planahorrofamiliar.data.remote.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AhorroRepository(RetrofitClient.apiService)

        setContent {
            val navController = rememberNavController()
            val viewModel: AhorroViewModel = viewModel(
                factory = AhorroViewModel.Factory(repository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.limpiarError()
                }
            }

            NavHost(navController = navController, startDestination = "planes") {
                composable("planes") {
                    PlanesScreen(
                        uiState = uiState,
                        onCrearPlanClick = { navController.navigate("crear_plan") },
                        onPlanClick = { planId ->
                            viewModel.cargarDetallePlan(planId)
                            navController.navigate("detalle_plan/$planId")
                        }
                    )
                }
                composable("crear_plan") {
                    CrearPlanScreen(
                        isLoading = uiState.isLoading,
                        onBack = { navController.popBackStack() },
                        onGuardarPlan = { motivo, meta, meses, miembros ->
                            viewModel.crearPlan(motivo, meta, meses, miembros) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
                composable("detalle_plan/{planId}") { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("planId") ?: ""
                    DetallePlanScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onRegistrarPagoClick = { navController.navigate("registrar_pago/$planId") }
                    )
                }
                composable("registrar_pago/{planId}") { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("planId") ?: ""
                    RegistrarPagoScreen(
                        miembros = uiState.miembrosDelPlan,
                        isLoading = uiState.isLoading,
                        onBack = { navController.popBackStack() },
                        onRegistrar = { memberId, monto ->
                            viewModel.registrarPago(planId, memberId, monto) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}