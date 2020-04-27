package com.example.btpproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import java.util.ArrayList
import android.text.method.TextKeyListener.clear
import androidx.fragment.app.FragmentTransaction


class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fmm = fm
    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitles = ArrayList<String>()


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitles.add(title)
    }

    fun clear() {
        val manager: FragmentManager =  this.fmm
        val transaction: FragmentTransaction = manager.beginTransaction()

        for (fragment in fragmentList) {
            transaction.remove(fragment)
        }
        for(title in fragmentTitles){
            fragmentTitles.remove(title)
        }
        fragmentList.clear()
        fragmentTitles.clear()
        transaction.commitAllowingStateLoss()
    }

    fun clear2(){
        val fragments = fmm.getFragments()
        if (fragments != null) {
            val ft = fmm.beginTransaction()
            for (f in fragments!!) {
                //You can perform additional check to remove some (not all) fragments:
                if (f != null) {
                    ft.remove(f)

                }
            }
            ft.commitAllowingStateLoss()
        }
    }
}
