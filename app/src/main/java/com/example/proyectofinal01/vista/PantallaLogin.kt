package com.example.proyectofinal01.vista

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal01.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Pantalla inicial para iniciar sesión con Google.
 */
@Composable
fun PantallaLogin(alIniciarSesion: (FirebaseUser?) -> Unit){
    val contexto = LocalContext.current
    val autenticacion = remember {FirebaseAuth.getInstance()}
    val tokenGoogle = "54089167098-i4vi8nog1j2jre8ppl1l7ei2k3fv2pv0.apps.googleusercontent.com"

    //Lanzador que abre la pestaña de inicio de sesión de google
    val lanzador = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){ resultado ->
        val tarea = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
        try{
            val cuenta = tarea.getResult(ApiException::class.java)!!
            //Se le pasan las credenciales de google a firebase
            val credencial = GoogleAuthProvider.getCredential(cuenta.idToken, null)

            //Se inicia sesión con las credenciales pasadas
            autenticacion.signInWithCredential(credencial)
                .addOnCompleteListener { tareaAuth ->
                    if(tareaAuth.isSuccessful){
                        alIniciarSesion(autenticacion.currentUser)
                    }
                }
        }catch(e: ApiException){
            Log.e("AUTH", "Error de Google: ${e.statusCode}")
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo CapiLorias",
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 24.dp)
        )
        
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Capi",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Lorias",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                val opcionesGoogle = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(tokenGoogle)
                    .requestEmail()
                    .build()
                val clienteGoogle = GoogleSignIn.getClient(contexto, opcionesGoogle)
                lanzador.launch(clienteGoogle.signInIntent)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(200.dp)
                .height(48.dp)
        ){
            Text("Iniciar sesión")
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}
