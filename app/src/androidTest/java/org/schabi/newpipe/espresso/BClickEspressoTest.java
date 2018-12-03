package org.schabi.newpipe.espresso;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.schabi.newpipe.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test classes have letter in start of their names because this way we can control the order of testing.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BClickEspressoTest extends EspressoTestBase {

    private static final int[] TEST_SCENARIO = {
            CLICK_ON_SUBSCRIPTIONS_TAB,
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            CLICK_ON_TOOLBAR_SEARCH_ACTION,
            TYPE_SMARTLOOK_INTO_SEARCH_INPUT,
            CLICK_IME_BUTTON_ON_SEARCH_INPUT,
            CLICK_ON_NAVIGATE_UP,
            CLICK_ON_TRENDING_TAB,
            CLICK_ON_BOOKMARKED_PLAYLIST_TAB,
            OPEN_HAMBURGER_MENU,
            CLICK_HAMBURGER_MENU_SETTINGS,
            CLICK_SETTINGS_DEBUG,
            CLICK_ON_NAVIGATE_UP,
            CLICK_ON_NAVIGATE_UP,
            CLICK_ON_TRENDING_TAB,
            CLICK_ON_TRENDING_VIDEO,
            PRESS_BACK,
            CLICK_ON_TRENDING_VIDEO,
            PRESS_BACK,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_LEFT,
            SWIPE_PAGER_RIGHT,
            CLICK_ON_TRENDING_TAB,
            LONG_CLICK_ON_TRENDING_VIDEO,
            CLICK_ADD_TO_PLAYLIST,
            CLICK_NEW_PLAYLIST_CANCEL};

    private int testClickCount;
    private List<String> testClickedViewsClassNames;

    @Rule
    public ActivityTestRule<MainActivity> startActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        clearLog();
    }

    @Test
    public void clickTest() {

        testClickCount = 0;
        testClickedViewsClassNames = new ArrayList<>();

        runTest(TEST_SCENARIO, viewClassName -> {
            if (viewClassName != null) {
                testClickCount++;
                testClickedViewsClassNames.add(viewClassName);
            }
        });

        assertClickEvents(parseOutClickedViewsClassNames());
    }

    private List<String> parseOutClickedViewsClassNames() {
        List<String> clickedViewsClassNames = new ArrayList<>();

        goThroughLog(line -> {
            if (line.contains("Selector created")) {
                clickedViewsClassNames.add(parseOutViewClassName(line));
            }
        });

        return clickedViewsClassNames;
    }

    private String parseOutViewClassName(String line) {

        int startIndex = line.indexOf("view=[") + "view=[".length();
        return line.substring(startIndex, line.indexOf("]", startIndex));
    }

    private void assertClickEvents(List<String> logClickedViewsClassNames) {

        System.out.println(String.format("Assert click count test=[%d] log=[%d]",
                testClickCount,
                logClickedViewsClassNames.size()));

        // We have detected all clicks
        assertEquals(testClickCount, logClickedViewsClassNames.size());

        for (int i = 0; i < logClickedViewsClassNames.size(); i++) {
            System.out.println(String.format("Assert click view test=[%s] log=[%s]",
                    testClickedViewsClassNames.get(i),
                    logClickedViewsClassNames.get(i)));

            assertEquals(testClickedViewsClassNames.get(i), logClickedViewsClassNames.get(i));
        }
    }
}