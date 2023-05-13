package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DeviceTest {
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
    String device_name = "";
    String device_password = "bbbbbbbb";
    @Test
    public void deviceActions() throws InterruptedException {
        device_name = String.valueOf(System.currentTimeMillis());
        //login
        onView(withId(R.id.welcome_login)).perform(click());
        onView(withId(R.id.login_username_1)).perform(typeText(username_good));
        onView(withId(R.id.login_password_1)).perform(typeText(password_good));
        Espresso.pressBack();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withText("Login")).check(doesNotExist());
        Thread.sleep(2000);
        //go to devices
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawerNav_deviceList)).perform(click());
        onView(withId(R.id.device_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_addDevice)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.deviceCreate_cancel)).perform(click());
        onView(withId(R.id.fab_addDevice)).perform(click());
        Thread.sleep(2000);
        //create device
        onView(withId(R.id.deviceCreate_btn)).perform(click());
        onView(withId(R.id.deviceCreate_name_1)).perform(typeText(device_name));
        onView(withId(R.id.deviceCreate_password_1)).perform(typeText(device_password));
        Espresso.pressBack();
        onView(withId(R.id.deviceCreate_btn)).perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.deviceSearch_name_1)).perform(typeText(device_name));
        Espresso.pressBack();
        //edit cancel
        onView(withId(R.id.device_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.deviceItem_edit)));
        Thread.sleep(2000);
        onView(withId(R.id.deviceEdit_cancel)).perform(click());
        Thread.sleep(1000);
        //search
        onView(withId(R.id.deviceSearch_name_1)).perform(typeText(device_name));
        Espresso.pressBack();
        //edit
        onView(withId(R.id.device_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.deviceItem_edit)));
        Thread.sleep(2000);
        onView(withId(R.id.deviceEdit_name_1)).perform(typeText("1"));
        onView(withId(R.id.deviceEdit_newPassword_1)).perform(typeText(device_password + "1"));
        Espresso.pressBack();
        onView(withId(R.id.deviceEdit_currentPassword_1)).perform(typeText(device_password));
        Espresso.pressBack();
        onView(withId(R.id.deviceEdit_btn)).perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.deviceSearch_name_1)).perform(typeText(device_name + "1"));
        //delete
        onView(withId(R.id.device_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.deviceItem_delete)));
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.device_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.deviceItem_delete)));
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }
}
