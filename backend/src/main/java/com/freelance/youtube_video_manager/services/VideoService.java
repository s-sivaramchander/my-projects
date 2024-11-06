package com.freelance.youtube_video_manager.services;

import com.freelance.youtube_video_manager.exceptions.VideoAlreadyExistsException;
import com.freelance.youtube_video_manager.models.db.Video;
import com.freelance.youtube_video_manager.models.request.AddVideo;
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
        return videoRepository.findFirstByVideoIdAndCategory(videoId, category);
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
//
//    public Video addVideo(String youtubeUrl, String category) throws Exception {
//        String videoId = extractVideoId(youtubeUrl);
//
//        // Check if the video already exists in the specified category
//        Optional<Video> existingVideo = videoRepository.findFirstByVideoIdAndCategory(videoId, category);
//        if (existingVideo.isPresent()) {
//            throw new VideoAlreadyExistsException("Video already exists in the specified category");
//        }
//
//        // Proceed with adding the video if it does not exist
//        YouTube.Videos.List request = youtube.videos()
//                .list("snippet,contentDetails")
//                .setKey(apiKey)
//                .setId(videoId);
//
//        VideoListResponse response = request.execute();
//
//        if (response.getItems().isEmpty()) {
//            throw new RuntimeException("Video not found");
//        }
//
//        com.google.api.services.youtube.model.Video youtubeVideo = response.getItems().get(0);
//
//        Video video = new Video();
//        video.setCategory(category);
//        video.setVideoId(videoId);
//        video.setThumbnailUrl(youtubeVideo.getSnippet().getThumbnails().getDefault().getUrl());
//        video.setThumbnailUrl("https://img.youtube.com/vi/" + videoId + "/0.jpg");
//        video.setTitle(youtubeVideo.getSnippet().getTitle());
//        video.setDuration(youtubeVideo.getContentDetails() != null ? parseDuration(youtubeVideo.getContentDetails().getDuration()) : null);
//        video.setDescription(youtubeVideo.getSnippet().getDescription());
//
//        return videoRepository.save(video);
//    }

    public Video addVideo(String youtubeUrl, String category) throws Exception {
        String videoId = extractVideoId(youtubeUrl);

        // Check if the video already exists in the specified category
        Optional<Video> existingVideo = videoRepository.findFirstByVideoIdAndCategory(videoId, category);
        if (existingVideo.isPresent()) {
            throw new VideoAlreadyExistsException("Video already exists in the specified category");
        }

        // Fetch video details from YouTube
        Video video = fetchVideoDetailsByVideoId(videoId);
        video.setCategory(category); // Set category for the new video

        return videoRepository.save(video);
    }

    private Video fetchVideoDetailsByVideoId(String videoId) throws Exception {
        YouTube.Videos.List request = youtube.videos()
                .list("snippet,contentDetails")
                .setKey(apiKey)
                .setId(videoId);

        VideoListResponse response = request.execute();

        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        com.google.api.services.youtube.model.Video youtubeVideo = response.getItems().get(0);

        // Populate Video object with details from YouTube API response
        Video video = new Video();
        video.setVideoId(videoId);
        video.setThumbnailUrl("https://img.youtube.com/vi/" + videoId + "/0.jpg");
        video.setTitle(youtubeVideo.getSnippet().getTitle());
        video.setDuration(youtubeVideo.getContentDetails() != null ? parseDuration(youtubeVideo.getContentDetails().getDuration()) : null);
        video.setDescription(youtubeVideo.getSnippet().getDescription());

        return video;
    }

    public Video updateVideo(Long id, AddVideo request) throws Exception {
        // Retrieve the video by ID
        Optional<Video> existingVideoOpt = videoRepository.findFirstById(id);

        if (existingVideoOpt.isEmpty()) {
            throw new RuntimeException("Video not found for the given ID");
        }

        // Get the existing video record
        Video existingVideo = existingVideoOpt.get();

        // Fetch updated details from YouTube using the new video ID
        String videoId = extractVideoId(request.getUrl());
        Video updatedVideoDetails = fetchVideoDetailsByVideoId(videoId);

        // Update fields with details from the YouTube API response
        existingVideo.setCategory(request.getCategory());
        existingVideo.setTitle(updatedVideoDetails.getTitle());
        existingVideo.setDescription(updatedVideoDetails.getDescription());
        existingVideo.setThumbnailUrl(updatedVideoDetails.getThumbnailUrl());
        existingVideo.setDuration(updatedVideoDetails.getDuration());
        existingVideo.setVideoId(videoId);  // Update the video ID if it's changing

        // Save the updated video record
        return videoRepository.save(existingVideo);
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

    public static String parseDuration(String isoDuration) {
        if (isoDuration == null || isoDuration.isEmpty()) {
            return "0:00";  // Default value for null or empty input
        }

        // Regular expression to extract hours, minutes, and seconds
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(isoDuration);

        int hours = 0, minutes = 0, seconds = 0;

        if (matcher.matches()) {
            if (matcher.group(1) != null) hours = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null) minutes = Integer.parseInt(matcher.group(2));
            if (matcher.group(3) != null) seconds = Integer.parseInt(matcher.group(3));
        }

        // Build the formatted string based on non-zero values
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public void deleteVideoById(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found");
        }
        videoRepository.deleteById(id);
    }
}
