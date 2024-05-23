import { Component, OnInit, ViewChild } from '@angular/core';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { ToastComponent } from '../toast/toast.component';
import { SharedModule } from '../../services/shared.module';
import { ContactService } from '../../services/contact.service';
import { CommonModule } from '@angular/common';
import { User } from '../../interface/user';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-find-contacts',
  standalone: true,
  imports: [FormsModule, SharedModule, CommonModule],
  templateUrl: './find-contacts.component.html',
  styleUrl: './find-contacts.component.css',
})
export class FindContactsComponent implements OnInit {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  user = JSON.parse(localStorage.getItem('user') ?? '{}');
  contacts: any[] = [];
  errors: any;
  searchTerm = '';
  loading = false

  id = this.user.id;

  constructor(private contactService: ContactService) {}

  sendInvite(arg0: any) {
    if (this.user.id) {
      this.contactService.addToContact(this.user.id, arg0).subscribe({
        next: (response: any) => {
          this.toastGComponent.openToast('Invite sent successfully!');
        },
        error: (error: any) => {
          const errorMessage = error.graphQLErrors[0].message;
          this.toastComponent.openToast(errorMessage);
        },
      });
    } else {
      this.toastComponent.openToast('Please login to send invite');
      window.location.href = '/login';
    }
  }

  ngOnInit(): void {
    this.loading = true;
    this.contactService.getAllContacts().subscribe({
      next: (contacts: User[]) => {
        this.contacts = contacts.filter((contact) => contact.id !== this.user.id);
        this.loading = false;
      },
      error: (error: any) => {
        console.error(error);
        this.errors = error;
        this.loading = false;
      },
    });
  }
}
