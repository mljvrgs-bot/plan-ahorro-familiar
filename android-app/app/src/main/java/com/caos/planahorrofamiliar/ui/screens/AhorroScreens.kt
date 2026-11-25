package com.caos.planahorrofamiliar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caos.planahorrofamiliar.data.model.Member
import com.caos.planahorrofamiliar.ui.viewmodel.AhorroUiState
import java.text.NumberFormat
import java.util.Locale

val VerdeAhorro = Color(0xFF0D8A4E)

fun formatoPesos(valor: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("es", "CO"))
    format.maximumFractionDigits = 0
    return format.format(valor)
}

// PANTALLA 1: Lista de Planes
@Composable
fun PlanesScreen(
    uiState: AhorroUiState,
    onCrearPlanClick: () -> Unit,
    onPlanClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = "Plan Familiar\nde Ahorro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCrearPlanClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeAhorro),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Crear plan", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Planes de ahorro",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = VerdeAhorro, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.planes) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlanClick(plan.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = plan.motive?.ifBlank { "Ahorro familiar" } ?: "Ahorro familiar",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatoPesos(plan.targetAmount),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${plan.months} meses", fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

// PANTALLA 2: Crear Plan
@Composable
fun CrearPlanScreen(
    isLoading: Boolean,
    onBack: () -> Unit,
    onGuardarPlan: (motivo: String, meta: String, meses: String, miembros: String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }
    var meses by remember { mutableStateOf("") }
    var miembros by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Text("Crear plan", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Motivo", fontSize = 14.sp)
        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            placeholder = { Text("Ej: Ahorro para viaje familiar") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Meta", fontSize = 14.sp)
        OutlinedTextField(
            value = meta,
            onValueChange = { meta = it },
            placeholder = { Text("Ej: 5000000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Meses", fontSize = 14.sp)
        OutlinedTextField(
            value = meses,
            onValueChange = { meses = it },
            placeholder = { Text("Ej: 12") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Miembros (separados por coma)", fontSize = 14.sp)
        OutlinedTextField(
            value = miembros,
            onValueChange = { miembros = it },
            placeholder = { Text("Ej: Jorge, Ana, Luis") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onGuardarPlan(motivo, meta, meses, miembros) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeAhorro),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (isLoading) "Guardando..." else "Crear", fontSize = 16.sp, color = Color.White)
        }
    }
}

// PANTALLA 3: Detalle del Plan, Miembros y Pagos
@Composable
fun DetallePlanScreen(
    uiState: AhorroUiState,
    onBack: () -> Unit,
    onRegistrarPagoClick: () -> Unit
) {
    val plan = uiState.planSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        if (plan != null) {
            Text(plan.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${plan.months} meses", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(formatoPesos(plan.targetAmount), fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { uiState.progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = VerdeAhorro,
                trackColor = Color(0xFFE0E0E0)
            )
            Text(
                text = "Ahorrado: $${formatoPesos(uiState.totalAhorrado)} (${(uiState.progreso * 100).toInt()}%)",
                fontSize = 13.sp,
                color = VerdeAhorro,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Miembros", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.miembrosDelPlan) { miembro ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCE7E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF3E564C))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(miembro.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (uiState.pagosDelPlan.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Historial de pagos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    items(uiState.pagosDelPlan) { pago ->
                        val nombrePagador = uiState.miembrosDelPlan.find { it.id == pago.memberId }?.name ?: "Miembro"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(nombrePagador, fontSize = 14.sp)
                            Text("+$${formatoPesos(pago.amount)}", fontWeight = FontWeight.Bold, color = VerdeAhorro)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRegistrarPagoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeAhorro),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Registrar pago", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

// PANTALLA 4: Registrar Pago
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarPagoScreen(
    miembros: List<Member>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRegistrar: (memberId: String, monto: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var miembroSeleccionado by remember { mutableStateOf(miembros.firstOrNull()) }
    var monto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Text("Registrar pago", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Miembro", fontSize = 14.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = miembroSeleccionado?.name ?: "Selecciona un miembro",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                miembros.forEach { miembro ->
                    DropdownMenuItem(
                        text = { Text(miembro.name) },
                        onClick = {
                            miembroSeleccionado = miembro
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Monto", fontSize = 14.sp)
        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            prefix = { Text("$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                miembroSeleccionado?.let { onRegistrar(it.id, monto) }
            },
            enabled = !isLoading && miembroSeleccionado != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeAhorro),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (isLoading) "Registrando..." else "Registrar", fontSize = 16.sp, color = Color.White)
        }
    }
}