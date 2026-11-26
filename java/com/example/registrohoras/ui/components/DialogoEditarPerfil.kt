package com.example.registrohoras.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

/** Edición del perfil: nombre completo y el nombre con el que la app saluda a la persona. */
@Composable
fun DialogoEditarPerfil(
    nombreInicial: String,
    nombreMostrarInicial: String,
    validarNombre: (String) -> String?,
    validarMostrar: (String) -> String?,
    alGuardar: (nombre: String, nombreMostrar: String) -> Unit,
    alCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var mostrar by remember { mutableStateOf(nombreMostrarInicial) }
    var tocoNombre by remember { mutableStateOf(false) }
    var tocoMostrar by remember { mutableStateOf(false) }

    val errorNombre = if (tocoNombre) validarNombre(nombre) else null
    val errorMostrar = if (tocoMostrar) validarMostrar(mostrar) else null

    fun guardar() {
        tocoNombre = true
        tocoMostrar = true
        if (validarNombre(nombre) == null && validarMostrar(mostrar) == null) alGuardar(nombre, mostrar)
    }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Editar perfil") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; tocoNombre = true },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    trailingIcon = { IconoEstado(hayError = errorNombre != null, valido = tocoNombre && errorNombre == null) },
                    isError = errorNombre != null,
                    supportingText = { MensajeError(errorNombre) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = mostrar,
                    onValueChange = { mostrar = it; tocoMostrar = true },
                    label = { Text("¿Cómo te llamamos?") },
                    placeholder = { Text("Ej.: Ani") },
                    leadingIcon = { Icon(Icons.Filled.Face, contentDescription = null) },
                    trailingIcon = {
                        IconoEstado(hayError = errorMostrar != null, valido = tocoMostrar && mostrar.isNotBlank() && errorMostrar == null)
                    },
                    isError = errorMostrar != null,
                    supportingText = {
                        if (errorMostrar != null) MensajeError(errorMostrar) else Text("Así te saludará la app. Si lo dejas vacío, usamos tu primer nombre.")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { guardar() }),
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = { TextButton(onClick = { guardar() }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } }
    )
}
