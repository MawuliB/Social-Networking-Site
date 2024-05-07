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
import { UserService } from '../../services/user.service';
import { environment } from '../../../environments/environment';

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
  @ViewChild(FindContactsComponent) findContactsComponent!: FindContactsComponent;

  user = JSON.parse(localStorage.getItem('user') || '{}');
  token = localStorage.getItem('token') || '';

  contacts: any[] = [];
  errors: any;
  PRODUCTION = environment.production;

  constructor(private userService: UserService) {}

  activeComponent = 'find-contacts';

  setActiveComponent(component: string) {
    this.activeComponent = component;
    localStorage.setItem('activeComponent', component);
  }

  ngOnInit(): void {
    const storedComponent = localStorage.getItem('activeComponent');
    this.userService.getUserByToken(this.token).subscribe({
      next: (user: any) => {
        if(!this.PRODUCTION){
            let userProfileImageUrl = user.getUserByToken.profileImageUrl;
        userProfileImageUrl = userProfileImageUrl.replace(/\\/g, '/'); // remove for production
        // Strip the unnecessary part of the path
        const prefix = "C:/Users/MawuliBadassou/Desktop/Samples/JavaCodes/sns/../../../Samples/Angular/sns/src/";
        if (userProfileImageUrl.startsWith(prefix)) {
          userProfileImageUrl = "./" + userProfileImageUrl.substring(prefix.length);
        }
        this.user = { ...user.getUserByToken, profileImageUrl: userProfileImageUrl };
        localStorage.setItem('user', JSON.stringify(this.user));
      }else{
        this.user = { ...user.getUserByToken };
        localStorage.setItem('user', JSON.stringify(this.user));
      }
      },
      error: (error: any) => {
        console.error(error);
        this.errors = error;
      }
    });
    if (storedComponent) {
      this.activeComponent = storedComponent;
    }
  }

  refreshSettings() {
    // refresh page
    window.location.href = '/home';
    }


  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  }
}
