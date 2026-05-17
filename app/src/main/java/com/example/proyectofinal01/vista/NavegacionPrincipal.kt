package com.example.proyectofinal01.vista

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Es la interfaz principal una vez ya registrados, tiene el menú de navegación con las diferentes
 * pestañas y se encarga de ir cambiando entre la seleccionado y la actual.
 */
@Composable
fun NavegacionPrincipal(alCerrarSesion: () -> Unit){
    // pestanaActual: Guarda qué botón del menú está seleccionado y empieza en el 2 que es el diario
    var pestanaActual by remember { mutableStateOf(2) }

    Scaffold(
        bottomBar = {
            NavigationBar{
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                    label = { Text("Mapa") },
                    selected = pestanaActual == 0,
                    onClick = { pestanaActual = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Recetas") },
                    label = { Text("Recetas") },
                    selected = pestanaActual == 1,
                    onClick = { pestanaActual = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Seguimiento") },
                    label = { Text("Seguimiento") },
                    selected = pestanaActual == 2,
                    onClick = { pestanaActual = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cuenta") },
                    label = { Text("Cuenta") },
                    selected = pestanaActual == 3,
                    onClick = { pestanaActual = 3 }
                )
            }
        }
    ) { espaciado ->
        //Box para que según el menú seleccionado salga su interfaz correspondiente
        Box(modifier = Modifier.padding(espaciado)){
            when (pestanaActual){
                0 -> PantallaMapa()
                1 -> PantallaRecetas()
                2 -> PantallaSeguimiento()
                3 -> PantallaCuenta(alCerrarSesion = alCerrarSesion)
            }
        }
    }
}
