package com.example.cityguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleIncidenciaActivity : AppCompatActivity(),
    ResueltaDialogFragment.Listener {

    private lateinit var tvDireccion: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvFecha: TextView
    private lateinit var ivIcono: ImageView
    private lateinit var btnResuelta: Button
    private lateinit var btnVolver: Button

    private var incidencia: Incidencia? = null

    companion object {
        const val RESULTADO_INCIDENCIA_ACTUALIZADA = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_incidencia)

        // Inicializar vistas
        btnVolver = findViewById(R.id.btnVolver)
        tvDireccion = findViewById(R.id.txtDireccion)
        tvTipo = findViewById(R.id.txtTipo)
        tvEstado = findViewById(R.id.txtEstado)
        tvDescripcion = findViewById(R.id.txtDescripcion)
        tvFecha = findViewById(R.id.txtFecha)
        ivIcono = findViewById(R.id.imgIcono)
        btnResuelta = findViewById(R.id.btnResuelta)

        // Botón volver
        btnVolver.setOnClickListener { finish() }

        // Recibir incidencia desde intent
        incidencia = intent.getSerializableExtra("incidencia") as? Incidencia
        mostrarDatos()
        configurarBotonSegunEstado()

        // Marcar como resuelta
        btnResuelta.setOnClickListener {
            if (incidencia?.estado == "Pendiente") {
                mostrarDialogoFragment()
            } else {
                Toast.makeText(this, "Esta incidencia ya está resuelta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoFragment() {
        val dialog = ResueltaDialogFragment()
        val bundle = Bundle()
        bundle.putSerializable("incidencia", incidencia)
        dialog.arguments = bundle
        dialog.listener = this  // <-- Importante
        dialog.show(supportFragmentManager, "resuelta_dialog")
    }

    private fun mostrarDatos() {
        incidencia?.let { inc ->
            tvDireccion.text = inc.direccion
            tvTipo.text = inc.tipo.nombre
            tvEstado.text = inc.estado
            tvDescripcion.text = inc.descripcion
            tvFecha.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(inc.fecha))
            ivIcono.setImageResource(inc.tipo.icono)
        }
    }

    private fun configurarBotonSegunEstado() {
        incidencia?.let { inc ->
            when (inc.estado) {
                "Resuelta" -> {
                    btnResuelta.text = "✓ Incidencia Resuelta"
                    btnResuelta.isEnabled = false
                    btnResuelta.alpha = 0.7f
                    btnResuelta.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.estadoResuelta)
                    )
                }
                "Pendiente" -> {
                    btnResuelta.text = "Marcar como Resuelta"
                    btnResuelta.isEnabled = true
                    btnResuelta.alpha = 1f
                    btnResuelta.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.colorPrimary)
                    )
                }
            }
        }
    }

    // Listener del DialogFragment
    override fun onIncidenciaResuelta(incidencia: Incidencia) {
        incidencia.estado = "Resuelta"
        IncidenciaManager.actualizarIncidencia(incidencia)
        this.incidencia = incidencia  // Actualizar referencia local
        mostrarDatos()
        configurarBotonSegunEstado()

        // Toast y Snackbar
        Toast.makeText(this, "✓ Incidencia marcada como resuelta", Toast.LENGTH_SHORT).show()
        Snackbar.make(findViewById(android.R.id.content), "Incidencia marcada como resuelta", Snackbar.LENGTH_SHORT).show()

        // Notificación
        CanalNotificacion.mostrarNotificacionResuelta(
            this,
            "✅ Incidencia Resuelta",
            "Se ha resuelto la incidencia en ${incidencia.direccion}",
            incidencia
        )

        // Preparar resultado para Activity que llamó (si lo hubiera)
        val resultIntent = Intent()
        resultIntent.putExtra("incidencia_actualizada", incidencia)
        setResult(RESULTADO_INCIDENCIA_ACTUALIZADA, resultIntent)
    }
}
