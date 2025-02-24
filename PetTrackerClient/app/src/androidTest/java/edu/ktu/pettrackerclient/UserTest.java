package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
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
import androidx.test.espresso.Root;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserTest {
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
    String searchTerm = "";
    @Test
    public void userActions() throws InterruptedException {
        //login
        onView(withId(R.id.welcome_login)).perform(click());
        onView(withId(R.id.login_username_1)).perform(typeText(username_good));
        onView(withId(R.id.login_password_1)).perform(typeText(password_good));
        Espresso.pressBack();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withText("Login")).check(doesNotExist());
        Thread.sleep(2000);
        //go to users
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawerNav_userList)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.user_list)).check(matches(isDisplayed()));
        //search
        onView(withId(R.id.userSearch_name_1)).perform(typeText("16"));
        onView(withId(R.id.userSearch_rolePicker)).perform(click());
        onView(withText("User")).inRoot(isPopupWindow()).perform(click());
        Thread.sleep(1000);

        //delete
        onView(withId(R.id.user_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.userItem_delete)));
        Thread.sleep(1000);
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.user_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.userItem_delete)));
        Thread.sleep(1000);
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
        //block
        onView(withId(R.id.user_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.userItem_block)));
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.user_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.userItem_block)));
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }
    public static Matcher<Root> isPopupWindow() {
        return isPlatformPopup();
    }
}
