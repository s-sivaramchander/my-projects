import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet, RouterLink, NavigationEnd } from '@angular/router';
import { VideoService } from '../../services/video.service';
import { CommonModule } from '@angular/common';
import { VideoPlayerComponent } from '../video-player/video-player.component';
import { filter } from 'rxjs/operators';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-video-list',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, VideoPlayerComponent, MatFormFieldModule, MatIconModule, MatInputModule, FormsModule],
  templateUrl: './video-list.component.html',
  styleUrls: ['./video-list.component.css'],
})
export class VideoListComponent implements OnInit {
  categoryName: string = '';
  videos: any[] = [];
  searchTerm: string = '';
  isLoading: boolean = true;
  isVideoSelected: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private videoService: VideoService
  ) {}
  
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.categoryName = params['categoryName'];
      this.loadVideos(); // Load videos initially
    });

    // Subscribe to videoAddedSubject to refresh videos when a new video is added
    this.videoService.getVideoAddedObservable().subscribe(() => {
      this.loadVideos(); // Refresh videos
    });

    // Check the current URL to set isVideoSelected initially
    this.isVideoSelected = this.router.url.includes('/video/');

    // Subscribe to router events to check the current URL for "/video/"
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isVideoSelected = event.url.includes('/video/');
      });
  }

  loadVideos() {
    this.videoService.getVideosByCategory(this.categoryName).subscribe((data) => {
      this.videos = data;
      this.isLoading = false; // Set loading to false when data is fetched
    }, () => {
      this.isLoading = false; // Ensure loading is false even on error
    });
  }

  filteredVideos() {
    return this.videos.filter(video =>
      video.title.toLowerCase().includes(this.searchTerm.toLowerCase())
    ).sort();
  }

  onSelectVideo(video: any) {
    this.router.navigate(['video', video.videoId], { relativeTo: this.route });
  }
}

