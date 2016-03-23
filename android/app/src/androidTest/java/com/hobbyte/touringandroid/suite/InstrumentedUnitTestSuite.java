package com.hobbyte.touringandroid.suite;

import com.hobbyte.touringandroid.internet.ServerAPITest;
import com.hobbyte.touringandroid.internet.UpdateCheckerTest;
import com.hobbyte.touringandroid.io.DBTest;
import com.hobbyte.touringandroid.io.DeleteTourTaskTest;
import com.hobbyte.touringandroid.io.FileManagerTest;
import com.hobbyte.touringandroid.tourdata.PointOfInterestTest;
import com.hobbyte.touringandroid.tourdata.SubSectionTest;
import com.hobbyte.touringandroid.tourdata.TourBuilderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * For convenience, give the possibility to run all of the instrumented unit tests in one go.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DBTest.class, DeleteTourTaskTest.class, FileManagerTest.class, PointOfInterestTest.class,
        ServerAPITest.class, SubSectionTest.class, TourBuilderTest.class, UpdateCheckerTest.class
})
public class InstrumentedUnitTestSuite {}
