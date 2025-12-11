package com.example.cityguard

import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class NuevaIncidenciaActivity : BaseActivity() {

    private lateinit var editDireccion: EditText
    private lateinit var spinner: Spinner
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var editOtros: EditText
    private lateinit var layoutOtros: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_incidencia)

        // Configurar Toolbar y BottomNavigationView con BaseActivity
        configurarToolbarYNav("Nueva Incidencia", R.id.nav_new)

        // Referencias
        editDireccion = findViewById(R.id.editTextDireccion)
        spinner = findViewById(R.id.spinner)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)
        editOtros = findViewById(R.id.editTextOtros)
        layoutOtros = findViewById(R.id.layoutOtros)

        // Spinner
        val tipos = resources.getStringArray(R.array.tipos_incidencia)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editOtros.visibility = if (parent?.getItemAtPosition(position).toString() == "Otros") View.VISIBLE else View.GONE
                if (editOtros.visibility == View.VISIBLE) editOtros.requestFocus()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnGuardar.setOnClickListener { guardarIncidencia() }
    }

    private fun guardarIncidencia() {
        editDireccion.error = null
        txtDescripcion.error = null
        layoutOtros.error = null

        if (editDireccion.text.isBlank()) {
            editDireccion.error = "Ingrese una dirección"
            return
        }
        if (txtDescripcion.text.isBlank()) {
            txtDescripcion.error = "Ingrese una descripción"
            return
        }

        val tipoSeleccionado = spinner.selectedItem.toString()
        if (tipoSeleccionado == "Otros" && editOtros.text.isBlank()) {
            editOtros.error = "Especifique el tipo de incidencia"
            return
        }

        val tipoFinal = if (tipoSeleccionado == "Otros") editOtros.text.toString().trim() else tipoSeleccionado
        val tipoIncidencia = TipoIncidencia.obtenerTipoIncidenciaPorNombre(tipoFinal)

        val nuevoId = generarIdUnico()
        val nuevaIncidencia = Incidencia(
            id = 0,
            direccion = editDireccion.text.toString().trim(),
            tipo = tipoIncidencia,
            descripcion = txtDescripcion.text.toString().trim(),
            estado = "Pendiente",
            fecha = System.currentTimeMillis(),
            usuario = "Usuario"
        )

        IncidenciaManager.agregarIncidencia(nuevaIncidencia)

        Snackbar.make(btnGuardar, "✓ Incidencia registrada", Snackbar.LENGTH_LONG)
            .setAction("DESHACER") {
                IncidenciaManager.eliminarIncidencia(nuevaIncidencia.id)
                Toast.makeText(this, "Incidencia deshecha", Toast.LENGTH_SHORT).show()
            }.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    limpiarCampos()
                }
            }).show()
    }

    private fun generarIdUnico(): Int {
        var id: Int
        var intentos = 0
        val maxIntentos = 50
        do {
            id = (100..9999).random()
            intentos++
            val existe = IncidenciaManager.obtenerTodasLasIncidencias().any { it.id == id }
            if (!existe) return id
        } while (intentos < maxIntentos)
        return (System.currentTimeMillis() % 9000).toInt() + 1000
    }

    private fun limpiarCampos() {
        editDireccion.text.clear()
        txtDescripcion.text.clear()
        editOtros.text.clear()
        layoutOtros.visibility = View.GONE
        layoutOtros.error = null
        spinner.setSelection(0)
        editDireccion.requestFocus()
    }
}
