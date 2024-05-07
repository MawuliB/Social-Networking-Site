import { Component, OnInit, ViewChild } from '@angular/core';
import { SharedModule } from '../../services/shared.module';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { ContactService } from '../../services/contact.service';

@Component({
  selector: 'app-invites',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './invites.component.html',
  styleUrl: './invites.component.css',
})
export class InvitesComponent implements OnInit{
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  user = JSON.parse(localStorage.getItem('user') || '{}');
  invites: any[] = [];

  constructor(private contactService: ContactService) {}

  ngOnInit(): void {
    this.getContactsById();
    console.log(this.invites)
  }

  getContactsById() {
    this.contactService.getInvitations(this.user.id).subscribe({
      next: (invite: any[]) => {
        this.invites = invite;
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
        this.toastComponent.openToast(errorMessage);
      },
    });
  }

  removeContact(arg0: any) {
    throw new Error('Method not implemented.');
  }
  acceptContact(arg0: any) {
    throw new Error('Method not implemented.');
    }
}
