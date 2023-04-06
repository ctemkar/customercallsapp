package com.smartshehar.customercallingv2.activities.auth.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.R
import com.smartshehar.customercallingv2.databinding.ActivityRegisterOwnerBinding
import com.smartshehar.customercallingv2.models.dtos.RegisterOwnerRq
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterOwnerActivity : AppCompatActivity() {

    private val TAG = "RegisterOwnerActivity"
    lateinit var binding: ActivityRegisterOwnerBinding
    val viewModel: RegisterOwnerVM by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        binding.btRegisterOwner.setOnClickListener {
            handleRegisterClick()
        }
        registerObservers()

    }

    private fun registerObservers() {

        viewModel.getRegisterStatusLiveData().observe(this) {
            hideFullScreenLoadingLayout()
            when (it.eventStatus) {
                EventStatus.LOADING -> TODO()
                EventStatus.SUCCESS -> TODO()
                EventStatus.ERROR -> {
                    if (it.error.isNotBlank()) {
                        if (it.error.contains("already registered")) {
                            binding.etRegisterContact.requestFocus()
                            binding.etRegisterContact.error =
                                "Phone is already in use, Kindly Login"
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Unable to signup, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                EventStatus.EMPTY -> TODO()
                EventStatus.CACHE_DATA -> TODO()
            }
        }
    }

    private fun handleRegisterClick() {
        val registerOwnerRq = RegisterOwnerRq(
            ownerName = binding.etRegisterOwnerName.text.toString(),
            0,
            password = binding.etRegisterPassword.text.toString()
        )

        if (registerOwnerRq.ownerName.isNullOrBlank()) {

        } else if (binding.etRegisterContact.text.isNullOrBlank()) {

        } else if (registerOwnerRq.password.isNullOrBlank()) {

        }

        registerOwnerRq.contactNumber = binding.etRegisterContact.text.toString().toLong()
        showFullScreenLoadingLayout()
        viewModel.registerOwner(createOwnerRq = registerOwnerRq)

    }

    private fun hideFullScreenLoadingLayout() {
        binding.slContentAddOwner.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.GONE
        binding.btRegisterOwner.isEnabled = true
    }

    private fun showFullScreenLoadingLayout() {
        binding.slContentAddOwner.visibility = View.GONE
        binding.btRegisterOwner.isEnabled = false
        findViewById<LinearLayout>(R.id.lottie_loading_utensils).visibility = View.VISIBLE

    }

    private fun setToolbar() {
        findViewById<TextView>(R.id.tv_tbTitle).text = "Owner Signup"
        findViewById<ImageButton>(R.id.bt_backToolbar).setOnClickListener {
            finish()
        }
    }
}