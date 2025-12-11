package com.example.cityguard

import java.io.Serializable

data class TipoIncidencia(
    val nombre: String,
    val icono: Int,
    val esPredefinido: Boolean = true
) : Serializable {

    companion object {
        // Tipos predefinidos actualizados
        val BACHE = TipoIncidencia("Bache", R.drawable.bache)
        val ALUMBRADO = TipoIncidencia("Alumbrado público", R.drawable.ic_luz)
        val LIMPIEZA = TipoIncidencia("Limpieza urbana", R.drawable.basura)
        val VANDALISMO = TipoIncidencia("Vandalismo", R.drawable.vandalismo)
        val TRAFICO = TipoIncidencia("Tráfico", R.drawable.trafico)
        val ALCANTARILLA = TipoIncidencia("Alcantarilla", R.drawable.alcantarilla)
        val PARQUE = TipoIncidencia("Parque", R.drawable.parque)
        val ACERADO = TipoIncidencia("Acerado", R.drawable.acerado)
        val OTROS = TipoIncidencia("Otros", R.drawable.otros)

        // Lista de tipos predefinidos (mantener)
        val TIPOS_PREDEFINIDOS = listOf(
            BACHE, ALUMBRADO, LIMPIEZA, VANDALISMO, TRAFICO,
            ALCANTARILLA, PARQUE, ACERADO, OTROS
        )

        // Solo mantener este método que SÍ usas en NuevaIncidenciaActivity
        fun obtenerTipoIncidenciaPorNombre(nombre: String): TipoIncidencia {
            return when (nombre) {
                "Bache" -> BACHE
                "Alumbrado público" -> ALUMBRADO
                "Limpieza urbana" -> LIMPIEZA
                "Vandalismo" -> VANDALISMO
                "Tráfico" -> TRAFICO
                "Alcantarilla" -> ALCANTARILLA
                "Parque" -> PARQUE
                "Acerado" -> ACERADO
                else -> OTROS
            }
        }
    }
}

