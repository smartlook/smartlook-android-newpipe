package org.schabi.newpipe.espresso;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.schabi.newpipe.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import androidx.test.espresso.matcher.ViewMatchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

abstract class EspressoTestBase {

    // User delay boundaries

    private static final long NO_USER_DELAY = -1;

    // quick test
    private static final long QUICK_TEST_USER_ACTION_MIN_DELAY = 100;
    private static final long QUICK_TEST_USER_ACTION_MAX_DELAY = 150;

    // standard test
    private static final long USER_ACTION_MIN_DELAY = TimeUnit.SECONDS.toMillis(1);
    private static final long USER_ACTION_MAX_DELAY = TimeUnit.SECONDS.toMillis(4);

    // Test action constants

    static final int CLICK_ON_TRENDING_TAB = 0;
    static final int CLICK_ON_SUBSCRIPTIONS_TAB = 1;
    static final int CLICK_ON_BOOKMARKED_PLAYLIST_TAB = 2;
    static final int CLICK_ON_TOOLBAR_SEARCH_ACTION = 3;
    static final int TYPE_SMARTLOOK_INTO_SEARCH_INPUT = 4;
    static final int CLICK_IME_BUTTON_ON_SEARCH_INPUT = 5;
    static final int CLICK_ON_NAVIGATE_UP = 6;
    static final int OPEN_HAMBURGER_MENU = 7;
    static final int CLICK_HAMBURGER_MENU_SETTINGS = 8;
    static final int CLICK_SETTINGS_DEBUG = 9;
    static final int CLICK_ON_TRENDING_VIDEO = 10;
    static final int PRESS_BACK = 11;
    static final int CLICK_EMPTY_STATE_ASCII_ART = 12;
    static final int CLICK_EMPTY_STATE_TEXT = 13;
    static final int SWIPE_PAGER_LEFT = 14;
    static final int SWIPE_PAGER_RIGHT = 15;
    static final int LONG_CLICK_ON_TRENDING_VIDEO = 16;
    static final int CLICK_ADD_TO_PLAYLIST = 17;
    static final int CLICK_NEW_PLAYLIST_CANCEL = 18;


    void runTestWithNoDelay(int[] testScenario) {
        runTest(testScenario, null, NO_USER_DELAY, NO_USER_DELAY);
    }

    void runTestWithNoDelay(int[] testScenario,
                            @Nullable TestViewClickListener testViewClickListener) {

        runTest(testScenario, testViewClickListener, NO_USER_DELAY, NO_USER_DELAY);
    }

    void runTest(int[] testScenario) {
        runTest(testScenario, null);
    }

    void runTest(int[] testScenario,
                 @Nullable TestViewClickListener testViewClickListener) {

        runTest(testScenario, testViewClickListener, USER_ACTION_MIN_DELAY, USER_ACTION_MAX_DELAY);
    }

    void runTest(int[] testScenario,
                 @Nullable TestViewClickListener testViewClickListener,
                 long minimalUserActionDelay,
                 long maximalUserActionDelay) {

        if (minimalUserActionDelay > maximalUserActionDelay) {
            throw new InvalidParameterException("minimalUserActionDelay has to be lower or equal to maximalUserActionDelay!");
        }

        for (int testAction : testScenario) {

            generateUserActionDelay(minimalUserActionDelay, maximalUserActionDelay);

            if (testViewClickListener != null) {
                testViewClickListener.onClick(clickedViewClassName(testAction));
            }

            performTestAction(testAction);
        }
    }

    private void performTestAction(int testAction) {
        System.out.println("Simulating user action: " + testActionLogString(testAction));

        switch (testAction) {
            case CLICK_ON_TRENDING_TAB:
                onView(childAtPosition(childAtPosition(withId(R.id.main_tab_layout), 0), 0)).perform(click());
                break;

            case CLICK_ON_SUBSCRIPTIONS_TAB:
                onView(childAtPosition(childAtPosition(withId(R.id.main_tab_layout), 0), 1)).perform(click());
                break;

            case CLICK_ON_BOOKMARKED_PLAYLIST_TAB:
                onView(childAtPosition(childAtPosition(withId(R.id.main_tab_layout), 0), 2)).perform(click());
                break;

            case CLICK_ON_TOOLBAR_SEARCH_ACTION:
                onView(withId(R.id.action_search)).perform(click());
                break;

            case TYPE_SMARTLOOK_INTO_SEARCH_INPUT:
                onView(withId(R.id.toolbar_search_edit_text)).perform(replaceText("smartlook"));
                break;

            case CLICK_IME_BUTTON_ON_SEARCH_INPUT:
                onView(withId(R.id.toolbar_search_edit_text)).perform(pressImeActionButton());
                break;

            case CLICK_ON_NAVIGATE_UP:
                onView(withContentDescription("Navigate up")).perform(click());
                break;

            case OPEN_HAMBURGER_MENU:
                onView(withContentDescription("Open Drawer")).perform(click());
                break;

            case CLICK_HAMBURGER_MENU_SETTINGS:
                onView(
                        allOf(
                                hasDescendant(withText("Settings")),
                                ViewMatchers.withClassName(endsWith("NavigationMenuItemView"))
                        )
                ).perform(click());
                break;

            case CLICK_SETTINGS_DEBUG:
                onView(withText("Debug")).perform(click());
                break;

            case CLICK_ON_TRENDING_VIDEO:
                onView(
                        allOf(
                                childAtPosition(withId(R.id.items_list), 0),
                                ViewMatchers.withClassName(endsWith("RelativeLayout"))
                        )
                ).perform(click());
                break;

            case PRESS_BACK:
                onView(isRoot()).perform(pressBack());
                break;

            case CLICK_EMPTY_STATE_ASCII_ART:
                onView(
                        allOf(
                                withText("¯\\_(ツ)_/¯"),
                                isDisplayed()
                        )
                ).perform(click());
                break;

            case CLICK_EMPTY_STATE_TEXT:
                onView(
                        allOf(
                                withText("Nothing here but crickets"),
                                isDisplayed()
                        )
                ).perform(click());
                break;
            case SWIPE_PAGER_LEFT:
                onView(withId(R.id.pager)).perform(swipeLeft());
                break;
            case SWIPE_PAGER_RIGHT:
                onView(withId(R.id.pager)).perform(swipeRight());
                break;
            case LONG_CLICK_ON_TRENDING_VIDEO:
                onView(
                        allOf(
                                childAtPosition(withId(R.id.items_list), 0),
                                ViewMatchers.withClassName(endsWith("RelativeLayout"))
                        )
                ).perform(longClick());
                break;
            case CLICK_ADD_TO_PLAYLIST:
                onView(
                        allOf(
                                withText("Add To Playlist"),
                                ViewMatchers.withClassName(endsWith("AppCompatTextView"))
                        )
                ).perform(longClick());
                break;
            case CLICK_NEW_PLAYLIST_CANCEL:
                onView(
                        allOf(
                                withText("Cancel"),
                                ViewMatchers.withClassName(endsWith("AppCompatButton"))
                        )
                ).perform(click());
        }
    }

    private String clickedViewClassName(int testAction) {
        switch (testAction) {
            case CLICK_ON_TRENDING_TAB:
            case CLICK_ON_SUBSCRIPTIONS_TAB:
            case CLICK_ON_BOOKMARKED_PLAYLIST_TAB:
                return "AppCompatImageView";
            case CLICK_ON_TOOLBAR_SEARCH_ACTION:
                return "ActionMenuItemView";
            case CLICK_ON_NAVIGATE_UP:
            case OPEN_HAMBURGER_MENU:
                return "AppCompatImageButton";
            case CLICK_HAMBURGER_MENU_SETTINGS:
                return "NavigationMenuItemView";
            case CLICK_SETTINGS_DEBUG:
                return "LinearLayout";
            case CLICK_ON_TRENDING_VIDEO:
            case LONG_CLICK_ON_TRENDING_VIDEO:
                return "RelativeLayout";
            case CLICK_EMPTY_STATE_ASCII_ART:
            case CLICK_EMPTY_STATE_TEXT:
            case CLICK_ADD_TO_PLAYLIST:
                return "AppCompatTextView";
            case CLICK_NEW_PLAYLIST_CANCEL:
                return "AppCompatButton";
            default:
                return null;
        }
    }

    private void generateUserActionDelay(long minimalUserActionDelay, long maximalUserActionDelay) {

        if (minimalUserActionDelay <= NO_USER_DELAY || maximalUserActionDelay <= NO_USER_DELAY) {
            return;
        }

        long userActionDelay = ThreadLocalRandom.current().nextLong(
                minimalUserActionDelay,
                maximalUserActionDelay + 1);

        System.out.println("Simulating user action delay -> sleep for: " + userActionDelay + "ms");
        generateDelay(userActionDelay);
    }

    private void generateDelay(long userDelay) {
        try {
            Thread.sleep(userDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean goThroughLog(@NonNull LogListener logListener) {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logListener.onLine(line);
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean clearLog() {
        try {
            System.out.println("Clearing log!");
            Runtime.getRuntime().exec("logcat -c");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private String testActionLogString(int testAction) {
        switch (testAction) {
            case CLICK_ON_TRENDING_TAB:
                return "Click on trending tab";
            case CLICK_ON_SUBSCRIPTIONS_TAB:
                return "Click on subscriptions tab";
            case CLICK_ON_BOOKMARKED_PLAYLIST_TAB:
                return "Click on bookmarked playlist tab";
            case CLICK_ON_TOOLBAR_SEARCH_ACTION:
                return "Click on toolbar search action";
            case TYPE_SMARTLOOK_INTO_SEARCH_INPUT:
                return "Type \"Smartlook\" into search input";
            case CLICK_IME_BUTTON_ON_SEARCH_INPUT:
                return "Click ime button on search input";
            case CLICK_ON_NAVIGATE_UP:
                return "Click on navigate up";
            case OPEN_HAMBURGER_MENU:
                return "Open hamburger menu";
            case CLICK_HAMBURGER_MENU_SETTINGS:
                return "Click on hamburger menu settings";
            case CLICK_SETTINGS_DEBUG:
                return "Click on settings debug";
            case CLICK_ON_TRENDING_VIDEO:
                return "Click on trending video";
            case PRESS_BACK:
                return "Press back";
            case CLICK_EMPTY_STATE_ASCII_ART:
                return "Click empty state ascii";
            case CLICK_EMPTY_STATE_TEXT:
                return "Click empty state text";
            case SWIPE_PAGER_LEFT:
                return "Swipe pager left";
            case SWIPE_PAGER_RIGHT:
                return "Swipe pager right";
            case LONG_CLICK_ON_TRENDING_VIDEO:
                return "Long click on trending video";
            case CLICK_ADD_TO_PLAYLIST:
                return "Click add to playlist";
            case CLICK_NEW_PLAYLIST_CANCEL:
                return "Click new playlist cancel";
        }

        return "Unknown test action";
    }

    interface TestViewClickListener {
        void onClick(String viewClassName);
    }

    interface LogListener {
        void onLine(String line);
    }
}