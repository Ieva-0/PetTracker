package edu.ktu.pettrackerclient;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

import static edu.ktu.pettrackerclient.ZoneTest.clickXY;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PetTest {
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
    String pet_name = "";
    @Test
    public void petActions() throws InterruptedException {
        pet_name = String.valueOf(System.currentTimeMillis());
        //login
        onView(withId(R.id.welcome_login)).perform(click());
        onView(withId(R.id.login_username_1)).perform(typeText(username_good));
        onView(withId(R.id.login_password_1)).perform(typeText(password_good));
        Espresso.pressBack();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withText("Login")).check(doesNotExist());
        Thread.sleep(2000);
        //go to pets
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawerNav_petList)).perform(click());
        onView(withId(R.id.pet_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_addPet)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.petCreate_cancel)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.fab_addPet)).perform(click());
        Thread.sleep(2000);
        //create pet
        onView(withId(R.id.petCreate_btn)).perform(click());
        onView(withId(R.id.petCreate_name_1)).perform(typeText(pet_name));
        Espresso.pressBack();

//        onView(withId(R.id.petCreate_upload)).perform(click());
//        Thread.sleep(5000);

        onView(withId(R.id.petCreate_enableNotifications)).perform(click());
        onView(withId(R.id.petCreate_btn)).perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.petSearch_name_1)).perform(typeText(pet_name));
        Espresso.pressBack();
        //edit cancel
        onView(withId(R.id.pet_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petItem_options)));
        onView(withText("Edit")).inRoot(isPopupWindow()).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.petCreate_cancel)).perform(click());
        Thread.sleep(1000);
        //search
        onView(withId(R.id.petSearch_name_1)).perform(typeText(pet_name));
        Espresso.pressBack();
        //edit
        onView(withId(R.id.pet_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petItem_options)));
        onView(withText("Edit")).inRoot(isPopupWindow()).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.petCreate_name_1)).perform(typeText("1"));
        Espresso.pressBack();
        onView(withId(R.id.petCreate_btn)).perform(click());
        Thread.sleep(2000);

        //location
        onView(withId(R.id.pet_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petItem_options)));
        onView(withText("See location")).inRoot(isPopupWindow()).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.bottomNavigation_history)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.bottomNavigation_radar)).perform(click());
        Thread.sleep(2000);
        Espresso.pressBack();

        //search
        onView(withId(R.id.petSearch_name_1)).perform(typeText(pet_name + "1"));
        //delete
        onView(withId(R.id.pet_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petItem_options)));
        onView(withText("Delete")).inRoot(isPopupWindow()).perform(click());
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.pet_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petItem_options)));
        onView(withText("Delete")).inRoot(isPopupWindow()).perform(click());
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.logout_btn)).perform(click());
    }

    public static Matcher<Root> isPopupWindow() {
        return isPlatformPopup();
    }
}
