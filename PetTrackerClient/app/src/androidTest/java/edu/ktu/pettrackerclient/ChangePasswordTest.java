package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
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
public class ChangePasswordTest {

    @Rule
    public ActivityScenarioRule<StartActivity> mActivityTestRule = new ActivityScenarioRule<>(StartActivity.class);
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getInstrumentation().getTargetContext();
        assertEquals("edu.ktu.pettrackerclient", appContext.getPackageName());
    }

    String name = "";
    @Test
    public void changePassword() throws InterruptedException {
        String name = String.valueOf(System.currentTimeMillis());
        Espresso.onView(withId(R.id.welcome_register)).perform(click());
        Espresso.onView(withId(R.id.registerBtn)).perform(click());
        Espresso.onView(withId(R.id.register_username_1)).perform(typeText(name));
        Espresso.onView(withId(R.id.register_email_1)).perform(typeText(name + "@gmail.com"));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.register_password_1)).perform(typeText(name));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.register_confirmPassword_1)).perform(typeText(name));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.registerBtn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawerNav_accountFragment)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.account_changePwBtn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.changePw_cancel)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.account_changePwBtn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.account_oldPassword_1)).perform(typeText(name));
        Espresso.pressBack();
        onView(withId(R.id.account_newPassword_1)).perform(typeText(name + "1"));
        Espresso.pressBack();
        onView(withId(R.id.account_confirmPassword_1)).perform(typeText(name + "1"));
        Espresso.pressBack();
        onView(withId(R.id.changePw_btn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }
}