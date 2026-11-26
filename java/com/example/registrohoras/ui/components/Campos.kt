package com.example.registrohoras.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.registrohoras.domain.fechaLegible
import com.example.registrohoras.ui.theme.VerdeExito
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Mensaje de error animado: aparece/desaparece con transición suave y deja el espacio reservado. */
@Composable
fun MensajeError(error: String?) {
    AnimatedContent(targetState = error, label = "mensaje_error") { mensaje ->
        if (mensaje != null) Text(mensaje)
    }
}

/** Ícono de estado del campo: rojo si hay error, verde si está correcto, nada si aún no se toca. */
@Composable
fun IconoEstado(hayError: Boolean, valido: Boolean) {
    val estado = when {
        hayError -> 1
        valido -> 2
        else -> 0
    }
    AnimatedContent(
        targetState = estado,
        transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
        label = "icono_estado"
    ) { valor ->
        when (valor) {
            1 -> Icon(Icons.Filled.Error, contentDescription = "Campo con error", tint = MaterialTheme.colorScheme.error)
            2 -> Icon(Icons.Filled.CheckCircle, contentDescription = "Campo correcto", tint = VerdeExito)
            else -> Spacer(Modifier.size(0.dp))
        }
    }
}

@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    valido: Boolean,
    alPerderFoco: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    capitalizar: Boolean = false,
    accion: ImeAction = ImeAction.Next
) {
    var tuvoFoco by remember { mutableStateOf(false) }
    val textoPlaceholder: (@Composable () -> Unit)? =
        if (placeholder != null) ({ Text(placeholder) }) else null

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { foco ->
                if (foco.isFocused) {
                    tuvoFoco = true
                } else if (tuvoFoco) {
                    tuvoFoco = false
                    alPerderFoco()
                }
            },
        label = { Text(etiqueta) },
        placeholder = textoPlaceholder,
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = { IconoEstado(hayError = error != null, valido = valido) },
        isError = error != null,
        supportingText = { MensajeError(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = teclado,
            capitalization = if (capitalizar) KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
            imeAction = accion
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelector(
    seleccionado: String?,
    opciones: List<Pair<String, String>>,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    valido: Boolean,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }, modifier = modifier) {
        OutlinedTextField(
            value = opciones.firstOrNull { it.first == seleccionado }?.second.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            placeholder = { Text("Selecciona una opción") },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconoEstado(hayError = error != null, valido = valido)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                }
            },
            isError = error != null,
            supportingText = { MensajeError(error) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = {
                        alSeleccionar(id)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    fecha: LocalDate,
    error: String?,
    valido: Boolean,
    alSeleccionar: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(
            value = fechaLegible(fecha),
            onValueChange = {},
            readOnly = true,
            label = { Text("Día del trabajo") },
            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
            trailingIcon = { IconoEstado(hayError = error != null, valido = valido) },
            isError = error != null,
            supportingText = { MensajeError(error) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente: un TextField de solo lectura no recibe el clic por sí solo.
        Box(
            Modifier
                .matchParentSize()
                .clickable(role = Role.Button, onClickLabel = "Elegir fecha") { abierto = true }
        )
    }

    if (abierto) {
        val hoyMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= hoyMillis
            }
        )
        DatePickerDialog(
            onDismissRequest = { abierto = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let {
                        alSeleccionar(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    abierto = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { abierto = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = estado)
        }
    }
}
