package org.schabi.newpipe.espresso;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.azimolabs.conditionwatcher.Instruction;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.schabi.newpipe.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test classes have letter in start of their names because this way we can control the order of testing.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DBlackVideoTest extends EspressoTestBase {

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

    // Regular expression user for parsing out detected black video parts
    private static final int BLACK_DETECTION_LINE_START = 1;
    private static final int BLACK_DETECTION_LINE_END = 3;
    private static final int BLACK_DETECTION_LINE_DURATION = 5;
    private static final String FLOAT_REGEX = "([+-]?(\\d+\\.)?\\d+)";
    private static final String BLACK_DETECTION_LINE_REGEX = String.format(
            "\\[blackdetect @ \\w+\\] black_start\\:%s black_end\\:%s black_duration\\:%s",
            FLOAT_REGEX,
            FLOAT_REGEX,
            FLOAT_REGEX);

    // Detect video intervals that are (almost) completely black.
    private static final String FFMPEG_BLACK_DETECTION = "-i %s -loglevel info -vf blackdetect=d=%.2f:pic_th=%.2f:pix_th=%.2f -an -f null -";

    // Minimum detected black duration expressed in seconds. It must be a non-negative floating point number.
    private static final float MIN_BLACK_DURATION = 1F;

    // Threshold for considering a picture "black". Express the minimum value for the ratio
    // black_pixels / all_pixels for which a picture is considered black.
    private static final float PICTURE_BLACK_RATIO_THRESHOLD = 0.95F;

    // Set the threshold for considering a pixel "black".
    // The threshold expresses the maximum pixel luminance value for which a pixel is considered "black".
    // The provided value is scaled according to the following equation:
    // absolute_threshold = luminance_minimum_value + pixel_black_th * luminance_range_size
    // luminance_range_size and luminance_minimum_value depend on the input video format,
    // the range is [0-255] for YUV full-range formats and [16-235] for YUV non full-range formats.
    private static final float PIXEL_BLACK_THRESHOLD = 0.1F; // default value

    private static FFmpeg ffmpeg;
    private CountingIdlingResource loadFFmpegIdling = new CountingIdlingResource("LoadFFmpeg");
    private VideoAssertionInstruction videoAssertionInstruction = new VideoAssertionInstruction();
    private List<BlackVideoSequence> blackVideoSequences = new ArrayList<>();

    @Rule
    public ActivityTestRule<MainActivity> startActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        clearLog();
    }

    @Before
    public void beforeTest() {

        Espresso.registerIdlingResources(loadFFmpegIdling);

        ffmpeg = FFmpeg.getInstance(startActivityRule.getActivity());
        loadFFmpegIdling.increment();
        tryToLoadFFmpeg(new LoadBinaryResponseHandler() {

            @Override
            public void onSuccess() {
                loadFFmpegIdling.decrement();
                System.out.println("FFmpeg loaded successfully!");
            }

            @Override
            public void onFailure() {
                loadFFmpegIdling.decrement();
                fail("Could not load FFmpeg wrapper!");
            }
        });
    }

    @Test
    public void blackVideoTest() throws Exception {
        runTest(TEST_SCENARIO);
        assertVideoData(findVideoFilePaths(parseOutVideoFolderPath()));
    }

    private List<String> parseOutVideoFolderPath() {

        final List<String> videoFolderPaths = new ArrayList<>();

        goThroughLog(line -> {
            if (line.contains("UploadApiCallsHandler") && line.contains("videoFolderPath")) {
                videoFolderPaths.add(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
            }
        });

        return videoFolderPaths;
    }

    private List<String> findVideoFilePaths(List<String> videoFolderPaths) {
        List<String> videoFilePaths = new ArrayList<>();

        for (String videoFolderPath : videoFolderPaths) {
            String videoFilePath = videoFolderPath + "session_record.mp4";

            File file = new File(videoFilePath);
            if (file.exists()) {
                videoFilePaths.add(videoFilePath);
            }
        }

        return videoFilePaths;
    }

    private void assertVideoData(List<String> videoFilePaths) throws Exception {
        for (String videoFilePath : videoFilePaths) {

            videoAssertionInstruction.setVideoAssertionInProgress(true);
            ffmpeg.execute(getFFmpegBlackDetectParameters(videoFilePath), new AbstractFFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    blackVideoSequences = parseOutBlackDetects(message);
                    videoAssertionInstruction.setVideoAssertionInProgress(false);
                }
            });

            ConditionWatcher.waitForCondition(videoAssertionInstruction);
            assertTrue("Black screens in video detected!", blackVideoSequences.isEmpty());
        }
    }

    private List<BlackVideoSequence> parseOutBlackDetects(String message) {
        List<BlackVideoSequence> blackVideoSequences = new ArrayList<>();

        Matcher matcher = Pattern.compile(BLACK_DETECTION_LINE_REGEX).matcher(message);
        while (matcher.find()) {
            blackVideoSequences.add(new BlackVideoSequence(
                    Float.valueOf(matcher.group(BLACK_DETECTION_LINE_START)),
                    Float.valueOf(matcher.group(BLACK_DETECTION_LINE_END)),
                    Float.valueOf(matcher.group(BLACK_DETECTION_LINE_DURATION))));
        }

        return blackVideoSequences;
    }

    private String[] getFFmpegBlackDetectParameters(String videoFilePath) {
        return String.format(
                FFMPEG_BLACK_DETECTION,
                videoFilePath,
                MIN_BLACK_DURATION,
                PICTURE_BLACK_RATIO_THRESHOLD,
                PIXEL_BLACK_THRESHOLD).split(" ");
    }

    private void tryToLoadFFmpeg(LoadBinaryResponseHandler loadingCallback) {
        try {
            ffmpeg.loadBinary(loadingCallback);
        } catch (FFmpegNotSupportedException e) {
            loadingCallback.onFailure();
        }
    }

    class BlackVideoSequence {
        float start;
        float end;
        float duration;

        BlackVideoSequence(float start, float end, float duration) {
            this.start = start;
            this.end = end;
            this.duration = duration;
        }
    }

    public class VideoAssertionInstruction extends Instruction {

        boolean videoAssertionInProgress;

        VideoAssertionInstruction() {
            this.videoAssertionInProgress = false;
        }

        @Override
        public String getDescription() {
            return "Asserting video has to finish";
        }

        @Override
        public boolean checkCondition() {
            return !videoAssertionInProgress;
        }

        void setVideoAssertionInProgress(boolean videoAssertionInProgress) {
            this.videoAssertionInProgress = videoAssertionInProgress;
        }
    }

    abstract class AbstractFFmpegExecuteResponseHandler implements FFmpegExecuteResponseHandler {

        @Override
        public void onSuccess(String message) {
        }

        @Override
        public void onProgress(String message) {
        }

        @Override
        public void onFailure(String message) {
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onFinish() {
        }
    }

}