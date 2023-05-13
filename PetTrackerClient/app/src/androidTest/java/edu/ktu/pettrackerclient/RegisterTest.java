package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterTest {

    @Rule
    public ActivityScenarioRule<StartActivity> mActivityTestRule = new ActivityScenarioRule<>(StartActivity.class);
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("edu.ktu.pettrackerclient", appContext.getPackageName());
    }

    @Test
    public void registerTest() throws InterruptedException {
        String name = String.valueOf(System.currentTimeMillis());
        Espresso.onView(withId(R.id.welcome_register)).perform(click());
        Espresso.onView(withId(R.id.registerBtn)).perform(click());
        Espresso.onView(withId(R.id.register_username_1)).perform(typeText(name));
        Espresso.onView(withId(R.id.register_email_1)).perform(typeText(name + "@gmail.com"));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.register_password_1)).perform(typeText("bbbbbbbb"));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.register_confirmPassword_1)).perform(typeText("bbbbbbbb"));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.registerBtn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }
}
