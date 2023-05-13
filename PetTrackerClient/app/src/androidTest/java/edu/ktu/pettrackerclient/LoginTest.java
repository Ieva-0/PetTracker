package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<StartActivity> mActivityTestRule = new ActivityScenarioRule<>(StartActivity.class);
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getInstrumentation().getTargetContext();
        assertEquals("edu.ktu.pettrackerclient", appContext.getPackageName());
    }

    String username_good = "bbbb";
    String password_good = "bbbbbbbb";
    @Test
    public void loginTest_success() throws InterruptedException {
        Espresso.onView(withId(R.id.welcome_login)).perform(click());
        Espresso.onView(withId(R.id.loginBtn)).perform(click());
        Espresso.onView(withId(R.id.login_username_1)).perform(typeText(username_good));
        Espresso.onView(withId(R.id.login_password_1)).perform(typeText(password_good));
//        Espresso.onView(withText("")).perform(ViewActions.closeSoftKeyboard());
        Espresso.pressBack();
        Espresso.onView(withId(R.id.loginBtn)).perform(click());
        Espresso.onView(withText("Login")).check(doesNotExist());
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }

    String username_bad = "asd";
    String password_bad = "fgfdg";
    @Test
    public void loginTest_fail() {
//        Espresso.onView(withId(R.id.welcome_login)).perform(click());
//        Espresso.onView(withId(R.id.login_username_1)).perform(typeText(username_bad));
//        Espresso.onView(withId(R.id.login_password_1)).perform(typeText(password_bad));
//        Espresso.pressBack();
//        Espresso.onView(withId(R.id.loginBtn)).perform(click());
//        Espresso.onView(withText("Login")).check(matches(isDisplayed()));

//        ActivityScenario scenario = mActivityTestRule.getScenario();
//        scenario.recreate();
    }
}