package com.freelance.youtube_video_manager.exceptions;

public class VideoAlreadyExistsException extends RuntimeException {
    public VideoAlreadyExistsException(String message) {
        super(message);
    }
}
