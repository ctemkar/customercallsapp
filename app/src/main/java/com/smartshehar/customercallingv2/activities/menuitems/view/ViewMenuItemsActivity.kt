package com.smartshehar.customercallingv2.activities.menuitems.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.activities.adapters.MenuItemAdapter
import com.smartshehar.customercallingv2.activities.menuitems.add.AddMenuItemActivity
import com.smartshehar.customercallingv2.databinding.ActivityViewMenuItemsBinding
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewMenuItemsActivity : AppCompatActivity() {

    private val TAG = "ViewMenuItemsActivity"
    lateinit var binding: ActivityViewMenuItemsBinding
    val viewModel: ViewMenuItemVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMenuItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()

        viewModel.getMenuItems().observe(this) {
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> {
                    Log.d(TAG, "onCreate: ${it.data!!.size}")
                    val mAdapter = MenuItemAdapter(it.data!!)
                    binding.rViewViewMenuItems.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = mAdapter
                    }
                }
                EventStatus.ERROR -> TODO()
                EventStatus.EMPTY -> TODO()
            }
        }

    }

    private fun setListeners() {
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.tv_tbTitle).text = getString(R.string.menu_items)
        binding.fabAddMenuItemRedirect.setOnClickListener {
            startActivity(Intent(this, AddMenuItemActivity::class.java))
        }
    }
}