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
public class PetGroupTest {
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
    String pet_group_name = "";
    @Test
    public void petGroupActions() throws InterruptedException {
        pet_group_name = String.valueOf(System.currentTimeMillis());
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
        onView(withId(R.id.drawerNav_petGroupListFragment)).perform(click());
        onView(withId(R.id.petGroup_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_addPetGroup)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.petGroupCreate_cancel)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.fab_addPetGroup)).perform(click());
        Thread.sleep(2000);
        //create pet
        onView(withId(R.id.petGroupCreate_btn)).perform(click());
        onView(withId(R.id.petGroupCreate_name_1)).perform(typeText(pet_group_name));
        Espresso.pressBack();
        onView(withId(R.id.petGroupCreate_choosePets)).perform(click());
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.petGroupCreate_choosePets)).perform(click());
        onView(withText("pet1"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("pet3"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.petGroupCreate_enableNotifications)).perform(click());
        onView(withId(R.id.petGroupCreate_btn)).perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.petGroupSearch_name_1)).perform(typeText(pet_group_name));
        Espresso.pressBack();
        //edit cancel
        onView(withId(R.id.petGroup_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petGroupItem_edit)));
        Thread.sleep(2000);
        onView(withId(R.id.petGroupCreate_cancel)).perform(click());
        Thread.sleep(1000);
        //search
        onView(withId(R.id.petGroupSearch_name_1)).perform(typeText(pet_group_name));
        Espresso.pressBack();
        //edit
        onView(withId(R.id.petGroup_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petGroupItem_edit)));
        onView(withId(R.id.petGroupCreate_name_1)).perform(typeText("1"));
        Espresso.pressBack();
        onView(withId(R.id.petGroupCreate_btn)).perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.petGroupSearch_name_1)).perform(typeText(pet_group_name + "1"));
        Espresso.pressBack();
        //delete
        onView(withId(R.id.petGroup_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petGroupItem_delete)));
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.petGroup_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.petGroupItem_delete)));
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
