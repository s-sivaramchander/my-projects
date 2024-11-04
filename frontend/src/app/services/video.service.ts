import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface Video {
  id: number;
  videoId: string;
  title: string;
  duration: string;
  thumbnailUrl: string;
  category: string;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class VideoService {
    private apiUrl = environment.apiUrl;
    private videoAddedSubject = new Subject<void>(); // Subject to notify when a video is added

    constructor(private http: HttpClient) {}

    getCategories(): Observable<String[]> {
        return this.http.get<String[]>(`${this.apiUrl}/categories`);
    }

    getVideosByCategory(categoryName: string): Observable<Video[]> {
        return this.http.get<Video[]>(`${this.apiUrl}/videos/${categoryName}`);
    }

    getVideoByCategoryAndId(categoryName: string, videoId: string): Observable<Video> {
        return this.http.get<Video>(`${this.apiUrl}/videos/${categoryName}/${videoId}`).pipe(
        catchError((error) => {
            console.error(`Error fetching video with ID ${videoId}:`, error);
            throw 'Failed to fetch video; please try again later.';
        })
        );
    }

    getVideoEmbedUrl(videoId: string): string {
        return `https://www.youtube.com/embed/${videoId}?rel=0&modestbranding=1&showinfo=0`;
    }

    // Observable to be subscribed to
    getVideoAddedObservable(): Observable<void> {
        return this.videoAddedSubject.asObservable();
    }
    
    saveVideoByCategoryAndLink(category: string, url: string): Observable<any> {
        const requestBody = { url, category };
        return this.http.post<any>(`${this.apiUrl}/videos`, requestBody).pipe(
        tap(() => {
            this.videoAddedSubject.next(); // Emit the event when a video is added
        }),
        catchError((error) => {
            console.error('Error saving video:', error);
            throw 'Failed to save video; please try again later.';
        })
        );
    }
}