package com.example.registrohoras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.registrohoras.domain.Perfil
import java.io.File

/** Foto de perfil circular. Si no hay foto, muestra la inicial de la persona. */
@Composable
fun AvatarPerfil(perfil: Perfil?, tamano: Dp, modifier: Modifier = Modifier) {
    val foto = perfil?.fotoPath
    val inicial = perfil?.nombreParaSaludo?.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (foto != null) {
            AsyncImage(
                model = File(foto),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                inicial,
                fontSize = (tamano.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
