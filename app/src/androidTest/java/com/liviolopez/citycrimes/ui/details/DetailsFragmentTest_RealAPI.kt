package com.liviolopez.citycrimes.ui.details

import androidx.core.os.bundleOf
import androidx.test.filters.SmallTest
import com.liviolopez.citycrimes._components.launchFragmentInHiltContainer
import com.liviolopez.citycrimes.base.Constants
import com.liviolopez.citycrimes.di.RemoteParamsModule
import com.liviolopez.citycrimes.ui._components.AppFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton


@SmallTest
@UninstallModules(RemoteParamsModule::class)
@HiltAndroidTest
class DetailsFragmentTest_RealAPI {

    @Module
    @InstallIn(SingletonComponent::class)
    object RemoteParamsModule {

        @Singleton
        @Provides
        fun provideBaseUrl(): String {
            return Constants.BASE_URL
        }

    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: AppFragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
        launchFragmentInHiltContainer<DetailsFragment>(
            factory = fragmentFactory, fragmentArgs = bundleOf(
                "persistentId" to "d9fba874c506c7abf48c7bd2fd85d04345e6c87f1ba22b7c24702afb1ad5d378"
            )
        )
    }

    @Test
    fun is_Url_source_the_same_that_return_the_api(){
        Thread.sleep(3000)

    }

    @Test
    fun is_the_Fragment_title_the_Source_name_that_return_the_api(){
        Thread.sleep(3000)

    }
}