package com.example.proyectofinal01.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Pantalla donde sale el mapa interactivo con los gimnasios cercanos mediante la API de Google Maps
 */
@Composable
fun PantallaMapa(){
    val contexto = LocalContext.current

    //Se inicializa el buscador de lugares de la API
    if(!Places.isInitialized()){
        Places.initialize(contexto, "AIzaSyADd6OKbfGL6aXlCgr8-GtBJJw774-FyqE")
    }

    val clienteLugares = remember { Places.createClient(contexto) }
    //Coordenadas donde empieza centrado el mapa, en este caso Granada
    val centroMapa = LatLng(37.178, -3.598)
    //Variable que va guardando donde va moviendo la cámara el usuario
    val estadoCamara = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(centroMapa, 6f)
    }

    var listaGimnasios by remember { mutableStateOf(listOf<Place>()) }
    var cargando by remember { mutableStateOf(false) }
    var mapaCargado by remember { mutableStateOf(false) }

    //Esto ve si el usuario mueve el mapa y lo registra
    LaunchedEffect(estadoCamara.isMoving, mapaCargado){
        if(!mapaCargado) return@LaunchedEffect
        val zoomActual = estadoCamara.position.zoom
        
        //Si el mapa no esta siendo movido y el zoom es mayor a 12 se enseñan los puntos de hasta 2km del centro actual
        if(!estadoCamara.isMoving && zoomActual > 12){
            val centroActual = estadoCamara.position.target
            val limitesBusqueda = CircularBounds.newInstance(centroActual, 2000.0)
            val camposLugar = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            //La petición a la API de que se quiere buscar y como
            val peticionBusqueda = SearchNearbyRequest.builder(limitesBusqueda, camposLugar)
                .setIncludedTypes(listOf("gym"))
                .setMaxResultCount(20)
                .build()

            cargando = true
            clienteLugares.searchNearby(peticionBusqueda)
                .addOnSuccessListener{ respuesta ->
                    listaGimnasios = respuesta.places
                    cargando = false
                }
                .addOnFailureListener{
                    cargando = false
                }
        }else{
            if(zoomActual <= 12){
                listaGimnasios = emptyList()
            }
        }
    }



    Box(modifier = Modifier.fillMaxSize()){
        if(!mapaCargado){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .clickable { mapaCargado = true },
                contentAlignment = Alignment.Center
            ){
                Button(onClick = { mapaCargado = true }){
                    Text("Cargar Mapa")
                }
            }
        }else{
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = estadoCamara
            ){
                listaGimnasios.forEach{ lugar ->
                    val coordenadas = lugar.latLng
                    if(coordenadas != null){
                        Marker(
                            state = MarkerState(position = coordenadas),
                            title = lugar.name ?: "Gimnasio",
                            onClick = { marker ->
                                marker.showInfoWindow()
                                true
                            }
                        )
                    }
                }
            }

            //Si estas muy lejos te dice que hagas zoom para ver los gimnasios
            if(estadoCamara.position.zoom <= 12){
                Text(
                    text = "Haz zoom para ver los gimnasios",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }else{
                if(cargando){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
