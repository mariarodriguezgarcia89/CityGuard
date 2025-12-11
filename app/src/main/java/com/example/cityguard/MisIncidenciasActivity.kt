package com.example.cityguard

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar // Mantenemos la importación por si se usa en otro lado, pero ya no es necesaria aquí.

class MisIncidenciasActivity : BaseActivity(), IncidenciaAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IncidenciaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_incidencias)

        configurarToolbarYNav("Mis Incidencias", R.id.nav_my)

        recyclerView = findViewById(R.id.recyclerMisIncidencias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener TODAS las incidencias
        val lista = IncidenciaManager.obtenerIncidenciasUsuario().toMutableList()

        // DEBUG: Ver qué incidencias tenemos (Esto es útil y se mantiene)
        println("=== DEBUG MisIncidencias ===")
        println("Total incidencias: ${lista.size}")
        lista.forEach { incidencia ->
            val esEjemplo = IncidenciaManager.esIncidenciaDeEjemplo(incidencia.id)
            println("ID: ${incidencia.id} - ${incidencia.direccion} - " +
                    "Usuario: ${incidencia.usuario} - Ejemplo: $esEjemplo")
        }

        // Configurar adapter
        adapter = IncidenciaAdapter(lista, this)
        recyclerView.adapter = adapter

        // ❌ La llamada a mostrarMensajeEstado() se elimina de aquí.
    }

    // ❌ ELIMINAMOS COMPLETAMENTE EL MÉTODO mostrarMensajeEstado(lista: List<Incidencia>)

    override fun onResume() {
        super.onResume()
        // Actualizar la lista cuando vuelve a esta actividad
        actualizarLista()
    }

    private fun actualizarLista() {
        val nuevaLista = IncidenciaManager.obtenerIncidenciasUsuario().toMutableList()
        adapter.actualizarLista(nuevaLista)
        // ❌ Y SE ELIMINA LA LLAMADA DE AQUÍ TAMBIÉN:
        // mostrarMensajeEstado(nuevaLista)
    }

    override fun onItemClick(incidencia: Incidencia) {
        // Abrir detalles de la incidencia
        val intent = Intent(this, DetalleIncidenciaActivity::class.java)
        intent.putExtra("incidencia", incidencia)
        startActivity(intent)
    }
}