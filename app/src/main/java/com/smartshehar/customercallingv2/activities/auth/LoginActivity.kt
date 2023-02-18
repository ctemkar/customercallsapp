package com.smartshehar.customercallingv2.activities.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.smartshehar.customercallingv2.activities.home.HomeActivity
import com.smartshehar.customercallingv2.databinding.ActivityLoginBinding
import com.smartshehar.customercallingv2.models.dtos.LoginRq
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.utils.states.AuthState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var authState : AuthState

    lateinit var binding: ActivityLoginBinding
    val viewModel: AuthenticationVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvErrorLogin.visibility = View.GONE
        binding.pgBarLogin.visibility = View.GONE

        if(authState.isUserLoggedIn()){
            startActivity(Intent(applicationContext, HomeActivity::class.java))
        }

        binding.btLogin.setOnClickListener {
            val loginRq = LoginRq()
            loginRq.contactNumber = binding.etLoginContact.text.toString()
            loginRq.password = binding.etLoginPassword.text.toString()
            if (validateLoginRq(loginRq)) {
                binding.pgBarLogin.visibility = View.VISIBLE
                binding.pgBarLogin.isIndeterminate = true
                binding.tvErrorLogin.visibility = View.GONE
                viewModel.loginOwner(loginRq).observe(this) {
                    when (it.eventStatus) {
                        EventStatus.LOADING -> {
                        }
                        EventStatus.SUCCESS -> {
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                        }
                        EventStatus.ERROR -> {
                            binding.pgBarLogin.visibility = View.GONE
                            binding.tvErrorLogin.text = it.error
                            binding.tvErrorLogin.visibility = View.VISIBLE
                        }
                        EventStatus.EMPTY -> TODO()
                    }
                }
            }
        }
    }

    private fun validateLoginRq(loginRq: LoginRq): Boolean {
        return true
    }
}