import { Component } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AddVideoModalComponent } from '../add-video-modal/add-video-modal.component';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    AddVideoModalComponent
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(private dialog: MatDialog, private router:Router) {}

  onLogoClick() {
    this.router.navigate(['/categories']);
  }

  addVideo() {
    const username = prompt('Enter username:');
    const password = prompt('Enter password:');
  
    if (username === environment.adminUsername && password === environment.adminPassword) {
      this.openVideoModal();
    } else {
      alert('Invalid credentials!');
    }
  }  

  openVideoModal() {
    const dialogRef = this.dialog.open(AddVideoModalComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Video details:', result);
      }
    });
  }
}