package com.sylvainautran.nanodegree.capstoneproject;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelDbHelperTest;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelProviderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AppelDbHelperTest.class,
        AppelProviderTest.class})
public class FullTestSuite {}
