package com.freelance.youtube_video_manager.models.request;

public class AddVideo {
    // First, create a DTO (Data Transfer Object) for the request
    private String url;
    private String category;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
