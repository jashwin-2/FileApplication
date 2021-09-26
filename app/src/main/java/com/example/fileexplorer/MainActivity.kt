package com.example.fileexplorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
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
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,InternalFragment()).commit()
        nav_view.setCheckedItem(R.id.nav_internal)
        var itemSelected=0
        var fragment : Fragment? = null
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.nav_internal ->{
                    fragment = InternalFragment()
                    itemSelected = R.id.nav_internal
                }
                R.id.nav_card ->{
                    fragment = CardFragment()
                    itemSelected=R.id.nav_card
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
        //To avoid Lagging
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                when (itemSelected) {

                    R.id.nav_internal -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment!!)
                            .commit()
                    }
                    R.id.nav_card -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment!!)
                            .commit()
                    }
                }

            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

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