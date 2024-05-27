package net.proyecto.victorberenguermadrid.musicheads.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Album (
    val titulo: String? = null,
    val genero: String? = null,
    val lanzamiento: Timestamp? = null,
    val num_canciones: Int = 0,
    val artistRef: DocumentReference? = null
)

