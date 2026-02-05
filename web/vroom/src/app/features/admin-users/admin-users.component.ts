import { Component, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';
import { AdminUserService } from './admin-user.service';
import { AdminUser } from './admin-user.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
private refresh$ = new Subject<void>();
  users$!: Observable<AdminUser[]>;

  error: string | null = null;
  success: string | null = null;

  blockReason: { [key: number]: string } = {};

  constructor(private adminUserService: AdminUserService, private router: Router) {}

ngOnInit(): void {
  this.users$ = this.refresh$.pipe(
    startWith(void 0),              
    switchMap(() => this.adminUserService.getAllUsers())
  );
}

private triggerRefresh(): void {
  this.refresh$.next();
}

 block(user: AdminUser): void {
  const reason = this.blockReason[user.id];

  if (!reason || reason.trim().length === 0) {
    this.error = 'Block reason is required.';
    this.success = null;
    return;
  }

  this.adminUserService.blockUser(user.id, reason).subscribe({
    next: () => {
      this.success = 'User successfully blocked.';
      this.error = null;
      this.blockReason[user.id] = '';
      this.triggerRefresh(); 
    },
    error: () => {
      this.error = 'Failed to block user.';
      this.success = null;
    }
  });
}

unblock(user: AdminUser): void {
  this.adminUserService.unblockUser(user.id).subscribe({
    next: () => {
      this.success = 'User successfully unblocked.';
      this.error = null;
      this.triggerRefresh(); 
    },
    error: () => {
      this.error = 'Failed to unblock user.';
      this.success = null;
    }
  });
}

  redirectToUserHistory(user: AdminUser){
    this.router.navigate(['/ride-history'], { 
      queryParams: { email: user.email } 
    });
  }
}
