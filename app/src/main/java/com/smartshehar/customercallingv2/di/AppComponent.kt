package com.smartshehar.customercallingv2.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DBModule::class,RepoModule::class] )
interface AppComponent {

}