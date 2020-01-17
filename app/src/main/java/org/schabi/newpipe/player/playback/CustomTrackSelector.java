package org.schabi.newpipe.player.playback;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;

/**
 * This class allows irregular text language labels for use when selecting text captions and
 * is mostly a copy-paste from {@link DefaultTrackSelector}.
 *
 * This is a hack and should be removed once ExoPlayer fixes language normalization to accept
 * a broader set of languages.
 * */
public class CustomTrackSelector extends DefaultTrackSelector {

    private String preferredTextLanguage;

    public CustomTrackSelector(TrackSelection.Factory adaptiveTrackSelectionFactory) {
        super(adaptiveTrackSelectionFactory);
    }

    public String getPreferredTextLanguage() {
        return preferredTextLanguage;
    }

    public void setPreferredTextLanguage(@NonNull final String label) {
        Assertions.checkNotNull(label);
        if (!label.equals(preferredTextLanguage)) {
            preferredTextLanguage = label;
            invalidate();
        }
    }

    private static boolean formatHasLanguage(Format format, String language) {
        return language != null && TextUtils.equals(language, format.language);
    }

    @Override
    @Nullable
    protected Pair<TrackSelection.Definition, TextTrackScore> selectTextTrack(
            TrackGroupArray groups,
            int[][] formatSupport,
            Parameters params,
            @Nullable String selectedAudioLanguage)
            throws ExoPlaybackException {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = C.INDEX_UNSET;
        int newPipeTrackScore = 0;
        TextTrackScore selectedTrackScore = null;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex],
                        params.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup.getFormat(trackIndex);
                    TextTrackScore trackScore =
                            new TextTrackScore(
                                    format, params, trackFormatSupport[trackIndex], selectedAudioLanguage);
                    if (formatHasLanguage(format, preferredTextLanguage)) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                        // found user selected match (perfect!)
                        break;
                    } else if (trackScore.isWithinConstraints
                            && (selectedTrackScore == null || trackScore.compareTo(selectedTrackScore) > 0)) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        return selectedGroup == null
                ? null
                : Pair.create(
                new TrackSelection.Definition(selectedGroup, selectedTrackIndex),
                Assertions.checkNotNull(selectedTrackScore));
    }
}
