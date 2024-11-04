import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http'; // Import provideHttpClient
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';



export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient()
  ],
  // schemas: [CUSTOM_ELEMENTS_SCHEMA]
};



// import { ApplicationConfig } from '@angular/core';
// import { provideRouter } from '@angular/router';
// import { routes } from './app.routes';

// export const appConfig: ApplicationConfig = {
//   providers: [
//     provideRouter(routes)
//   ],
//   schemas: [CUSTOM_ELEMENTS_SCHEMA]
// };