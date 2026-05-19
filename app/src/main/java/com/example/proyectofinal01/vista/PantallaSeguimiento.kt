package com.example.proyectofinal01.vista

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal01.modelo.Alimento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Pantalla princiapl en la que se encuentra el seguimiento diario y la opción de añadir alimentos
 */
@Composable
fun PantallaSeguimiento(){
    var mostrandoInsertar by remember { mutableStateOf(false) }
    //Se hace el calendario y se usa la fecha con formato para la base de datos y con otro formato para enseñarla
    val calendario = remember { Calendar.getInstance() }
    val formatoBD = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val formatoVisual = remember { SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES")) }
    //La fecha que el usuario ve, por defecto es hoy
    var fechaBD by remember { mutableStateOf(formatoBD.format(calendario.time)) }
    var fechaVisual by remember { mutableStateOf(formatoVisual.format(calendario.time)) }

    if(mostrandoInsertar){
        AñadirAlimento(
            fechaSeleccionada = fechaBD,
            alVolver = { mostrandoInsertar = false }
        )
    }else{
        ResumenDiario(
            fechaBD = fechaBD,
            fechaVisual = fechaVisual,
            alCambiarFecha = { nuevaFechaBD, nuevaFechaVisual ->
                fechaBD = nuevaFechaBD
                fechaVisual = nuevaFechaVisual
            },
            alIrAInsertar = { mostrandoInsertar = true }
        )
    }
}

// Función que muestra lo consumido diariamente y las calorías y macros
@Composable
fun ResumenDiario(
    fechaBD: String,
    fechaVisual: String,
    alCambiarFecha: (String, String) -> Unit,
    alIrAInsertar: () -> Unit
) {
    val contexto = LocalContext.current
    val usuario = FirebaseAuth.getInstance().currentUser
    val baseDatos = FirebaseFirestore.getInstance()
    //Variable donde se guardan los alimentos registrados en la base de datos
    var listaAlimentos by remember { mutableStateOf(emptyList<Alimento>()) }
    val caloriasTotales = listaAlimentos.sumOf { it.calorias }
    val calendario = Calendar.getInstance()
    //El calendario que sale para elegir una fecha
    val dialogoFecha = DatePickerDialog(
        contexto,
        { _, year, month, dayOfMonth ->
            calendario.set(year, month, dayOfMonth)
            val fBD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendario.time)
            val fVisual = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES")).format(calendario.time)
            alCambiarFecha(fBD, fVisual)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    //Esto esta atento de si se cambia el día o añade/borra algo y al hacerlo consulta a la base de datos o guarda los cambios
    LaunchedEffect(usuario, fechaBD){
        if(usuario != null){
            baseDatos.collection("usuarios")
                .document(usuario.uid)
                .collection("alimentos_diarios")
                .whereEqualTo("fecha", fechaBD)
                .addSnapshotListener { snapshot, error ->
                    if(error != null) return@addSnapshotListener
                    if(snapshot != null){
                        val alimentosCargados = snapshot.documents.mapNotNull { doc ->
                            val alimento = doc.toObject(Alimento::class.java)
                            alimento?.copy(id = doc.id)
                        }
                        listaAlimentos = alimentosCargados
                    }
                }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(onClick = { dialogoFecha.show() }){
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(fechaVisual)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Calorías Consumidas", fontSize = 16.sp, color = Color.Gray)
        Text("$caloriasTotales kcal", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = alIrAInsertar, modifier = Modifier.fillMaxWidth().height(50.dp)){
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir Alimento")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Registros de hoy", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)){
            if(listaAlimentos.isEmpty()){
                item { Text("No hay registros de este día.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp)) }
            }else{
                items(listaAlimentos){ alimento ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(modifier = Modifier.weight(1f)){
                            Text(alimento.nombre, fontSize = 16.sp)
                            Text("${alimento.calorias} kcal", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        IconButton(onClick = {
                            if(usuario != null && alimento.id.isNotEmpty()){
                                baseDatos.collection("usuarios").document(usuario.uid)
                                    .collection("alimentos_diarios").document(alimento.id).delete()
                            }
                        }){
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Divider()
                }
            }
        }


        if (caloriasTotales > 0) {
            val totalProtes = listaAlimentos.sumOf { it.proteinas }
            val totalCarbos = listaAlimentos.sumOf { it.carbohidratos }
            val totalGrasas = listaAlimentos.sumOf { it.grasas }

            //Se aplica la regla para calcular los % de macrosque es: Proteína = 4 kcal/g, Carbos = 4 kcal/g, Grasas = 9 kcal/g
            val caloriasProtes = totalProtes * 4
            val caloriasCarbos = totalCarbos * 4
            val caloriasGrasas = totalGrasas * 9
            val macroTotalReal = caloriasProtes + caloriasCarbos + caloriasGrasas

            val pctProtes = if(macroTotalReal > 0) (caloriasProtes / macroTotalReal) * 100 else 0.0
            val pctCarbos = if(macroTotalReal > 0) (caloriasCarbos / macroTotalReal) * 100 else 0.0
            val pctGrasas = if(macroTotalReal > 0) (caloriasGrasas / macroTotalReal) * 100 else 0.0

            if(macroTotalReal > 0){
                Spacer(modifier = Modifier.height(16.dp))
                Text("Macros", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().height(24.dp).clip(CircleShape)){
                    if(pctProtes > 0){
                        Box(modifier = Modifier.fillMaxHeight().weight(pctProtes.toFloat()).background(Color(0xFF4CAF50)))
                    }
                    if(pctCarbos > 0){
                        Box(modifier = Modifier.fillMaxHeight().weight(pctCarbos.toFloat()).background(Color(0xFF2196F3)))
                    }
                    if(pctGrasas > 0){
                        Box(modifier = Modifier.fillMaxHeight().weight(pctGrasas.toFloat()).background(Color(0xFFFFC107)))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                    Text("P: ${"%.1f".format(Locale.US, pctProtes)}%", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("C: ${"%.1f".format(Locale.US, pctCarbos)}%", color = Color(0xFF2196F3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("G: ${"%.1f".format(Locale.US, pctGrasas)}%", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Pantalla con un formulario para registrar un nuevo alimento
@Composable
fun AñadirAlimento(fechaSeleccionada: String, alVolver: () -> Unit){
    val contexto = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var mostrarMacros by remember { mutableStateOf(false) }
    var proteinas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }

    // Regla para asegurarse de que en los macros solo se escriben números y puntos decimales
    val patronDecimal = Regex("^\\d*\\.?\\d*$")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Añadir a consumo diario", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del alimento") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = calorias,
            onValueChange = { nuevoValor ->
                if(nuevoValor.isEmpty() || nuevoValor.all { it.isDigit() }){
                    calorias = nuevoValor
                }
            },
            label = { Text("Calorías") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            Checkbox(checked = mostrarMacros, onCheckedChange = { mostrarMacros = it })
            Text("Añadir macros (Opcional)")
        }

        if(mostrarMacros){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                OutlinedTextField(
                    value = proteinas,
                    onValueChange = { if (it.matches(patronDecimal)) proteinas = it },
                    label = { Text("Proteínas (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = carbohidratos,
                    onValueChange = { if (it.matches(patronDecimal)) carbohidratos = it },
                    label = { Text("Carbos (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
                )
                OutlinedTextField(
                    value = grasas,
                    onValueChange = { if (it.matches(patronDecimal)) grasas = it },
                    label = { Text("Grasas (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
            OutlinedButton(onClick = alVolver) { Text("Cancelar") }

            Button(onClick = {
                //Se comprueba que todos los campos esten correctos
                val cals = calorias.toIntOrNull()
                if(nombre.isNotBlank() && cals != null && cals >= 0){
                    val usuario = FirebaseAuth.getInstance().currentUser
                    val baseDatos = FirebaseFirestore.getInstance()

                    if(usuario != null){
                        val nuevoAlimento = Alimento(
                            id = "",
                            nombre = nombre,
                            calorias = cals,
                            fecha = fechaSeleccionada,
                            proteinas = if (mostrarMacros) proteinas.toDoubleOrNull() ?: 0.0 else 0.0,
                            carbohidratos = if (mostrarMacros) carbohidratos.toDoubleOrNull() ?: 0.0 else 0.0,
                            grasas = if (mostrarMacros) grasas.toDoubleOrNull() ?: 0.0 else 0.0
                        )

                        // Se guarda el alimento en la base de datos (se guarda localmente si no hay conexión y se sincronizará después)
                        baseDatos.collection("usuarios").document(usuario.uid)
                            .collection("alimentos_diarios").add(nuevoAlimento)
                        
                        android.widget.Toast.makeText(contexto, "Añadido", android.widget.Toast.LENGTH_SHORT).show()
                        alVolver()
                    }
                }else{
                    android.widget.Toast.makeText(contexto, "Datos inválidos", android.widget.Toast.LENGTH_SHORT).show()
                }
            }) { Text("Guardar") }
        }
    }
}
