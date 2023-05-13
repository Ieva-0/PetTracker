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
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ZoneTest {
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
    String zone_name = "";
    @Test
    public void zoneActions() throws InterruptedException {
        zone_name = String.valueOf(System.currentTimeMillis());
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
        onView(withId(R.id.drawerNav_zoneList)).perform(click());
        onView(withId(R.id.zone_list)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_addZone)).perform(click());
        Thread.sleep(2000);
        //create pet
        onView(withId(R.id.map_zone)).perform(clickXY(400, 400));
        Thread.sleep(1000);
        onView(withId(R.id.map_zone)).perform(clickXY(1000, 1000));
        Thread.sleep(1000);
        onView(withId(R.id.map_zone)).perform(clickXY(1100, 1100));
        Thread.sleep(1000);
        onView(withId(R.id.map_zone)).perform(clickXY(500, 200));
        Thread.sleep(1000);
        onView(withId(R.id.zoneCreate_Btn)).perform(click());
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.zoneCreate_Btn)).perform(click());
        onView(withText("Save"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.zoneCreate_name_1))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(typeText(zone_name));
        Espresso.pressBack();
        onView(withText("Save"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);
        //search
        onView(withId(R.id.zoneSearch_name_1)).perform(typeText(zone_name));
        Espresso.pressBack();
        //edit
        onView(withId(R.id.zone_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.zoneItem_edit)));
        Thread.sleep(2000);
        onView(withId(R.id.map_zone)).perform(clickXY(200, 200));
        Thread.sleep(1000);
        onView(withId(R.id.map_zone)).perform(clickXY(300, 500));
        Thread.sleep(1000);
        onView(withId(R.id.modifyPoints_btn)).perform(click());
        onView(withId(R.id.zonePointList)).perform(RecyclerViewActions.actionOnItemAtPosition(3, MyViewAction.clickChildViewWithId(R.id.zonePointItem_delete)));
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.zonePointList)).perform(RecyclerViewActions.actionOnItemAtPosition(3, MyViewAction.clickChildViewWithId(R.id.zonePointItem_delete)));
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.dismissbtn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.zoneCreate_Btn)).perform(click());
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.zoneCreate_Btn)).perform(click());
        onView(withId(R.id.zoneCreate_name_1))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(typeText("1"));
        Espresso.pressBack();
        onView(withText("Save"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        //search
        onView(withId(R.id.zoneSearch_name_1)).perform(typeText(zone_name + "1"));
        Espresso.pressBack();
        //delete
        onView(withId(R.id.zone_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.zoneItem_delete)));
        onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.zone_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.zoneItem_delete)));
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

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}
