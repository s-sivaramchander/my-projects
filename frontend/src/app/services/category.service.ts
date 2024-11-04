import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { VideoService } from './video.service';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private categoriesSubject = new BehaviorSubject<any[]>([]);
  categories$ = this.categoriesSubject.asObservable();

  setCategories(categories: any[]) {
    this.categoriesSubject.next(categories);
  }

  refreshCategories(videoService: VideoService) {
    videoService.getCategories().subscribe((data) => {
      this.setCategories(data);
    });
  }
}
