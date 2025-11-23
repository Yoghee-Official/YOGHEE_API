package com.lagavulin.yoghee.model.enums;

public enum ImageRule {
    PROFILE(1080, 1080, 5 * 1024 * 1024),
    BANNER(2560, 720, 10 * 1024 * 1024),
    POST(4000, 4000, 15 * 1024 * 1024);

    public final int maxWidth;
    public final int maxHeight;
    public final long maxFileSize;

    ImageRule(int maxWidth, int maxHeight, long maxFileSize) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.maxFileSize = maxFileSize;
    }
}
