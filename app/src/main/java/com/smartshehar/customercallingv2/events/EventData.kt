package com.amaze.emanage.events

import com.smartshehar.customercallingv2.events.EventStatus

class EventData<T> {
    lateinit var eventStatus : EventStatus
    var data : T? = null
    lateinit var error : String
}