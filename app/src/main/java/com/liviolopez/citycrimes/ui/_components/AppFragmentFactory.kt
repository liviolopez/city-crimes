package com.liviolopez.citycrimes.ui._components

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.liviolopez.citycrimes.ui.home.HomeFragment
import javax.inject.Inject

class AppFragmentFactory @Inject constructor()
: FragmentFactory(){
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HomeFragment::class.java.name -> HomeFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}