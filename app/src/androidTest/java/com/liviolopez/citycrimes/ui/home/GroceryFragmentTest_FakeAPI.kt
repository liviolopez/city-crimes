package com.liviolopez.citycrimes.ui.home

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
import javax.inject.Inject

@SmallTest
@HiltAndroidTest
class HomeFragmentTest_FakeAPI{

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

        launchFragmentInHiltContainer<HomeFragment>(factory = fragmentFactory)
    }

    @After
    fun tearDown() = mockWebServer.shutdown()

    @Test
    fun is_OverlayStandByView_on_initial_status(){
        onView(withId(R.id.progress_bar_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.error_container)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.empty_list)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun test_(){

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("citycrimes.crimes.json"))
            }
        }
    }
}
