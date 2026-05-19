package com.example.proyectofinal01.vista

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.proyectofinal01.modelo.Receta
import com.example.proyectofinal01.modelo.baseDeRecetas

/**
 * Pantalla que enseña una lista con recetas y si le das click las expande
 */
@Composable
fun PantallaRecetas(){
    var recetaSeleccionada by remember { mutableStateOf<Receta?>(null) }

    if(recetaSeleccionada != null){
        DetalleReceta(
            receta = recetaSeleccionada!!,
            alVolver = { recetaSeleccionada = null }
        )
    }else{
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)){
            item{
                Text("Recetas Saludables", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Platos para tu día a día", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
            }
            items(baseDeRecetas){ receta ->
                ElementoReceta(receta = receta, alVerCompleta = { recetaSeleccionada = receta })
            }
        }
    }
}

/**
 * Función para hacer las recetas en la pantalla
 */
@Composable
fun ElementoReceta(receta: Receta, alVerCompleta: () -> Unit){
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expandido = !expandido }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ){
        Column{
            AsyncImage(
                model = receta.imagenUrl,
                contentDescription = receta.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(modifier = Modifier.padding(16.dp)){
                Text(text = receta.titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Default.AccessTime, contentDescription = "Tiempo", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(" ${receta.tiempoPreparacion}", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = "Calorías", modifier = Modifier.size(16.dp), tint = Color(0xFFF44336))
                    Text(" ${receta.calorias}", color = Color.Gray, fontSize = 14.sp)
                }

                if (expandido){
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(receta.descripcionBreve, fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = alVerCompleta,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("Ver pasos")
                    }
                }
            }
        }
    }
}

@Composable
fun DetalleReceta(receta: Receta, alVolver: () -> Unit){
    BackHandler { alVolver() }
    
    val estadoDesplazamiento = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()){
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)){
            AsyncImage(
                model = receta.imagenUrl,
                contentDescription = receta.titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = alVolver,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ){
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(estadoDesplazamiento)
                .padding(24.dp)
        ){
            Text(text = receta.titulo, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = "Tiempo", tint = MaterialTheme.colorScheme.primary)
                Text(" ${receta.tiempoPreparacion}", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(24.dp))
                Icon(Icons.Default.LocalFireDepartment, contentDescription = "Calorías", tint = Color(0xFFF44336))
                Text(" ${receta.calorias}", fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Ingredientes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            receta.ingredientes.forEach { ingrediente ->
                Row(modifier = Modifier.padding(vertical = 4.dp)){
                    Text("•", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                    Text(ingrediente)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Pasos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            receta.pasos.forEachIndexed { indice, paso ->
                Row(modifier = Modifier.padding(vertical = 6.dp)){
                    Text("${indice + 1}.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
                    Text(paso)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
