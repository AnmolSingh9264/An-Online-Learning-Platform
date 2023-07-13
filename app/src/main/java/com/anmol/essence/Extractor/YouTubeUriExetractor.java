package com.anmol.essence.Extractor;;

import android.content.Context;
import android.util.SparseArray;

@Deprecated
abstract class YouTubeUriExtractor extends YouTubeExtractor {

    public YouTubeUriExtractor(Context con) {
        super(con);
    }

    @Override
    protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
        onUrisAvailable(videoMeta.getVideoId(), videoMeta.getTitle(), ytFiles);
    }

    public abstract void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles);
}
