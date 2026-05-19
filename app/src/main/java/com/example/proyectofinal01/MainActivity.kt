package com.example.proyectofinal01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.proyectofinal01.vista.NavegacionPrincipal
import com.example.proyectofinal01.vista.PantallaLogin
import com.example.proyectofinal01.ui.theme.ProyectoFinal01Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinal01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var usuarioLogueado by remember {
                        mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                    }

                    if (usuarioLogueado) {
                        NavegacionPrincipal(
                            alCerrarSesion = {
                                usuarioLogueado = false
                            }
                        )
                    } else {
                        PantallaLogin(
                            alIniciarSesion = {
                                usuarioLogueado = true
                            }
                        )
                    }
                }
            }
        }
    }
}
