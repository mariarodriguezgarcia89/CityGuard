package com.example.cityguard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val AYUNTAMIENTO_PHONE = "123456789"
        private const val AYUNTAMIENTO_EMAIL = "contacto@ayuntamiento.es"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // CONFIGURAR TOOLBAR (con el tipo correcto)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Inicio"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Inicializar vistas
        val btnNueva = findViewById<Button>(R.id.btnNuevaIncidencia)
        val btnMis = findViewById<Button>(R.id.btnMisIncidencias)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Inicializar canal de notificaciones
        CanalNotificacion.crearCanalNotificaciones(this)

        // Solicitar permiso para Android 13+
        solicitarPermisoNotificaciones()

        // --- Botones principales ---
        btnNueva.setOnClickListener {
            startActivity(Intent(this, NuevaIncidenciaActivity::class.java))
        }

        btnMis.setOnClickListener {
            startActivity(Intent(this, MisIncidenciasActivity::class.java))
        }

        // --- FAB como botón de contacto ---
        fab.setOnClickListener { mostrarDialogoContacto() }

        // --- BottomNavigationView ---
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true // Ya estamos en MainActivity
                }
                R.id.nav_new -> {
                    startActivity(Intent(this, NuevaIncidenciaActivity::class.java))
                    true
                }
                R.id.nav_my -> {
                    startActivity(Intent(this, MisIncidenciasActivity::class.java))
                    true
                }
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_home
    }

    // ⚡ MÉTODO PARA EL MENÚ (nivel de clase, NO dentro de onCreate)
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ⚡ MÉTODO PARA PERMISOS (nivel de clase)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // ⚡ NO dentro de onCreate
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "✅ Permiso de notificaciones concedido")
            } else {
                Toast.makeText(
                    this,
                    "Las notificaciones están desactivadas. Puedes activarlas en Ajustes.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ⚡ MÉTODOS PRIVADOS (nivel de clase)
    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun mostrarDialogoContacto() {
        val opciones = arrayOf("Llamar al Ayuntamiento", "Enviar correo")
        AlertDialog.Builder(this)
            .setTitle("Contacto Ayuntamiento")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirDialer()
                    1 -> enviarCorreo()
                }
            }.show()
    }

    private fun abrirDialer() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$AYUNTAMIENTO_PHONE"))
        startActivity(intent)
    }

    private fun enviarCorreo() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$AYUNTAMIENTO_EMAIL"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Incidencia")
        intent.putExtra(Intent.EXTRA_TEXT, "Detalle de la incidencia:")
        startActivity(Intent.createChooser(intent, "Enviar correo"))
    }
}