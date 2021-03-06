package com.example.dbwithfragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()

        container.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })

    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent

        Log.d("joljak", "onResume: ")
        if(intent.action == "NOTI"){Log.d("joljak", "onResume: NOTI")
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DataFragment())
                    .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_get_data -> {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, DataFragment())
                        .commit()
            }
            R.id.menu_change_pwd -> {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ChangeFragment())
                    .commit()
            }
            R.id.menu_get_one_data -> {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OneDataFragment())
                    .commit()
            }
        }
        drawer_layout.closeDrawers()
        return false
    }

    fun replaceFragment(fragment: Fragment) {
        var fragmentManager = getSupportFragmentManager();
        var fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
        var actionBar = supportActionBar
        actionBar?.show()
    }


    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    fun initLayout() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}