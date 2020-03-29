package com.example.btpproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class AjouterOrdreDeTravailActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_ordre_de_travail)


        tabLayout = findViewById(R.id.ajoutOTTabLayout)
        viewPager = findViewById(R.id.ajoutOTViewPager)

        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        //Ajouter les fragments
        adapter.addFragment(FragmentListeLigneLotAjouteOt(), "Lignes")
        adapter.addFragment(FragmentListeArticlesAjoutOt(), "Articles")

        //Adapter setup
        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
    }
}
