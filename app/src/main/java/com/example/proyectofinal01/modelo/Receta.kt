package com.example.proyectofinal01.modelo

/**
 * Plantilla de las recetas
 */
data class Receta(
    val id: String,
    val titulo: String,
    val descripcionBreve: String,
    val tiempoPreparacion: String,
    val calorias: String,
    val imagenUrl: String,
    val ingredientes: List<String>,
    val pasos: List<String>
)

//Lista creada a mano con una lista de recetas, si despues se quisiera hacer mejor se podría automatizar con otra API
val baseDeRecetas = listOf(
    Receta(
        id = "1",
        titulo = "Ensalada César Saludable",
        descripcionBreve = "Ligera y alta en proteínas, perfecta para cenar.",
        tiempoPreparacion = "15 min",
        calorias = "320 kcal",
        imagenUrl = "https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480_1_5x/img/recipe/ras/Assets/b876d8ea-fc9b-4b04-9958-9c70fe1c74e0/Derivates/fb3399fa-df15-4d0d-9beb-83a79a37a16e.jpg",
        ingredientes = listOf("1 Pechuga de pollo a la plancha", "Lechuga romana picada", "2 cdas Yogurt griego natural", "Queso parmesano rallado", "Picatostes integrales"),
        pasos = listOf("Corta la pechuga de pollo en tiras o cubos.", "En un bol grande, mezcla la lechuga con el pollo.", "Haz la salsa mezclando el yogurt griego con un poco de limón, ajo en polvo y sal.", "Baña la ensalada con la salsa y añade parmesano y picatostes por encima.")
    ),
    Receta(
        id = "2",
        titulo = "Pollo al Horno con Patatas",
        descripcionBreve = "Un clásico equilibrado y muy fácil de preparar para comer.",
        tiempoPreparacion = "45 min",
        calorias = "450 kcal",
        imagenUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=800&q=80",
        ingredientes = listOf("2 Pechugas de pollo enteras", "2 Patatas medianas", "Aceite de oliva virgen extra", "Romero y Tomillo", "Sal y pimienta"),
        pasos = listOf("Precalienta el horno a 200ºC.", "Corta las patatas en gajos finos y colócalas en una bandeja.", "Pon el pollo sobre las patatas, y rocía todo con un poco de aceite.", "Espolvorea generosamente el romero, tomillo, sal y pimienta.", "Hornea durante 35-40 minutos hasta que el pollo esté dorado.")
    ),
    Receta(
        id = "3",
        titulo = "Avena con Plátano y Nueces",
        descripcionBreve = "Desayuno energético, ideal antes de entrenar duro.",
        tiempoPreparacion = "10 min",
        calorias = "380 kcal",
        imagenUrl = "https://storage.googleapis.com/fitia_recipe_images/GR-R-V-00004813%2Fv2%2Frect.jpeg",
        ingredientes = listOf("50g Copos de avena", "1 Plátano maduro", "150ml Leche (o bebida vegetal)", "Un puñado de nueces picadas", "Canela al gusto"),
        pasos = listOf("Calienta la leche en un cazo o microondas.", "Añade los copos de avena y remueve hasta que espese y absorba el líquido.", "Corta el plátano en rodajas finas.", "Sirve la avena en un bol, decora con el plátano, nueces y espolvorea canela.")
    ),
    Receta(
        id = "4",
        titulo = "Salmón a la Plancha con Espárragos",
        descripcionBreve = "Cargado de Omega-3 y grasas saludables.",
        tiempoPreparacion = "20 min",
        calorias = "410 kcal",
        imagenUrl = "https://jetextramar.com/wp-content/uploads/2021/11/receta-de-rodaja-de-salm%C3%B3n-jet-extramar.jpg",
        ingredientes = listOf("1 Lomo de salmón fresco", "1 Manojo de espárragos trigueros", "1/2 Limón", "Pimienta negra", "Aceite de oliva"),
        pasos = listOf("Lava los espárragos y quita la parte dura del tallo.", "En una sartén con unas gotas de aceite, haz los espárragos a fuego medio por 8 minutos.", "En otra sartén, pon el salmón primero por la parte de la piel.", "Cocina 4 minutos por lado, añade un chorrito de limón al final y sirve junto a los espárragos.")
    ),
    Receta(
        id = "5",
        titulo = "Tortitas de Avena y Claras",
        descripcionBreve = "Altas en proteína pero saben a fin de semana.",
        tiempoPreparacion = "15 min",
        calorias = "290 kcal",
        imagenUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgaubVWO-PiIyyAWH96C3D1olUDzWKpyeSMw&s",
        ingredientes = listOf("4 Claras de huevo", "40g Harina de avena", "1 cda Edulcorante líquido", "Esencia de vainilla", "Chocolate 85% derretido (opcional)"),
        pasos = listOf("Bate las claras de huevo junto con la harina de avena, edulcorante y vainilla hasta lograr masa homogénea.", "Calienta una sartén antiadherente a fuego medio-alto.", "Vierte despacio porciones de masa para formar las tortitas.", "Vuelta y vuelta cuando veas burbujas, sirve y decora con algo de chocolate negro.")
    ),
    Receta(
        id = "6",
        titulo = "Tazón de Quinoa y Aguacate",
        descripcionBreve = "Opción vegana llena de fibra y proteínas completas.",
        tiempoPreparacion = "25 min",
        calorias = "430 kcal",
        imagenUrl = "https://cdn.prod.website-files.com/692da62b1095e881eb691deb/694ee00d36e0d7a0565a0483_zoo-martinique-animaux-mammiferes-capybara-06.jpg",
        ingredientes = listOf("60g Quinoa seca", "1/2 Aguacate", "5 Tomates cherry", "50g Garbanzos cocidos", "Zumo de limón"),
        pasos = listOf("Lava bien la quinoa bajo el grifo y cuécela unos 15 minutos.", "Escurre la quinoa y déjala enfriar un poco.", "Corta el aguacate en láminas y los tomates cherry por la mitad.", "Coloca la quinoa en la base de un bol, añade los garbanzos, tomate y aguacate por encima.", "Aliña con limón y pimentón al gusto.")
    )
)
