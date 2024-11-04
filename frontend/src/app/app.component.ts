// import { Component } from '@angular/core';
// import { GreetingsComponent } from "./components/greetings/greetings.component";
// import { CategoryListComponent } from "./components/category-list/category-list.component";
// import { VideoService } from './services/video.service';
// import { VideoListComponent } from "./components/video-list/video-list.component";
// import { VideoPlayerComponent } from "./components/video-player/video-player.component";
// import { HeaderComponent } from './components/header/header.component';
// import { faCoffee, faHome } from '@fortawesome/free-solid-svg-icons';


// @Component({
//   selector: 'app-root',
//   standalone: true,
//   imports: [GreetingsComponent, CategoryListComponent, VideoListComponent, VideoPlayerComponent, HeaderComponent,
//     // AppRoutingModule
//   ],
//   template: `
//   <div class="container">
//     <app-header></app-header>
//     <app-category-list (selectCategory)="onCategorySelected($event)"></app-category-list>
//     <div class="main-content">
//       <!-- Your main content goes here -->
//       <app-video-player [selectedVideo]="selectedVideo"></app-video-player>
//       <app-video-list [selectedCategory]="selectedCategory" (selectVideo)="onVideoSelected($event)"></app-video-list>
//     </div>
//   </div>

//     <!-- <app-category-list (selectCategory)="onCategorySelected($event)"></app-category-list> -->
//   `,
//   styleUrls: ['./app.component.css']
// })
// export class AppComponent {
//   selectedCategory: any;
//   selectedVideo: any;

//   onCategorySelected(category: any) {
//     this.selectedCategory = category;
//     this.selectedVideo = null;
//   }

//   onVideoSelected(video: any) {
//     this.selectedVideo = video;
//   }
// }

// import { Component } from '@angular/core';
// import { GreetingsComponent } from "./components/greetings/greetings.component";
// import { CategoryListComponent } from "./components/category-list/category-list.component";
// import { VideoListComponent } from "./components/video-list/video-list.component";
// import { VideoPlayerComponent } from "./components/video-player/video-player.component";
// import { HeaderComponent } from './components/header/header.component';
// import { faCoffee, faHome } from '@fortawesome/free-solid-svg-icons';
// import { AppRoutingModule } from './app-routing.module';

// @Component({
//   selector: 'app-root',
//   // standalone: true,
//   // imports: [
//   //   GreetingsComponent,
//   //   CategoryListComponent,
//   //   VideoListComponent,
//   //   VideoPlayerComponent,
//   //   HeaderComponent,
//   //   AppRoutingModule
//   // ],
//   templateUrl: './app.component.html',
//   // template: `
//   //   <div class="container">
//   //     <app-header></app-header>
//   //     <app-category-list (selectCategory)="onCategorySelected($event)"></app-category-list>
//   //     <div class="main-content">
//   //       <app-video-player [selectedVideo]="selectedVideo"></app-video-player>
//   //       <app-video-list [selectedCategory]="selectedCategory" (selectVideo)="onVideoSelected($event)"></app-video-list>
//   //     </div>
//   //   </div>
//   // `,
//   styleUrls: ['./app.component.css']
// })
// export class AppComponent {
//   selectedCategory: any;
//   selectedVideo: any;

//   onCategorySelected(category: any) {
//     this.selectedCategory = category;
//     this.selectedVideo = null;
//   }

//   onVideoSelected(video: any) {
//     this.selectedVideo = video;
//   }
// }


// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-root',
//   templateUrl: './app.component.html',
//   styleUrls: ['./app.component.css']
// })
// export class AppComponent {
//   // AppComponent does not require any additional logic for routing to work.
// }


import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent
    // Add other components you use directly in app.component.html
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
}