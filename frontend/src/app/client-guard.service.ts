import { Injectable } from '@angular/core';
import { AuthappService } from './services/authapp.service';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ClientGuardService implements CanActivate{

  constructor(private authService: AuthappService, private router: Router) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedAdmin()) {
      return true;
    } else {
      this.router.navigate(['/error'], {
        state: { status: 403 }  // HTTP status code 403 Forbidden
      });
      return false;
    }
  }
}
