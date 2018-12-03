package org.schabi.newpipe.espresso;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.schabi.newpipe.MainActivity;

/**
 * Test classes have letter in start of their names because this way we can control the order of testing.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CTabLayoutStressTest extends EspressoTestBase {

    private static final int[] TEST_SCENARIO = {
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            CLICK_EMPTY_STATE_ASCII_ART,
            CLICK_EMPTY_STATE_TEXT,
            CLICK_ON_TRENDING_TAB,
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            CLICK_EMPTY_STATE_ASCII_ART,
            CLICK_EMPTY_STATE_TEXT,
            CLICK_ON_TRENDING_TAB,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_LEFT,
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            CLICK_EMPTY_STATE_ASCII_ART,
            CLICK_EMPTY_STATE_TEXT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_RIGHT,
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            CLICK_EMPTY_STATE_ASCII_ART,
            CLICK_EMPTY_STATE_TEXT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_RIGHT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_RIGHT};

    private static final int TAB_STRESS_TEST_CYCLES = 5; // lots of stress

    @Rule
    public ActivityTestRule<MainActivity> startActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        clearLog();
    }

    @Test
    public void tabStressTest() {
        for (int i = 0; i < TAB_STRESS_TEST_CYCLES; i++) {
            runTestWithNoDelay(TEST_SCENARIO);
        }

        // We are not asserting nothing, we just hope it doesn't crash :D
    }
}