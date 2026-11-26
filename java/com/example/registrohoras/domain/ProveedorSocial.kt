package com.example.registrohoras.domain

/** Proveedores de inicio de sesión social y la página oficial donde se inicia sesión. */
enum class ProveedorSocial(val nombre: String, val urlInicio: String) {
    GOOGLE("Google", "https://accounts.google.com/"),
    APPLE("Apple", "https://appleid.apple.com/sign-in"),
    FACEBOOK("Facebook", "https://www.facebook.com/login")
}
