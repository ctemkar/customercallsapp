package com.smartshehar.customercallingv2.repositories.account

import com.smartshehar.customercallingv2.models.Owner
import com.smartshehar.customercallingv2.repositories.api.AuthApi
import javax.inject.Inject

class AccountRepository {

    @Inject
    lateinit var authApi : AuthApi


}
