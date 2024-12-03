package com.example.proyecto_guion

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    //Se ejecuta cuando la actividad es creada por primera vez, como cuando el usuario abre la aplicación.
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //Llama a la función original de onCreate en AppCompatActivity. Es decir, se asegura de que se inicialice correctamente antes de añadir cosas nuestras.
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //esto es para decir que viene por defecto y asi sustituye la que viene por defecto

        //controlador de navegacion
        val navHostFragmento = supportFragmentManager.findFragmentById(R.id.container_fragment) as NavHostFragment
        val navControla = navHostFragmento.navController

        //barra de arriba y controlador de navegacion
        val appBarConfiguration = AppBarConfiguration.Builder(navControla.graph) //le pasamos el grafo de navegacion


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //que se hace cuando se selecciona un item de las opciones
        val navControlador = findNavController(R.id.container_fragment) // Encuentra el NavController
        return when (item.itemId) {
            R.id.atras -> { // Este es el id de tu item del menú
                if (!navControlador.popBackStack()) {
                    // Si no hay nada en la pila de retroceso, haz algo adicional si es necesario
                    finish() // Cierra la actividad o maneja el caso según tu aplicación
                }
                true // Indica que se manejó el evento
            }
            else -> super.onOptionsItemSelected(item) // Usa la implementación predeterminada
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //hay que indicar que los menus de la toolbar son por defecto tambien aunq la barra ya se mantenga
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        return super.onCreateOptionsMenu(menu)
    }


}