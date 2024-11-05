import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { VideoService } from '../../services/video.service';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { filter } from 'rxjs/operators';
import { CategoryService } from '../../services/category.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatFormFieldModule, MatInputModule, FormsModule],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  categories: any[] = [];
  searchTerm: string = '';
  isLoading: boolean = true;
  isVideoSelected: boolean = false;

  constructor(
    private categoryService: CategoryService,
    private videoService: VideoService,
    private router: Router
  ) {
  }

  ngOnInit() {
    // Track video selection based on the URL
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.isVideoSelected = event.url.includes('/video/');
    });

    // Fetch initial categories
    this.loadCategories();

    // Listen for category updates in CategoryService
    this.categoryService.categories$.subscribe((data) => {
      this.categories = data;
    });

    // Listen for video additions and refresh categories when a new video is added
    this.videoService.getVideoAddedObservable().subscribe(() => {
      this.loadCategories();
    });
  }

  loadCategories() {
    this.isLoading = true;
    this.categoryService.refreshCategories(this.videoService);
    this.isLoading = false;
  }

  filteredCategories() {
    return this.categories.filter(category =>
      category.toLowerCase().includes(this.searchTerm.toLowerCase())
    ).sort();
  }

  onSelectCategory(category: any) {
    this.router.navigate(['/categories', category]);
  }
}