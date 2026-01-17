import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideNgToast } from 'ng-angular-popup';

import { routes } from './app.routes';
import { PanicNotificationService } from './core/services/panic-notification.service';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([jwtInterceptor])
    ),
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideNgToast({
      duration: 5000,
      position: 'toaster-bottom-right',
      maxToasts: 3,
      width: 400,
      showProgress: true,
      dismissible: true,
      enableAnimations: true 
    }),
    provideRouter(routes)
    // uncomment next set of code, it is used to listen for panic notifications in bg, starts on load but only admin gets notifications
    /*,{
      provide: APP_INITIALIZER,
      useFactory: (pns: PanicNotificationService) => () => Promise.resolve(),
      deps: [PanicNotificationService],
      multi: true
    }*/
  ]
};
