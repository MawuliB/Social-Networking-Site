import { Component, ViewChild } from '@angular/core';
import { share } from 'rxjs';
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
  token = localStorage.getItem('token') || '';

  errors: any;
  API_URL = environment.API_URL;
  PRODUCTION = environment.production;

  openEditAvatarModal() {
    throw new Error('Method not implemented.');
  }
  userForm: any;

  constructor(
    private http: HttpClient,
    private userService: UserService,
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
      username: this.user.username,
    });
  }

  editProfileForm = this.fb.group({
    firstname: [this.user.firstname, Validators.required],
    lastname: [this.user.lastname],
    username: [this.user.username, Validators.required],
  });

  // change User password
  changePasswordForm = this.fb.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, this.passwordValidator]],
    confirmPassword: ['', Validators.required],
  });

  onSubmit() {
    this.user = JSON.parse(localStorage.getItem('user') || '{}');

    if (this.editProfileForm.valid) {
      const { firstname, lastname, username } = this.editProfileForm.value;
      if (firstname && username) {
        if (this.user && this.user.id) {
          this.userService
            .updateUser(this.user.id, { firstname, lastname, username })
            .subscribe({
              next: (userData: any) => {
                console.log(userData.updateUser);
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
          console.error('User not found');
        }
      } else {
        this.toastComponent.openToast('Passwords do not match');
        console.error('Passwords do not match');
      }
    } else {
      this.toastComponent.openToast('Form is invalid');
      console.error('Form is invalid');
    }
  }

  selectedFile: File | null = null;

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
            this.toastGComponent.openToast(
              'Profile picture uploaded successfully'
            );
          },
          error: (error: any) => {
            console.error('Error uploading file', error);
          },
        });
    } else {
      console.error('No file selected');
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
}
