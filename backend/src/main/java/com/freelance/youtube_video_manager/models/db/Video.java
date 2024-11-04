package com.freelance.youtube_video_manager.models.db;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String duration;
    private String videoId;
    private String thumbnailUrl;
    private String category;
    private String description;
}