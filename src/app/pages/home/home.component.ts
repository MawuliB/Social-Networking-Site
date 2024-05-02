import { Component, OnInit, ViewChild } from '@angular/core';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { SharedModule } from '../../services/shared.module';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
      @ViewChild(ToastComponent) toastComponent!: ToastComponent;
      @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

      user = JSON.parse(localStorage.getItem('user') || '{}');
      
      constructor() { }

      ngOnInit(): void {
        console.log('User:', this.user);
      }

      logout(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }

}
