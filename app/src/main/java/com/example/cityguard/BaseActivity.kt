package com.example.cityguard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    protected lateinit var toolbar: Toolbar
    protected lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // NO ponemos setContentView aquí, lo hace cada Activity hija
    }

    // Llamar desde la Activity hija después de setContentView
    protected fun configurarToolbarYNav(titulo: String, itemSeleccionado: Int) {
        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = titulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // BottomNavigationView
        bottomNav = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_new -> {
                    if (this !is NuevaIncidenciaActivity) {
                        startActivity(Intent(this, NuevaIncidenciaActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_my -> {
                    if (this !is MisIncidenciasActivity) {
                        startActivity(Intent(this, MisIncidenciasActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        bottomNav.selectedItemId = itemSeleccionado
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
