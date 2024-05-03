import { Component } from '@angular/core';
import { share } from 'rxjs';
import { SharedModule } from '../../services/shared.module';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {

  user = JSON.parse(localStorage.getItem('user') || '{}');

openEditAvatarModal() {
throw new Error('Method not implemented.');
}
openEditFieldModal(arg0: string) {
throw new Error('Method not implemented.');
}
userForm: any;

}
