package com.example.marinemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.marinemanagement.ui.theme.MarineManagementTheme

enum class Pantalla { Inicio, Principal, Calendario }
// Estas son las tres pantallas de la aplicación
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarineManagementTheme {
                MarineManagementApp()
            }
        }
    }
}

@Composable
fun MarineManagementApp() {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.Inicio) }

    // Para ir hacia la pantalla anterior, se usa el botón de retroceso del dispositivo
    BackHandler(enabled = pantalla != Pantalla.Inicio) {
        pantalla = when (pantalla) {
            Pantalla.Calendario -> Pantalla.Principal
            else -> Pantalla.Inicio
        }
    }

    AnimatedContent(
        targetState = pantalla,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            // Desvanece la pantalla actual, y aparece la siguiente
            fadeIn(tween(durationMillis = 500, delayMillis = 500)) togetherWith
                    fadeOut(tween(durationMillis = 500))
        },
        label = "transicionPantallas"
    ) { destino ->
        when (destino) {
            Pantalla.Inicio -> PantallaInicio(
                onIniciar = { pantalla = Pantalla.Principal }
            )
            Pantalla.Principal -> PantallaRecordatorios(
                onCalendario = { pantalla = Pantalla.Calendario }
            )
            Pantalla.Calendario -> PantallaCalendario()
        }
    }
}


@Composable
fun PantallaInicio(onIniciar: () -> Unit) {
    PantallaConFondo(R.drawable.pantalla1) {
        // Botón invisible sobre el texto
        ZonaTactil(
            left = 0.15f, top = 0.72f, right = 0.90f, bottom = 0.80f,
            onClick = onIniciar
        )
    }
}
@Composable
fun PantallaRecordatorios(onCalendario: () -> Unit) {
    PantallaConFondo(R.drawable.pantalla2) {
        // Botón de calendario
        Image(
            painter = painterResource(R.drawable.boton_calendario),
            contentDescription = "Calendario",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        // Zona táctil (funciona como boton invisible)
        ZonaTactil(
            left = 0.15f, top = 0.50f, right = 0.88f, bottom = 0.61f,
            onClick = onCalendario
        )
    }
}
@Composable
fun PantallaCalendario() {
    PantallaConFondo(R.drawable.pantalla3)
}

@Composable
fun PantallaConFondo(
    @DrawableRes fondo: Int,
    contenido: @Composable BoxWithConstraintsScope.() -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        contenido()
    }
}

/* Área táctil invisible definida como fracciones (0f..1f) del tamaño de la pantalla. */
@Composable
fun BoxWithConstraintsScope.ZonaTactil(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = maxWidth * left, y = maxHeight * top)
            .size(width = maxWidth * (right - left), height = maxHeight * (bottom - top))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // sin efecto ripple
                onClick = onClick
            )
    )
}