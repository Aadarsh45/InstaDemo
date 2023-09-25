package com.rj.geeksinstademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rj.geeksinstademo.fragments.HomeFragment
import com.rj.geeksinstademo.fragments.NotifiactionFragment
import com.rj.geeksinstademo.fragments.ProfileFragment
import com.rj.geeksinstademo.fragments.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var selectorFragment:Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bootom_navigation)
        selectorFragment = HomeFragment()
        bottomNavigationView.setOnNavigationItemSelectedListener(object :
        BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                when(item.itemId){
                    R.id.nav_home->selectorFragment = HomeFragment()
                    R.id.nav_search->selectorFragment = SearchFragment()
                    R.id.nav_add->{
                        startActivity(Intent(this@MainActivity,PostActivity::class.java))

                    }
                    R.id.nav_heart->selectorFragment = NotifiactionFragment()
                    R.id.nav_profile->selectorFragment = ProfileFragment()
                }
                if (selectorFragment!= null){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,selectorFragment).commit()
                }
                return true
            }

        })
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,HomeFragment()).commit()
    }
}