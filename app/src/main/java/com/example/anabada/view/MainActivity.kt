package com.example.anabada.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.example.anabada.util.BaseViewBindingActivity
import com.example.anabada.R
import com.example.anabada.databinding.ActivityMainBinding
import com.example.anabada.repository.SharedPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it)}) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(binding)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        // 위와 같은 2번째 방법
        //        NavigationUI.setupWithNavController(
        //            main_bottom_navigation,
        //            findNavController(R.id.main_nav_host)
        //        )
    }

    private fun initView(binding: ActivityMainBinding) {
//        binding.bottomNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.tab_home -> {
//                    Intent(this, HomeFragment::class.java).apply {
//                        startActivity(this)
//                    }
//                    return@OnNavigationItemSelectedListener true
//                }
//            }
//            false
//        })

//        pager?.adapter = MainPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
//        pager?.offscreenPageLimit = 3
//        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                when (position) {
//                    0 -> bottomNav?.selectedItemId = R.id.tab_home
//                    1 -> bottomNav?.selectedItemId = R.id.tab_trends
////                    2 -> bottomNav?.selectedItemId = R.id.tab_post
//                    2 -> bottomNav?.selectedItemId = R.id.tab_chat
//                    3 -> bottomNav?.selectedItemId = R.id.tab_mypage
//                }
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//        })

//        bottomNav?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.tab_home -> {
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.tab_trends -> {
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.tab_post -> {
//                    if (SharedPreferencesManager.getUserId(this) == "no") { // need to login
//                        Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
//                        Intent(this, LoginActivity::class.java).run {
//                            startActivity(this)
//                        }
//                    } else {
//                        Intent(this, PostActivity::class.java).run {
//                            startActivity(this)
//                        }
//                    }
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.tab_chat -> {
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.tab_mypage -> {
//                    return@OnNavigationItemSelectedListener true
//                }
//            }
//            false
//        })
    }

    private var time: Long = 0
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.currentDestination
        if (navController.currentDestination?.id == R.id.homeFragment) {
            if (System.currentTimeMillis() - time >= 1000) {
                time = System.currentTimeMillis()
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else if (System.currentTimeMillis() - time < 1000) {
                finishAffinity()
                System.runFinalization()
                exitProcess(0)
            }
        } else {
            super.onBackPressed()
        }
    }

//    private inner class MainPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
//
//        private val fragments = listOf(
//            HomeFragment(),
//            TempFragment(),
//            ChatFragment(),
//            MyPageFragment()
//        )
//
//        override fun getItem(position: Int): Fragment {
//            return fragments[position]
//        }
//
//        override fun getCount(): Int {
//            return fragments.size
//        }
//    }

}

