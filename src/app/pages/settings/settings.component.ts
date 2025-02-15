import { Component, ViewChild } from '@angular/core';
import { SharedModule } from '../../services/shared.module';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ContactService } from '../../services/contact.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule, CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
})
export class SettingsComponent {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  user = JSON.parse(localStorage.getItem('user') || '{}');
  token = localStorage.getItem('token') ?? '';

  blacklist: any[] = [];
  errors: any;
  API_URL = environment.API_URL;
  PRODUCTION = environment.production;

  selectedFile: File | null = null;

  constructor(
    private http: HttpClient,
    private userService: UserService,
    private contactService: ContactService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.user = JSON.parse(localStorage.getItem('user') || '{}');
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

    this.editProfileForm.setValue({
      firstname: this.user.firstname,
      lastname: this.user.lastname,
      alias: this.user.alias,
    });

    this.contactService.getAllContactByContactId(this.user.id).subscribe({
      next: (contacts: any[]) => {
        this.blacklist = contacts;
      },
      error: (error: any) => {
        console.error(error);
        this.errors = error;
      },
    });
  }

  removeFromBlacklist(arg0: any) {
    this.contactService.removeFromBlackList(arg0).subscribe({
      next: (response: any) => {
        this.toastGComponent.openToast('User removed from blacklist');
        this.blacklist = this.blacklist.filter((user) => user.id !== arg0);
      },
      error: (error: any) => {
        console.error(error);
        this.toastComponent.openToast('Error removing user from blacklist');
      },
    });
    }

  editProfileForm = this.fb.group({
    firstname: [this.user.firstname, Validators.required],
    lastname: [this.user.lastname],
    alias: [this.user.alias, Validators.required],
  });

  // change User password
  changePasswordForm = this.fb.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, this.passwordValidator]],
    confirmPassword: ['', Validators.required],
  });

  onSubmit() { // update user details
    this.user = JSON.parse(localStorage.getItem('user') || '{}');

    if (this.editProfileForm.valid) {
      const { firstname, lastname, alias } = this.editProfileForm.value;
      if (firstname && alias) {
        if (this.user && this.user.id) {
          this.userService
            .updateUser(this.user.id, { firstname, lastname, alias })
            .subscribe({
              next: (userData: any) => {
                localStorage.setItem(
                  'user',
                  JSON.stringify(userData.updateUser)
                );

                if (!this.PRODUCTION) {
                  this.user.profileImageUrl = this.user.profileImageUrl.replace(
                    /\\/g,
                    '/'
                  ); // remove for production
                  // Strip the unnecessary part of the path
                  const prefix =
                    'C:/Users/MawuliBadassou/Desktop/Samples/JavaCodes/sns/../../../Samples/Angular/sns/src/';
                  if (this.user.profileImageUrl.startsWith(prefix)) {
                    this.user.profileImageUrl =
                      './' + this.user.profileImageUrl.substring(prefix.length);
                  }
                }

                this.user = { ...this.user, ...userData.updateUser };
                this.toastGComponent.openToast('Profile updated');
              },
              error: (error: any) => {
                const errorMessage = error.graphQLErrors
                  .map((err: any) => err.message)
                  .join(', ');
                this.toastComponent.openToast(errorMessage);
              },
            });
        } else {
          this.toastComponent.openToast('User not found');
        }
      } else {
        this.toastComponent.openToast(
          'Firstname, lastname, and username are required'
        );
      }
    }
  }

  onChangePassword() {
    if (this.changePasswordForm.valid) {
      const { oldPassword, newPassword, confirmPassword } =
        this.changePasswordForm.value;
      if (newPassword === confirmPassword) {
        if (this.user && this.user.id) {
          this.userService
            .changePassword(this.user.id, oldPassword!, newPassword!)
            .subscribe({
              next: (response: any) => {
                this.toastGComponent.openToast('Password changed successfully');
              },
              error: (error: any) => {
                const errorMessage = error.graphQLErrors
                  .map((err: any) => err.message)
                  .join(', ');
                this.toastComponent.openToast(errorMessage);
                console.error(error);
              },
            });
        } else {
          this.toastComponent.openToast('User not found');
        }
      } else {
        this.toastComponent.openToast('Passwords do not match');
      }
    } else {
      this.toastComponent.openToast('Form is invalid');
    }
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files[0]) {
      this.selectedFile = event.target.files[0];

      // Create a FileReader to read the contents of the selected file
      const reader = new FileReader();

      // Set the onload event to update the img src with the file content
      reader.onload = (e: any) => {
        const preview = document.getElementById('preview') as HTMLImageElement;
        const upload = document.getElementById('upload') as HTMLImageElement;
        preview.src = e.target.result;
        preview.classList.remove('hidden');
        upload.classList.add('hidden');
      };

      // Read the contents of the file
      if (this.selectedFile) {
        reader.readAsDataURL(this.selectedFile);
      }
    }
  }

  onUploadProfilePic() {
    if (this.selectedFile) {
      // Create a FormData object to send the file
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      // Get the token from localStorage
      const token = localStorage.getItem('token');

      // Create headers with the authorization token
      const headers = { Authorization: `Bearer ${token}` };

      this.http
        .post(`${this.API_URL}/profile/upload/${this.user.id}`, formData, {
          headers,
          responseType: 'text',
        })
        .subscribe({
          next: (response: any) => {
            if (!this.PRODUCTION) {
              this.user.profileImageUrl = response.replace(/\\/g, '/'); // remove for production
              // Strip the unnecessary part of the path
              const prefix =
                'C:/Users/MawuliBadassou/Desktop/Samples/JavaCodes/sns/../../../Samples/Angular/sns/src/';
              if (this.user.profileImageUrl.startsWith(prefix)) {
                this.user.profileImageUrl =
                  './' + this.user.profileImageUrl.substring(prefix.length);
              }
            } else {
              this.user.profileImageUrl = response;
            }
            // display a success message
            this.toastGComponent.openToast(
              'Profile picture uploaded successfully'
            );
            const button = document.getElementById('close-modal');
            if (button) {
              button.click();
            }else{
              console.error('Modal toggle button not found');
            }

          // Reset the selectedFile
          this.selectedFile = null;

          },
          error: (error: any) => {
            console.error('Error uploading file', error);
          },
        });
    } else {
      this.toastComponent.openToast('No file selected');
    }
  }

  passwordValidator(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    const value = control.value;
    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /[0-9]/.test(value);
    const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/.test(value);
    const isLengthValid = value.length > 8;

    const passwordValid =
      hasUpperCase &&
      hasLowerCase &&
      hasNumeric &&
      hasSpecialChar &&
      isLengthValid;

    return !passwordValid ? { newPassword: true } : null;
  }

  get oldPassword() {
    return this.changePasswordForm.get('oldPassword');
  }

  get newPassword() {
    return this.changePasswordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.changePasswordForm.get('confirmPassword');
  }

  get firstname() {
    return this.editProfileForm.get('firstname');
  }

  get lastname() {
    return this.editProfileForm.get('lastname');
  }

  get alias() {
    return this.editProfileForm.get('alias');
  }
}
