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
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { AddVideoModalComponent } from '../add-video-modal/add-video-modal.component';
import { UpdateVideoModalComponent } from '../update-video-modal/update-video-modal.component';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
  selector: 'app-video-list',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, VideoPlayerComponent, MatFormFieldModule, MatIconModule, MatInputModule, FormsModule, MatButtonModule, MatTooltipModule, MatMenuModule],
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
    private videoService: VideoService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
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

  deleteVideo(video: any): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Action',
        message: 'Are you sure you want to delete this video?',
      },
    });
  
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('User confirmed the action');
        this.videoService.deleteVideoById(video.id).subscribe({
          next: () => {
            this.snackBar.open(`Video Deleted Successfully!\n\nTitle: ${video.title}`, 'Close', {
              duration: 3000,
              panelClass: ['snack-bar-success']
            });
            // Add any additional logic here, such as refreshing the list of videos
          },
          error: (error) => {
            this.snackBar.open('Error deleting video. Please try again.', 'Close', {
              duration: 3000,
              panelClass: ['snack-bar-error']
            });
            console.error('Error deleting video:', error);
          },
        });
      } else {
        console.log('User cancelled the action');
        // Add any cancellation logic here if needed
      }
    });
  }
  
  

  editVideo(video: any) {
    console.log('Edit video clicked');
    this.openUpdateVideoModal(video)
    
  }

  openUpdateVideoModal(videoData: any) {
    const dialogRef = this.dialog.open(UpdateVideoModalComponent, {
      width: '400px',
      data: videoData 
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Video updated:', result);
      }
    });
  }

}

