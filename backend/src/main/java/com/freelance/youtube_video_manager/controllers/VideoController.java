//////package controllers;
//////
//////import models.Video;
//////import org.springframework.web.bind.annotation.*;
//////import services.VideoService;
//////
//////import java.util.List;
//////
//////@RestController
//////@RequestMapping("/api/videos")
////////@CrossOrigin(origins = "http://localhost:4200")
//////public class VideoController {
//////    private final VideoService videoService;
//////
//////    public VideoController(VideoService videoService) {
//////        this.videoService = videoService;
//////    }
//////
//////    @PostMapping
//////    public Video addVideo(@RequestParam String url, @RequestParam String category) throws Exception {
//////        return videoService.addVideo(url, category);
//////    }
//////
//////    @GetMapping
//////    public List<Video> getAllVideos() {
//////        return videoService.getAllVideos();
//////    }
//////
//////    @GetMapping("/category/{category}")
//////    public List<Video> getVideosByCategory(@PathVariable String category) {
//////        return videoService.getVideosByCategory(category);
//////    }
//////}
////
////package com.freelance.youtube_video_manager.controllers;
////
////import com.freelance.youtube_video_manager.models.db.Video;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////import com.freelance.youtube_video_manager.services.VideoService;
////
////import java.util.List;
////
////@RestController
////@RequestMapping("/api/videos")
////public class VideoController {
////    private final VideoService videoService;
////
////    public VideoController(VideoService videoService) {
////        this.videoService = videoService;
////    }
////
////    @PostMapping
////    public Video addVideo(@RequestParam String url, @RequestParam String category) throws Exception {
////        System.out.println("URL: " + url);
////        return videoService.addVideo(url, category);
////    }
////
//////    @PostMapping
//////    public ResponseEntity<?> addVideo(@RequestParam String url, @RequestParam String category) throws Exception {
//////        if (url == null || url.isEmpty()) {
//////            return ResponseEntity.badRequest().body("The 'url' parameter is required.");
//////        }
//////        if (category == null || category.isEmpty()) {
//////            return ResponseEntity.badRequest().body("The 'category' parameter is required.");
//////        }
//////
//////        Video video = videoService.addVideo(url, category);
//////        return ResponseEntity.ok(video);
//////    }
////
////    @GetMapping
////    public List<Video> getAllVideos() {
////        return videoService.getAllVideos();
////    }
////
////    @GetMapping("/category/{category}")
////    public List<Video> getVideosByCategory(@PathVariable String category) {
////        return videoService.getVideosByCategory(category);
////    }
////
////    @GetMapping("/test")
////    public String test() {
////        return "Controller is working!";
////    }
////}
//
//
//package com.freelance.youtube_video_manager.controllers;
//
//import com.freelance.youtube_video_manager.models.db.Video;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import com.freelance.youtube_video_manager.services.VideoService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/videos")
//public class VideoController {
//    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
//    private final VideoService videoService;
//
//    public VideoController(VideoService videoService) {
//        this.videoService = videoService;
//    }
//
//    @PostMapping
//    public ResponseEntity<?> addVideo(@RequestBody(required = true) String url,
//                                      @RequestBody(required = true) String category) {
//        try {
//            logger.info("Received request to add video - URL: {}, Category: {}", url, category);
//
//            if (url == null || url.trim().isEmpty()) {
//                logger.warn("Invalid request: URL is empty or null");
//                return ResponseEntity.badRequest().body(Map.of("error", "URL parameter is required"));
//            }
//
//            if (category == null || category.trim().isEmpty()) {
//                logger.warn("Invalid request: Category is empty or null");
//                return ResponseEntity.badRequest().body(Map.of("error", "Category parameter is required"));
//            }
//
//            Video video = videoService.addVideo(url, category);
//            logger.info("Successfully added video with ID: {}", video.getId());
//            return ResponseEntity.ok(video);
//
//        } catch (Exception e) {
//            logger.error("Error while adding video", e);
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Video>> getAllVideos() {
//        try {
//            List<Video> videos = videoService.getAllVideos();
//            logger.info("Retrieved {} videos", videos.size());
//            return ResponseEntity.ok(videos);
//        } catch (Exception e) {
//            logger.error("Error while retrieving videos", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<?> getVideosByCategory(@PathVariable String category) {
//        try {
//            if (category == null || category.trim().isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "Category parameter is required"));
//            }
//
//            List<Video> videos = videoService.getVideosByCategory(category);
//            logger.info("Retrieved {} videos for category: {}", videos.size(), category);
//            return ResponseEntity.ok(videos);
//        } catch (Exception e) {
//            logger.error("Error while retrieving videos for category: " + category, e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @GetMapping("/test")
//    public ResponseEntity<String> test() {
//        logger.info("Test endpoint called");
//        return ResponseEntity.ok("Controller is working!");
//    }
//}


package com.freelance.youtube_video_manager.controllers;

import com.freelance.youtube_video_manager.exceptions.VideoAlreadyExistsException;
import com.freelance.youtube_video_manager.models.db.Video;
import com.freelance.youtube_video_manager.models.request.AddVideo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.freelance.youtube_video_manager.services.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin(value = "http://localhost:4200")
public class VideoController {
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

//    @PostMapping("/videos")
//    public ResponseEntity<?> addVideo(@RequestBody AddVideo request) {
//        try {
//            logger.info("Received request to add video - URL: {}, Category: {}",
//                    request.getUrl(), request.getCategory());
//
//            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
//                logger.warn("Invalid request: URL is empty or null");
//                return ResponseEntity.badRequest().body(Map.of("error", "URL is required"));
//            }
//
//            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
//                logger.warn("Invalid request: Category is empty or null");
//                return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
//            }
//
//            Video video = videoService.addVideo(request.getUrl(), request.getCategory());
//            logger.info("Successfully added video with ID: {}", video.getId());
//            return ResponseEntity.ok(video);
//
//        } catch (Exception e) {
//            logger.error("Error while adding video", e);
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
//        }
//    }

    @PostMapping("/videos")
    public ResponseEntity<?> addVideo(@RequestBody AddVideo request) {
        try {
            logger.info("Received request to add video - URL: {}, Category: {}",
                    request.getUrl(), request.getCategory());

            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                logger.warn("Invalid request: URL is empty or null");
                return ResponseEntity.badRequest().body(Map.of("error", "URL is required"));
            }

            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
                logger.warn("Invalid request: Category is empty or null");
                return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
            }

            Video video = videoService.addVideo(request.getUrl(), request.getCategory());
            logger.info("Successfully added video with ID: {}", video.getId());
            return ResponseEntity.ok(video);

        } catch (VideoAlreadyExistsException e) {
            logger.warn("Attempted to add duplicate video - URL: {}, Category: {}", request.getUrl(), request.getCategory());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error while adding video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @PutMapping("/videos/{id}")
    public ResponseEntity<Video> updateVideo(
            @PathVariable Long id,
            @RequestBody AddVideo request) {
        try {
            Video updated = videoService.updateVideo(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if video not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 for other errors
        }
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideoById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if video not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for other errors
        }
    }



    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getAllVideos() {
        try {
            List<Video> videos = videoService.getAllVideos();
            logger.info("Retrieved {} videos", videos.size());
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            logger.error("Error while retrieving videos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/videos/{category}")
    public ResponseEntity<?> getVideosByCategory(@PathVariable String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Category parameter is required"));
            }

            List<Video> videos = videoService.getVideosByCategory(category);
            logger.info("Retrieved {} videos for category: {}", videos.size(), category);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            logger.error("Error while retrieving videos for category: " + category, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("Controller is working!");
    }


    @GetMapping("/videos/{category}/{videoId}")
    public ResponseEntity<Video> getVideoById(@PathVariable String category, @PathVariable String videoId) {
        Optional<Video> video = videoService.getVideoByCategoryAndId(category, videoId);
        return video.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/embed/{videoId}")
    public ResponseEntity<String> getVideoEmbedUrl(@PathVariable String videoId) {
        String embedUrl = videoService.getVideoEmbedUrl(videoId);
        return ResponseEntity.ok(embedUrl);
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return videoService.getAllDistinctCategories();
    }
}