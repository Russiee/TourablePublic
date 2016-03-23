package com.hobbyte.touringandroid.ui.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;

/**
 * Runs through all the main functionality of the app, such as downloading and stepping through a
 * tour.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRunThrough {

    private static final String KEY_ID = "cjWRKDygIZ";
    private static final String GOOD_KEY = "KCL-1010";
    private static final String BAD_KEY = "nokeyhere";
    private static final String UNCLEAN_KEY = "KC  \\ L -/10//10";
    private static final String TOUR_NAME = "Ultimate Flat Tour";
    private boolean first = true;

    @Rule
    public ActivityTestRule<StartActivity> mActivityRule = new ActivityTestRule<>(
            StartActivity.class);

    @Before
    public void setUp() throws Exception {
        // need to ensure that this tests starts without the tour under test downloaded
        if (first) {
            File dir = new File(App.context.getFilesDir(), KEY_ID);

            if (dir.exists()) {
                System.out.println("poop");
                FileManager.removeTour(App.context, KEY_ID);
                Thread.sleep(1500);
                first = false;
            }
        }
    }

    /**
     * Make sure that the download dialog displays when a valid key is entered.
     */
    @Test
    public void aTestEnterCorrectKey() {
        enterKey(GOOD_KEY);
        onView(withId(R.id.downloadTourText)).check(matches(isDisplayed()));
    }

    /**
     * Make sure that unwanted characters get removed from what the user enters.
     */
    @Test
    public void bTestUncleanCorrectKey() {
        enterKey(UNCLEAN_KEY);
        onView(withId(R.id.downloadTourText)).check(matches(isDisplayed()));
    }

    /**
     * Make sure that the download dialog does not show when an invalid key is entered.
     */
    @Test
    public void cTestEnterInvalidKey() {
        enterKey(BAD_KEY);
        onView(withId(R.id.downloadTourText)).check(doesNotExist());
    }

    /**
     * Make sure that the user is taken back to StartActivity after downloading a tour.
     */
    @Test
    public void dTestEnterSummaryWithoutMedia() throws Exception {
        enterKey(GOOD_KEY);
        onView(withId(R.id.download_without_media)).perform(click());
        Thread.sleep(1000);

        onView(withText(TOUR_NAME)).check(matches(isDisplayed()));
    }

    /**
     * Make sure that entering the key of a tour that has already been downloaded will
     * not give the option to download it again.
     */
    @Test
    public void eTestDuplicateKey() {
        enterKey(GOOD_KEY);
        onView(withId(R.id.download_without_media)).check(doesNotExist());
    }

    /**
     * Make sure that the mechanism for deleting tours (from a long-click menu)
     * functions properly.
     */
    @Test
    public void fTestDeleteTour() {
        onView(withText(TOUR_NAME)).perform(longClick());
        onView(withText("Delete")).perform(click());
        onView(withText(TOUR_NAME)).check(doesNotExist());

        TourDBManager db = TourDBManager.getInstance(App.context);
        assertFalse(db.doesTourKeyNameExist(GOOD_KEY));
    }

    /**
     * Make sure that the user is taken back to StartActivity after downloading a tour with
     * the other option, and the tour is there.
     */
    @Test
    public void gTestEnterSummaryWithMedia() throws Exception {
        enterKey(GOOD_KEY);
        onView(withId(R.id.download_with_media)).perform(click());
        Thread.sleep(1000);

        onView(withText(TOUR_NAME)).check(matches(isDisplayed()));
    }

    /**
     * These tests are for TourActivity class, testing navigation.
     */
    @Test
    public void hTestEnterTourActivity() {
        enterTourActivity();
        onView(withId(R.id.sectionDescription)).check(matches(isDisplayed()));
        onView(withText("The Flat")).check(matches(isDisplayed()));
    }

    @Test
    public void iTestOpenSections() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).check(matches(isDisplayed()));
        onView(withText("Peter's Room")).check(matches(isDisplayed()));
        onView(withText("The Kitchen")).check(matches(isDisplayed()));
    }

    /**
     * Make sure that the 'back to section' button takes the user back to the parent section.
     */
    @Test
    public void jTestBackToSectionButton() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).perform(click());
        onView(withText("Alex's Bed")).perform(click());
        onData(instanceOf(String.class)).onChildView(withId(R.id.nextPOIFooter)).perform(click());
        onData(instanceOf(String.class)).onChildView(withId(R.id.backToSectionButton)).perform(click());
        onView(withText("Alex's Room")).check(matches(isDisplayed()));
    }

    @Test
    public void kTestFlatSubsections() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).perform(click());
        onView(withText("Alex's Bed")).check(matches(isDisplayed()));
        onView(withText("Alex's Desk")).check(matches(isDisplayed()));
        pressBack();
        onView(withText("Peter's Room")).perform(click());
        onView(withText("Peter's Bed")).check(matches(isDisplayed()));
        onView(withText("Peter's Desk")).check(matches(isDisplayed()));
        pressBack();
        onView(withText("The Kitchen")).perform(click());
        onView(withText("The Living Room")).check(matches(isDisplayed()));
        onView(withText("Kitchen")).check(matches(isDisplayed()));
        onView(withText("The Living Room")).perform(click());
        onView(withText("Drying Rack")).check(matches(isDisplayed()));
    }

    /**
     * Make sure that POis have the expected content.
     */
    @Test
    public void lTestPOIs() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).perform(click());
        onView(withText("Alex's Bed")).perform(click());
        onView(withText(containsString("This is where Alex"))).check(matches(isDisplayed()));
        pressBack();
        onView(withText("Alex's Desk")).perform(click());
        onView(withText(containsString("Here is where Alexander Gubbay (born 1995)"))).check(matches(isDisplayed()));
        pressBack();
        pressBack();
        onView(withText("Peter's Room")).perform(click());
        onView(withText("Peter's Desk")).perform(click());
        onView(withText(containsString("Peter Barta (born 1996)"))).check(matches(isDisplayed()));
        pressBack();
        onView(withText("Peter's Bed")).perform(click());
        onView(withText(containsString("Messy messy messy"))).check(matches(isDisplayed()));
        pressBack();
        pressBack();
        onView(withText("The Kitchen")).perform(click());
        onView(withText("Kitchen")).perform(click());
        onView(withText(containsString("crucial part"))).check(matches(isDisplayed()));
        pressBack();
        onView(withText("The Living Room")).perform(click());
        onView(withText("Drying Rack")).perform(click());
        onView(withText(containsString("where clothes"))).check(matches(isDisplayed()));
    }

    /**
     * Make sure that the back button works as expected.
     */
    @Test
    public void mTestPOINavigation() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).perform(click());
        onView(withText("Alex's Bed")).perform(click());

        onData(instanceOf(String.class)).onChildView(withId(R.id.nextPOIFooter)).check(matches(isDisplayed()));
        onView(withText(containsString("Go to Previous POI"))).check(doesNotExist());
        onData(instanceOf(String.class)).onChildView(withId(R.id.nextPOIFooter)).perform(click());

        onView(withText(containsString("Alexander Gubbay (born 1995)"))).check(matches(isDisplayed()));
        onData(instanceOf(String.class)).onChildView(withId(R.id.previousPOIFooter)).check(matches(isDisplayed()));
        onData(instanceOf(String.class)).onChildView(withId(R.id.previousPOIFooter)).perform(click());
        onData(instanceOf(String.class)).onChildView(withId(R.id.backToSectionButton)).perform(click());
        onView(withText("Alex's Bed")).check(matches(isDisplayed()));
        pressBack();
        pressBack();
        pressBack();
        pressBack();
        onView(withText(TOUR_NAME)).perform(longClick());
        onView(withText("Delete")).perform(click());
    }

    public void enterKey(String key) {
        onView(withId(R.id.addTourButton)).perform(click());
        onView(withId(R.id.textKey)).perform(typeText(key), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
    }

    public void enterTour() {
        onView(withText(TOUR_NAME)).perform(click());
    }

    public void enterTourActivity() {
        enterTour();
        onView(withId(R.id.buttonStartTour)).perform(click());
    }

    public void deleteTour() {
        onView(withText(TOUR_NAME)).perform(longClick());
        onView(withText("Delete")).perform(click());
    }
}
