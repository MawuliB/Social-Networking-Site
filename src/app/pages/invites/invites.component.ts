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
    this.getInvitationsById();
  }

  getInvitationsById() {
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
    this.contactService.removeFromContacts(arg0).subscribe({
      next: (response: any) => {
        this.toastGComponent.openToast('Contact removed successfully!');
        this.invites = this.invites.filter((contact) => contact.id !== arg0);
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
          this.toastComponent.openToast(errorMessage);
      },
    });
    }
  acceptContact(arg0: any) {
    this.contactService.acceptInvitation(arg0).subscribe({
      next: (response: any) => {
        this.toastGComponent.openToast('Invite accepted successfully!');
        this.invites = this.invites.filter((contact) => contact.id !== arg0);
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
          this.toastComponent.openToast(errorMessage);
      },
    });
    }
}
