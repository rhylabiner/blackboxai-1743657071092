package com.example.elibrary.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.elibrary.fragments.AdminBooksFragment
import com.example.elibrary.fragments.AdminUsersFragment

class AdminPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AdminBooksFragment()
            1 -> AdminUsersFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}