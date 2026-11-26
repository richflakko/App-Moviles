package com.example.registrohoras.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.registrohoras.R
import com.example.registrohoras.domain.ProveedorSocial

/**
 * Botón de inicio de sesión social. Los tres usan la misma altura, el mismo borde y un ícono de
 * 24 dp dentro de una caja de 24x24 (parte visible de 20x20), así ninguno se ve más grande o pequeño.
 */
@Composable
fun BotonSocial(proveedor: ProveedorSocial, alClic: () -> Unit, modifier: Modifier = Modifier) {
    val icono = when (proveedor) {
        ProveedorSocial.GOOGLE -> R.drawable.ic_login_google
        ProveedorSocial.APPLE -> R.drawable.ic_login_apple
        ProveedorSocial.FACEBOOK -> R.drawable.ic_login_facebook
    }
    OutlinedButton(
        onClick = alClic,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Image(
            painter = painterResource(icono),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            // El logo de Apple es monocromático: negro en modo claro y blanco en oscuro.
            colorFilter = if (proveedor == ProveedorSocial.APPLE) ColorFilter.tint(MaterialTheme.colorScheme.onSurface) else null
        )
        Spacer(Modifier.width(12.dp))
        Text("Continuar con ${proveedor.nombre}", style = MaterialTheme.typography.titleSmall)
    }
}
