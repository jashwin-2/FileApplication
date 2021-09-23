package com.example.fileexplorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fileexplorer.fragments.CardFragment
import com.example.fileexplorer.fragments.InternalFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var drawer : DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = drawer_layout

        var toolbar : Toolbar = toolbar
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,InternalFragment()).addToBackStack(null).commit()
        nav_view.setCheckedItem(R.id.nav_internal)
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.nav_internal ->{
                    val internalFragment = InternalFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container,internalFragment).addToBackStack(null).commit()
                    true
                }
                R.id.nav_card ->{
                    val cardFragment = CardFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container,cardFragment).addToBackStack(null).commit()
                    true
                }

                else -> false
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
        var toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        fragmentManager.popBackStackImmediate()
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }
}