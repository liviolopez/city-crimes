package com.liviolopez.citycrimes

import com.liviolopez.citycrimes.other.StringUtilsTest
import com.liviolopez.citycrimes.other.ThemeColorTest
import com.liviolopez.citycrimes.ui.MainActivityTest
import com.liviolopez.citycrimes.ui.details.SourceFragmentTest_FakeAPI
import com.liviolopez.citycrimes.ui.details.SourceFragmentTest_RealAPI
import com.liviolopez.citycrimes.ui.home.StoriesFragmentTest_FakeAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityTest::class,
    StoriesFragmentTest_FakeAPI::class,
    SourceFragmentTest_FakeAPI::class,
    SourceFragmentTest_RealAPI::class,
    ThemeColorTest::class,
    StringUtilsTest::class
)
class AppTestSuite