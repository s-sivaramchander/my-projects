import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VideoService, Video } from '../../services/video.service';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-video-player',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.css']
})
export class VideoPlayerComponent implements OnInit, OnDestroy, OnChanges {
  @Input() video: Video | null = null; // Accept video as an input
  safeVideoUrl: SafeResourceUrl | null = null;
  private subscription: Subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private videoService: VideoService,
    private sanitizer: DomSanitizer
  ) {}


  ngOnInit() {
    this.subscription.add(
      this.route.params.subscribe(params => {
        const videoId = params['videoId'];
        const categoryName = this.route.parent?.snapshot.params['categoryName'];
        
        if (categoryName && videoId) {
          this.subscription.add(
            this.videoService.getVideoByCategoryAndId(categoryName, videoId).subscribe({
              next: (data) => {
                this.video = data;
                if (this.video?.videoId) {
                  const embedUrl = this.videoService.getVideoEmbedUrl(this.video.videoId);
                  this.safeVideoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
                }
              },
              error: (error) => {
                console.error('Error loading video:', error);
                this.video = null;
                this.safeVideoUrl = null;
              }
            })
          );
        }
      })
    );
    this.updateVideoUrl(); // Initialize video on component load
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['video']) {
      this.updateVideoUrl(); // Update the embed URL if the video changes
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private updateVideoUrl() {
    if (this.video?.videoId) {
      const embedUrl = this.videoService.getVideoEmbedUrl(this.video.videoId);
      this.safeVideoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
    } else {
      this.safeVideoUrl = null; // Clear the embed URL to stop playback
    }
  }
}
