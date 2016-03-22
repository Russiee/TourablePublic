package com.hobbyte.touringandroid.ui.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;



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

/**
 * Created by Nikita on 21/03/2016.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class StartActivityTest {

    private String validKey;
    private String invalidKey;
    private TourDBManager db;
    RenamingDelegatingContext context;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(
            SplashActivity.class);

    @Before
    public void initValidKey() {
        validKey = "KCL-1010";
        invalidKey = "invalidText";
        context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_"
        );

        db = TourDBManager.getInstance(context);
    }

    @Test
    public void ATestEnterCorrectKey() {
        enterKey(validKey);
        onView(withId(R.id.downloadTourText)).check(matches(isDisplayed()));
    }

    @Test
    public void BTestEnterInvalidKey() {
        enterKey(invalidKey);
        onView(withId(R.id.downloadTourText)).check(doesNotExist());
    }

    @Test
    public void CTestEnterSummaryWithoutMedia() {
        enterKey(validKey);
        onView(withId(R.id.download_without_media)).perform(click());
        onView(withId(R.id.txtTourDescription)).check(matches(isDisplayed()));
    }

    @Test
    public void DTestDuplicateKey() {
        enterKey(validKey);
        onView(withId(R.id.download_without_media)).check(doesNotExist());
    }

    @Test
    public void ETestDeleteTour() {
        onView(withId(R.id.tourItem)).perform(longClick());
        onView(withText("Delete")).perform(click());
        onView(withId(R.id.tourItem)).check(doesNotExist());
    }

    @Test
    public void FTestEnterSummaryWithMedia() {
        enterKey(validKey);
        onView(withId(R.id.download_with_media)).perform(click());
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.tourItem)).check(matches(isDisplayed()));
    }

    @Test
    public void HTestClickExistingTour() {
        onView(withId(R.id.tourItem)).perform(click());
        onView(withId(R.id.buttonStartTour)).check(matches(isDisplayed()));
        onView(withId(R.id.txtTourDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.tourCard)).check(matches(isDisplayed()));
    }

    @Test
    public void KTestEnterTourActivity() {
        enterTourActivity();
        onView(withId(R.id.sectionDescription)).check(matches(isDisplayed()));
        onView(withText("The Flat")).check(matches(isDisplayed()));
    }

    @Test
    public void LTestOpenSections() {
        enterTourActivity();
        onView(withText("The Flat")).perform(click());
        onView(withText("Alex's Room")).check(matches(isDisplayed()));
        onView(withText("Peter's Room")).check(matches(isDisplayed()));
        onView(withText("The Kitchen")).check(matches(isDisplayed()));
    }

    @Test
    public void MTestFlatSubsections() {
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

    @Test
    public void NTestPOIs() {
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

    @Test
    public void testPOINavigation() {
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
        pressBack();
        pressBack();
        pressBack();
        pressBack();
        pressBack();
        onView(withId(R.id.tourItem)).perform(longClick());
        onView(withText("Delete")).perform(click());
    }

    public void enterKey(String key) {
        onView(withId(R.id.addTourButton)).perform(click());
        onView(withId(R.id.textKey)).perform(typeText(key), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
    }

    public void enterTour() {
        onView(withId(R.id.tourItem)).perform(click());
    }

    public void enterTourActivity() {
        enterTour();
        onView(withId(R.id.buttonStartTour)).perform(click());
    }

    public void deleteTour() {
        onView(withId(R.id.tourItem)).perform(longClick());
        onView(withText("Delete")).perform(click());
    }

}
