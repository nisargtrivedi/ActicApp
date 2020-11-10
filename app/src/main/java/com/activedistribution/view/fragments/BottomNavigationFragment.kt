package com.activedistribution.view.fragments

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.activedistribution.R
import com.activedistribution.utils.BottomNavigationViewEx

class BottomNavigationFragment : BaseFragment() {

    var navigation: BottomNavigationViewEx?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragmnet_bottom_nav, container, false);

        if (savedInstanceState == null) {
            baseActivity.loadFragment(HomeFragment(), R.id.frame_layout_new)
        }
        init(view)
        return view
    }

    private fun init(view: View?) {
        navigation = view!!.findViewById(R.id.tabLayout)
        navigation!!.enableAnimation(false)
        navigation!!.enableShiftingMode(false)
        navigation!!.enableItemShiftingMode(false)
        navigation!!.itemIconTintList = null
        navigation!!.currentItem = 0
        navigation!!.setTextVisibility(false)
        navigation!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (navigation!!.currentItem !== 0)
                        baseActivity.loadFragment(HomeFragment(), R.id.frame_layout_new)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    if (navigation!!.currentItem !== 1)
                        baseActivity.loadFragment(ProfileFragment(),R.id.frame_layout_new)
                        return@OnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    if (navigation!!.currentItem !== 2)
                        baseActivity.loadFragment(SettingFragment(),R.id.frame_layout_new)
                        return@OnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    baseActivity.logout_dialog()
                    if (navigation!!.currentItem !== 3)
                        return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}