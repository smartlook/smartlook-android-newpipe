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
import static org.junit.Assert.assertTrue;

/**
 * Test classes have letter in start of their names because this way we can control the order of testing.
 * RestEspressoTest needs to run first (detection of check).
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ARestEspressoTest extends EspressoTestBase {

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
            PRESS_BACK};

    private static final int REST_CHECK = 0;
    private static final int REST_INIT = 1;
    private static final int REST_EVENTS_WRITER = 2;
    private static final int REST_SDK_WRITER = 3;

    @Rule
    public ActivityTestRule<MainActivity> startActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Test A started------------------------------------------>");
        clearLog();
    }

    @Test
    public void restTest() {

        super.runTest(TEST_SCENARIO);
        assertRestEvents(parseOutRestEvents());
    }

    private List<Integer> parseOutRestEvents() {
        List<Integer> restEvents = new ArrayList<>();

        goThroughLog(line -> {
            if (line.contains("RestCommunication")) {
                Integer restEvent = parseOutRestEvent(line);

                if (restEvent != null) {
                    restEvents.add(restEvent);
                }
            }
        });

        return restEvents;
    }

    private Integer parseOutRestEvent(String line) {
        if (line.contains("[POST]") && line.contains("/rec/check/mobile/")) {
            return REST_CHECK;
        } else if (line.contains("[POST]") && line.contains("/rec/init/")) {
            return REST_INIT;
        } else if (line.contains("[POST]") && line.contains("events-writer.smartlook.com")) {
            return REST_EVENTS_WRITER;
        } else if (line.contains("[POST]") && line.contains("sdk-writer.smartlook.com")) {
            return REST_SDK_WRITER;
        } else {
            return null;
        }
    }

    /**
     * Here are the test assertions for rest events.
     * <p>
     * We are checking these rules:
     * 1) check() gets called first
     * 2) init() gets called before every sdkWriter()
     * 3) atleast one sdkWriter() call
     */
    private void assertRestEvents(List<Integer> restEvents) {

        boolean checkCalled = false;
        boolean initCalled = false;
        boolean sdkWriterCalled = false;
        int sdkWriterCalls = 0;

        // Start with check
        assertEquals(REST_CHECK, restEvents.get(0).intValue());

        for (Integer restEvent : restEvents) {

            if (restEvent == REST_CHECK) {
                checkCalled = true;
                continue;
            }

            // Check was called
            assertTrue("Check should be called first", checkCalled);

            if (restEvent == REST_INIT) {
                initCalled = true;
            } else if (restEvent == REST_SDK_WRITER) {
                sdkWriterCalled = true;
            }


            if (sdkWriterCalled) {
                assertTrue("Writer should be called after init", initCalled);

                sdkWriterCalls++;
                sdkWriterCalled = false;
                initCalled = false;
            }
        }

        // Check if writer was called
        assertTrue("Writer should be called:", sdkWriterCalls > 0);
    }

    private String restEventLogString(Integer restEvent) {
        switch (restEvent) {
            case REST_CHECK:
                return "CHECK";
            case REST_INIT:
                return "INIT";
            case REST_EVENTS_WRITER:
                return "EVENTS_WRITER";
            case REST_SDK_WRITER:
                return "SDK_WRITER";
        }

        return "Unknown rest event";
    }
}
