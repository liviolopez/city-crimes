package com.liviolopez.citycrimes.ui.details

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.SmallTest
import com.liviolopez.citycrimes.R
import com.liviolopez.citycrimes._components.FileReader
import com.liviolopez.citycrimes._components.launchFragmentInHiltContainer
import com.liviolopez.citycrimes.ui._components.AppFragmentFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@SmallTest
@HiltAndroidTest
class DetailsFragmentTest_FakeAPI {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: AppFragmentFactory

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()

        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

        launchFragmentInHiltContainer<DetailsFragment>(factory = fragmentFactory, fragmentArgs = bundleOf("persistentId" to "d9fba874c506c7abf48c7bd2fd85d04345e6c87f1ba22b7c24702afb1ad5d378"))
    }

    @After
    fun tearDown() = mockWebServer.shutdown()

    @Test
    fun is_OverlayStandByView_on_initial_status(){
        onView(withId(R.id.progress_bar_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.error_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.empty_list)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun is_ProgressBar_shown_and_hidden_after_load_api_data(){

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBodyDelay(1,TimeUnit.SECONDS)
                    .setBody(FileReader.readStringFromFile("citycrimes.crimes.json"))
            }
        }

        onView(withId(R.id.progress_bar_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.VISIBLE)))
        Thread.sleep(1100)
        onView(withId(R.id.progress_bar_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun is_Url_source_the_same_that_return_the_api(){

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("citycrimes.crimes.json"))
            }
        }

        Thread.sleep(300)
    }
}