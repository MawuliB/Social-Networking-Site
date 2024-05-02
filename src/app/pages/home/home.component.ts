import { Component, ViewChild } from '@angular/core';
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
export class HomeComponent {
      @ViewChild(ToastComponent) toastComponent!: ToastComponent;
      @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

}
