import { Component, OnInit, ViewChild } from '@angular/core';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { ToastComponent } from '../toast/toast.component';
import { SharedModule } from '../../services/shared.module';
import { ContactService } from '../../services/contact.service';
import { CommonModule } from '@angular/common';
import { User } from '../../interface/user';

@Component({
  selector: 'app-find-contacts',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './find-contacts.component.html',
  styleUrl: './find-contacts.component.css'
})
export class FindContactsComponent implements OnInit {

  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
      @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

      user = JSON.parse(localStorage.getItem('user') || '{}');
      contacts: any[] = [];
      errors: any;
      
      constructor(private contactService: ContactService) { }

      ngOnInit(): void {
        this.contactService.getAllContacts().subscribe({
          next: (contacts: User[]) => {
            this.contacts = contacts;
          },
          error: (error: any) => {
            console.error(error);
            this.errors = error;
          }
        });
      }
}
