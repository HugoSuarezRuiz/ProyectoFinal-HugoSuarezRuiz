package com.example.proyectofinal01.vista

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Pantalla para ver y editar tu perfil. Donde se puede cambiar el nombre, la foto de perfil y cerrar sesión.
 */
@Composable
fun PantallaCuenta(alCerrarSesion: () -> Unit){
    val contexto = LocalContext.current
    val usuario = FirebaseAuth.getInstance().currentUser
    val baseDatos = FirebaseFirestore.getInstance()

    //Variables para guardar el estado
    var nombrePersonalizado by remember { mutableStateOf(usuario?.displayName ?: "") }
    var uriFotoPerfil by remember { mutableStateOf<Uri?>(null) }
    var estaEditando by remember { mutableStateOf(false) } //Para saber si se esta en modo edición
    var datosCargados by remember { mutableStateOf(false) }

    //Busca en firestore los datos del usuario y los guarda en las variables
    LaunchedEffect(usuario){
        if(usuario != null){
            baseDatos.collection("usuarios").document(usuario.uid)
                .get()
                .addOnSuccessListener{ doc ->
                    if(doc.exists()){
                        val nom = doc.getString("nombrePersonalizado")
                        val pic = doc.getString("fotoString")
                        if(!nom.isNullOrEmpty()) nombrePersonalizado = nom
                        if(!pic.isNullOrEmpty()){
                            try{
                                uriFotoPerfil = Uri.parse(pic)
                            } catch (e: Exception) {}
                        }
                    }
                    datosCargados = true
                }.addOnFailureListener{
                    datosCargados = true
                }
        }else{
            datosCargados = true
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ){uri: Uri? ->
        if(uri != null){
            try{
                contexto.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                uriFotoPerfil = uri
            }catch (e: Exception){
            }
        }
    }

    val nombreFinal = if(nombrePersonalizado.isNotEmpty()) nombrePersonalizado else "Usuario Desconocido"
    val fotoFinal = uriFotoPerfil ?: usuario?.photoUrl

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(!datosCargados){
            CircularProgressIndicator()
            return@Column
        }

        AsyncImage(
            model = fotoFinal,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable {
                    if(estaEditando) lanzadorGaleria.launch(arrayOf("image/*"))
                }
        )
        if(estaEditando){
            Text("Dale a la foto para cambiarla", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(estaEditando){
            OutlinedTextField(
                value = nombrePersonalizado,
                onValueChange = {nombrePersonalizado = it},
                label = {Text("Nombre")}
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                estaEditando = false
                if(usuario != null){
                    val datos = hashMapOf<String, Any>(
                        "nombrePersonalizado" to nombrePersonalizado
                    )
                    if (uriFotoPerfil != null){
                        datos["fotoString"] = uriFotoPerfil.toString()
                    }
                    baseDatos.collection("usuarios").document(usuario.uid).set(datos, SetOptions.merge())
                }
            }){
                Text("Guardar")
            }
        }else{
            Text(text = nombreFinal, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = usuario?.email ?: "", color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = {estaEditando = true }){
                Text("Editar perfil")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                cerrarSesionUsuario(contexto){
                    alCerrarSesion()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ){
            Text("Cerrar Sesión")
        }
    }
}

fun cerrarSesionUsuario(contexto: android.content.Context, alCompletar: () -> Unit){
    FirebaseAuth.getInstance().signOut()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
    val gsc = GoogleSignIn.getClient(contexto, gso)

    gsc.signOut().addOnCompleteListener{
        alCompletar()
    }
}
