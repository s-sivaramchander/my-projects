import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { VideoService } from '../../services/video.service';
import { CategoryService } from '../../services/category.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select'; // Import MatSelectModule
import { MatOptionModule } from '@angular/material/core'; // Import MatOptionModule
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms'; // Required for reactive forms
import { MatAutocompleteModule } from '@angular/material/autocomplete';

@Component({
  selector: 'app-video-modal',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatIconModule,
    MatSelectModule,
    MatOptionModule,
    CommonModule,
    ReactiveFormsModule,
    MatAutocompleteModule
  ],
  templateUrl: './add-video-modal.component.html',
  styleUrls: ['./add-video-modal.component.css']
})

export class AddVideoModalComponent {

  categories: any[] = []
  filteredCategories: any[] =[]

  videoData = {
    category: '',
    link: '',
    videoId: ''
  };

  constructor(private dialog: MatDialog, private videoService: VideoService, private categoryService: CategoryService, private snackBar: MatSnackBar) {
    this.videoService.getCategories().subscribe((data) => {
      this.categories = data;
    });  
    this.filteredCategories = this.categories;
  }

  filterCategories(value: string) {
    const filterValue = value.toLowerCase();
    this.filteredCategories = this.categories.filter(category =>
      category.toLowerCase().includes(filterValue)
    );
  }

  onClose() {
    this.dialog.closeAll();
  }

  onSubmit() {
    if (this.videoData.category && this.videoData.link) {
      this.videoService.saveVideoByCategoryAndLink(this.videoData.category, this.videoData.link).subscribe({
        next: (response) => {
          this.snackBar.open(`Video Saved Successfully!\n\nDetails:-\nTitle: ${response?.title}\nCategory: ${response?.category}`, 'Close', {
            duration: 3000, // Duration in milliseconds
            panelClass: ['snack-bar-success'] // Optional: custom class for styling
          });
          this.dialog.closeAll();
        },
        error: (error) => {
          this.snackBar.open(`Error saving video. ${error?.error?.error}. Please try again.`, 'Close', {
            duration: 3000,
            panelClass: ['snack-bar-error']
          });
          console.error('Error saving video:', error);
        }
      })
      this.categoryService.refreshCategories(this.videoService); // Refresh categories after adding a video
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

