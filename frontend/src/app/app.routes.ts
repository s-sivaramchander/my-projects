import { Routes } from '@angular/router';
import { CategoryListComponent } from './components/category-list/category-list.component';
import { VideoListComponent } from './components/video-list/video-list.component';
import { VideoPlayerComponent } from './components/video-player/video-player.component';

export const routes: Routes = [
  { path: 'categories', component: CategoryListComponent },
  { 
    path: 'categories/:categoryName', 
    component: VideoListComponent,
    children: [
      { path: 'video/:videoId', component: VideoPlayerComponent }
    ]
  },
  { path: '**', redirectTo: 'categories' },
];