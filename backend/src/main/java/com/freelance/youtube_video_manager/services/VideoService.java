package com.freelance.youtube_video_manager.services;

import com.freelance.youtube_video_manager.models.db.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.freelance.youtube_video_manager.repositories.VideoRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VideoService {
    private final YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Autowired
    private final VideoRepository videoRepository;

    public Optional<Video> getVideoByCategoryAndId(String category, String videoId) {
        // You would need to adjust this logic to fetch video based on your data structure.
        return videoRepository.findByVideoIdAndCategory(videoId, category);
    }

    public String getVideoEmbedUrl(String videoId) {
        return "https://www.youtube.com/embed/" + videoId; // Replace with your actual embed URL structure
    }

    public List<String> getAllDistinctCategories() {
        return videoRepository.findAllDistinctCategories();
    }

    public VideoService(VideoRepository videoRepository) throws Exception {
        this.videoRepository = videoRepository;
        this.youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
        ).setApplicationName("youtube-catalog").build();
    }

    public Video addVideo(String youtubeUrl, String category) throws Exception {
        String videoId = extractVideoId(youtubeUrl);
        YouTube.Videos.List request = youtube.videos()
                .list("snippet")
                .setKey(apiKey)
                .setId(videoId);

        VideoListResponse response = request.execute();

        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        com.google.api.services.youtube.model.Video youtubeVideo = response.getItems().get(0);

        Video video = new Video();
        video.setCategory(category);
        video.setVideoId(videoId);
        video.setThumbnailUrl(youtubeVideo.getSnippet().getThumbnails().getDefault().getUrl());
        video.setThumbnailUrl("https://img.youtube.com/vi/" + videoId + "/0.jpg");
        video.setTitle(youtubeVideo.getSnippet().getTitle());
        video.setDuration(youtubeVideo.getContentDetails() != null ? youtubeVideo.getContentDetails().getDuration() : null);
        video.setDescription(youtubeVideo.getSnippet().getDescription());

        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getVideosByCategory(String category) {
        return videoRepository.findByCategory(category);
    }

    public String extractVideoId(String youtubeUrl) {
        String regex = "(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?|.+/.+/|.*[?&]v=)|(?:[^/\\n\\s]+/)?(?:\\S+)?\\S+?)([a-zA-Z0-9_-]{11})|youtu\\.be/([a-zA-Z0-9_-]{11}))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            // Return the first capturing group or the second if the first is null
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        return youtubeUrl;
    }
}


/*

package com.freelance.youtube_video_manager.services;

import com.freelance.youtube_video_manager.models.db.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.freelance.youtube_video_manager.repositories.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VideoService {
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
    private final VideoRepository videoRepository;
    private final YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey;

    public VideoService(VideoRepository videoRepository) throws Exception {
        this.videoRepository = videoRepository;
        this.youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
        ).setApplicationName("youtube-catalog").build();
    }

    public Video addVideo(String youtubeUrl, String category) throws Exception {
        String videoId = extractVideoId(youtubeUrl);
        logger.info("Extracted video ID: {} from URL: {}", videoId, youtubeUrl);

        // Request both snippet and contentDetails parts
        YouTube.Videos.List request = youtube.videos()
                .list("snippet,contentDetails")  // Added contentDetails here
                .setKey(apiKey)
                .setId(videoId);

        VideoListResponse response = request.execute();

        if (response.getItems() == null || response.getItems().isEmpty()) {
            logger.error("No video found for ID: {}", videoId);
            throw new RuntimeException("Video not found");
        }

        com.google.api.services.youtube.model.Video youtubeVideo = response.getItems().get(0);

        // Log the response for debugging
        if (youtubeVideo.getContentDetails() == null) {
            logger.warn("Content details are null for video ID: {}", videoId);
        } else {
            logger.info("Successfully retrieved content details for video ID: {}", videoId);
        }

        Video video = new Video();
        video.setCategory(category);
        video.setVideoId(videoId);

        // Safely get thumbnail URL
        if (youtubeVideo.getSnippet() != null &&
            youtubeVideo.getSnippet().getThumbnails() != null &&
            youtubeVideo.getSnippet().getThumbnails().getDefault() != null) {
            video.setThumbnailUrl(youtubeVideo.getSnippet().getThumbnails().getDefault().getUrl());
        }

        // Safely get title and description
        if (youtubeVideo.getSnippet() != null) {
            video.setTitle(youtubeVideo.getSnippet().getTitle());
            video.setDescription(youtubeVideo.getSnippet().getDescription());
        }

        // Safely get duration
        if (youtubeVideo.getContentDetails() != null) {
            video.setDuration(youtubeVideo.getContentDetails().getDuration());
        }

        // Log the video details before saving
        logger.info("Saving video with ID: {}, Title: {}, Duration: {}",
            videoId,
            video.getTitle(),
            video.getDuration());

        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getVideosByCategory(String category) {
        return videoRepository.findByCategory(category);
    }

    public String extractVideoId(String youtubeUrl) {
        String regex = "(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?|.+/.+/|.*[?&]v=)|(?:[^/\\n\\s]+/)?(?:\\S+)?\\S+?)([a-zA-Z0-9_-]{11})|youtu\\.be/([a-zA-Z0-9_-]{11}))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            String id = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid YouTube URL: Could not extract video ID");
            }
            return id;
        }

        if (youtubeUrl.matches("[a-zA-Z0-9_-]{11}")) {
            return youtubeUrl;
        }

        throw new IllegalArgumentException("Invalid YouTube URL or video ID format");
    }
}
 */