import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { VideoService } from '../../services/video.service';
import { CategoryService } from '../../services/category.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReactiveFormsModule } from '@angular/forms'; // Required for reactive forms
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-update-video-modal',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatIconModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    CommonModule
  ],
  templateUrl: './update-video-modal.component.html',
  styleUrls: ['./update-video-modal.component.css']
})
export class UpdateVideoModalComponent {

  categories: any[] = []
  filteredCategories: any[] = []

  videoData = {
    id: '',
    category: '',
    link: '',
    videoId: ''
  };

  constructor(
    private dialogRef: MatDialogRef<UpdateVideoModalComponent>,
    private videoService: VideoService,
    private categoryService: CategoryService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // Initialize videoData with data passed from the parent component
    if (data) {
      this.videoData = { ...data };
      this.videoData.link = `https://www.youtube.com/watch?v=${data.videoId}`;
    }
    this.videoService.getCategories().subscribe((data) => {
      this.categories = data;
    }); 
    this.filteredCategories = this.categories
    console.table(this.categories)
  }

  filterCategories(value: string) {
    const filterValue = value.toLowerCase();
    this.filteredCategories = this.categories.filter(category =>
      category.toLowerCase().includes(filterValue)
    );
  }

  onClose() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.videoData.category && this.videoData.link) {
      this.extractVideoId();
      this.videoService.updateVideoByIdCategoryAndLink(this.videoData.id, this.videoData.category, this.videoData.link).subscribe({
        next: (response) => {
          this.snackBar.open(`Video Saved Successfully!\n\nDetails:\nTitle: ${response?.title}\nCategory: ${response?.category}`, 'Close', {
            duration: 3000, 
            panelClass: ['snack-bar-success']
          });
          this.dialogRef.close(response);
        },
        error: (error) => {
          this.snackBar.open('Error saving video. Please try again.', 'Close', {
            duration: 3000,
            panelClass: ['snack-bar-error']
          });
          console.error('Error saving video:', error);
        }
      });
      this.categoryService.refreshCategories(this.videoService);
    } else {
      alert('Please provide both category and link.');
    }
  }

  extractVideoId(): void {
    const url = this.videoData.link;
    const regex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/(?:[^\/\n\s]+\/\S+\/|(?:v|e(?:mbed)?|.+\/.+\/|.*[?&]v=)|(?:[^\/\n\s]+\/)?(?:\S+)?\S+?)([a-zA-Z0-9_-]{11})|youtu\.be\/([a-zA-Z0-9_-]{11}))/;
    const matches = url.match(regex);

    if (matches) {
      this.videoData.videoId = matches[1] || matches[2];
    } else {
      this.videoData.videoId = 'Invalid YouTube URL';
    }
  }
}
