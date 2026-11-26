package com.example.registrohoras.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.registrohoras.domain.FormularioValidator
import kotlinx.coroutines.delay

/**
 * Diálogo para escribir a mano un usuario o proyecto nuevo (opción "Otro").
 * Valida mientras se escribe y muestra el mismo ícono verde/rojo que el resto de los campos.
 */
@Composable
fun DialogoNombreNuevo(
    titulo: String,
    ayuda: String,
    etiqueta: String,
    icono: ImageVector,
    validar: (String) -> String?,
    alConfirmar: (String) -> Unit,
    alCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf("") }
    var escribio by remember { mutableStateOf(false) }
    val foco = remember { FocusRequester() }
    val error = if (escribio) validar(texto) else null
    val valido = escribio && error == null

    // Abre el teclado solo; se espera un instante a que el campo exista dentro de la ventana del diálogo.
    LaunchedEffect(Unit) {
        delay(150)
        runCatching { foco.requestFocus() }
    }

    fun confirmar() {
        escribio = true
        if (validar(texto) == null) alConfirmar(texto)
    }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text(titulo) },
        text = {
            Column {
                Text(ayuda, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = {
                        texto = it.take(FormularioValidator.LARGO_MAX_NOMBRE + 10)
                        escribio = true
                    },
                    modifier = Modifier.focusRequester(foco),
                    label = { Text(etiqueta) },
                    leadingIcon = { Icon(icono, contentDescription = null) },
                    trailingIcon = { IconoEstado(hayError = error != null, valido = valido) },
                    isError = error != null,
                    supportingText = { MensajeError(error) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { confirmar() }),
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = { TextButton(onClick = { confirmar() }) { Text("Agregar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } }
    )
}
