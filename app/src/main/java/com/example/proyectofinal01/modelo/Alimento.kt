package com.example.proyectofinal01.modelo

/**
 * Plantilla para los alimentos
 */
data class Alimento(
    val id: String = "",
    val nombre: String = "",
    val calorias: Int = 0,
    val fecha: String = "",
    //Estos son opcionales
    val proteinas: Double = 0.0,
    val carbohidratos: Double = 0.0,
    val grasas: Double = 0.0
)
