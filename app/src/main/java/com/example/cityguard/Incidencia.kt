package com.example.cityguard

import java.io.Serializable

data class Incidencia(
    val id: Int,
    val direccion: String,
    val tipo: TipoIncidencia,
    val descripcion: String,
    var estado: String,
    val fecha: Long,
    val usuario: String
) : Serializable {
    val icono: Int get() = tipo.icono
}

object IncidenciaManager {
    private val _incidencias = mutableListOf<Incidencia>()
    private var nextId = 3 // Siguiente ID después de los ejemplos (1 y 2)

    // IDs de las incidencias de ejemplo (NO MODIFICAR)
    private val idsEjemplo = setOf(1, 2)

    init {
        inicializarIncidenciasEjemplo()
    }

    private fun inicializarIncidenciasEjemplo() {
        // Solo añadir si no existen ya
        if (_incidencias.none { it.id == 1 }) {
            _incidencias.add(
                Incidencia(
                    id = 1,
                    direccion = "Calle Mayor 123",
                    tipo = TipoIncidencia.BACHE,
                    descripcion = "Bache grande en la calzada",
                    estado = "Pendiente",
                    fecha = System.currentTimeMillis(),
                    usuario = "Ejemplo" // Todas las de ejemplo tienen usuario "Ejemplo"
                )
            )
        }

        if (_incidencias.none { it.id == 2 }) {
            _incidencias.add(
                Incidencia(
                    id = 2,
                    direccion = "Av. Libertad 45",
                    tipo = TipoIncidencia.ALUMBRADO,
                    descripcion = "Farola rota",
                    estado = "Pendiente",
                    fecha = System.currentTimeMillis(),
                    usuario = "Ejemplo" // Todas las de ejemplo tienen usuario "Ejemplo"
                )
            )
        }
    }

    // Para agregar una incidencia ya creada
    fun agregarIncidencia(incidencia: Incidencia) {
        // Asignar nuevo ID si es 0
        val nuevaIncidencia = if (incidencia.id == 0) {
            incidencia.copy(id = nextId++)
        } else {
            incidencia
        }

        if (_incidencias.none { it.id == nuevaIncidencia.id }) {
            _incidencias.add(nuevaIncidencia)
        }
    }

    // Para crear una nueva incidencia desde los datos
    fun crearNuevaIncidencia(
        direccion: String,
        tipo: TipoIncidencia,
        descripcion: String
    ): Incidencia {
        val nuevaIncidencia = Incidencia(
            id = nextId++, // Auto-incrementar
            direccion = direccion,
            tipo = tipo,
            descripcion = descripcion,
            estado = "Pendiente",
            fecha = System.currentTimeMillis(),
            usuario = "UsuarioActual" // Todas las nuevas con este usuario
        )
        _incidencias.add(nuevaIncidencia)
        return nuevaIncidencia
    }

    fun obtenerTodasLasIncidencias(): List<Incidencia> {
        return _incidencias.sortedBy { it.id }
    }

    // CORRECCIÓN IMPORTANTE: Mostrar TODAS las incidencias
    fun obtenerIncidenciasUsuario(): List<Incidencia> {
        // Para el ejercicio, mostrar TODAS (ejemplo + usuario)
        return _incidencias.sortedBy { it.id }
    }

    // Si quieres separar: solo las NO de ejemplo
    fun obtenerIncidenciasNoEjemplo(): List<Incidencia> {
        return _incidencias.filter { !esIncidenciaDeEjemplo(it.id) }.sortedBy { it.id }
    }

    fun actualizarIncidencia(incidenciaActualizada: Incidencia) {
        val index = _incidencias.indexOfFirst { it.id == incidenciaActualizada.id }
        if (index != -1) {
            _incidencias[index] = incidenciaActualizada
        }
    }

    // Método que tienes y SÍ se va a usar
    fun esIncidenciaDeEjemplo(id: Int): Boolean {
        return id in idsEjemplo // Retorna true si id es 1 o 2
    }

    fun eliminarIncidencia(id: Int) {
        // Solo eliminar si NO es de ejemplo
        if (!esIncidenciaDeEjemplo(id)) {
            _incidencias.removeAll { it.id == id }
        }
    }

    // Método útil para debug
    fun imprimirEstado() {
        println("=== ESTADO DE INCIDENCIAS ===")
        println("Total: ${_incidencias.size}")
        _incidencias.sortedBy { it.id }.forEach {
            println("ID: ${it.id}, Dirección: ${it.direccion}, " +
                    "Usuario: ${it.usuario}, Ejemplo: ${esIncidenciaDeEjemplo(it.id)}")
        }
        println("=============================")
    }
}