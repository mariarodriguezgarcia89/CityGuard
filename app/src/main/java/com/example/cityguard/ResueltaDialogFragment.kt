package com.example.cityguard

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ResueltaDialogFragment : DialogFragment() {

    interface Listener {
        fun onIncidenciaResuelta(incidencia: Incidencia)
    }

    var listener: Listener? = null
    private var incidencia: Incidencia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperar incidencia de los argumentos
        incidencia = arguments?.getSerializable("incidencia") as? Incidencia
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Confirmar")
            .setMessage("¿Marcar incidencia como resuelta?")
            .setPositiveButton("Sí") { _, _ ->
                incidencia?.let { listener?.onIncidenciaResuelta(it) }
            }
            .setNegativeButton("No", null)
            .create()
    }
}
