package com.example.proyecto_guion

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menusLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //esto es para decir que viene por defecto y asi sustituye la que viene por defecto

        //recuperamso el elemento padre (el layout)
        val layoutPintador = findViewById<DrawerLayout>(R.id.layoutPintable)

        //controlador de navegacion
        val navHostFragmento = supportFragmentManager.findFragmentById(R.id.container_fragment) as NavHostFragment
        val navControla = navHostFragmento.navController

        //barra de arriba y controlador de navegacion
        val appBarConfiguration = AppBarConfiguration.Builder(navControla.graph) //le pasamos el grafo de navegacion
        //indico que hay un elemento abrible antes de construirla
        appBarConfiguration.setOpenableLayout(layoutPintador)
        val appBarraConstructor = appBarConfiguration.build() //cogemos el objeto buildeado

        //tool bar con controlador para que navegue entre los fragmentos
        toolbar.setupWithNavController(navControla, appBarraConstructor) //le ponemos los objetos con los que va a funcionar

        //se le pasa el objeto que tiene el nav_graph de navigation para que el menu pase de un fragmento a otro
        val barralado = findViewById<NavigationView>(R.id.sidetoolbar)
        barralado.setupWithNavController(navControla)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //que se hace cuando se selecciona un item de las opciones
        val navControlador = findNavController(R.id.container_fragment) //para que encuentre en la actividad el controlador del navegador
        NavigationUI.onNavDestinationSelected(item,navControlador)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //hay que indicar que los menus de la toolbar son por defecto tambien aunq la barra ya se mantenga
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        return super.onCreateOptionsMenu(menu)
    }
}