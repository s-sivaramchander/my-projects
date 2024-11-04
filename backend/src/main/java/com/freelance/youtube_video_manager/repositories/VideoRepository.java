package com.freelance.youtube_video_manager.repositories;

import com.freelance.youtube_video_manager.models.db.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query(value = "SELECT DISTINCT category FROM videos", nativeQuery = true)
    List<String> findAllDistinctCategories();
    List<Video> findByCategory(String category);
    Optional<Video> findByVideoIdAndCategory(String videoId, String category);
}