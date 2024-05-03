import { Component, OnInit, ViewChild } from '@angular/core';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { SharedModule } from '../../services/shared.module';
import { FindContactsComponent } from '../find-contacts/find-contacts.component';
import { MyContactsComponent } from '../my-contacts/my-contacts.component';
import { MessagesComponent } from '../messages/messages.component';
import { InvitesComponent } from '../invites/invites.component';
import { SettingsComponent } from '../settings/settings.component';
import { CommonModule } from '@angular/common';
import { Apollo } from 'apollo-angular';
import { GET_ALL_CONTACTS } from '../../services/graphql.operations';
import { ContactService } from '../../services/contact.service';
import { Contact } from '../../interface/contact';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    SharedModule,
    FindContactsComponent,
    MyContactsComponent,
    MessagesComponent,
    InvitesComponent,
    SettingsComponent,
    CommonModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  user = JSON.parse(localStorage.getItem('user') || '{}');

  contacts: any[] = [];
  errors: any;

  constructor(private apollo: Apollo, private contactService: ContactService) {}

  activeComponent = 'find-contacts';

  setActiveComponent(component: string) {
    this.activeComponent = component;
    localStorage.setItem('activeComponent', component);
  }

  ngOnInit(): void {
    const storedComponent = localStorage.getItem('activeComponent');
    if (storedComponent) {
      this.activeComponent = storedComponent;
    }
  }

  
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  }
}
