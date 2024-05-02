import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { ToastComponent } from '../toast/toast.component';
import { SharedModule } from '../../services/shared.module';

@Component({
  selector: 'app-activate-code',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './activate-code.component.html',
  styleUrls: ['./activate-code.component.css']
})
export class ActivateCodeComponent implements OnInit {
  closeToast() {
    throw new Error('Method not implemented.');
    }
      @ViewChild(ToastComponent) toastComponent!: ToastComponent;
      @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;
      
  apiUrl = environment.API_URL;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');

    if (code) {
      this.http.get(`${this.apiUrl}/auth/activate-account?token=${code}`).subscribe({
        next: (response: any) => {
          this.toastGComponent.openToast('Account activated successfully! Redirecting to login page...');
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 3000);
        },
        error: (error: any) => {
          console.error(error);
          this.toastComponent.openToast(error.error.error);
        }
      });
    }
  }
}