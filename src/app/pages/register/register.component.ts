import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder,  ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { SharedModule } from '../../services/shared.module';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, SharedModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
      @ViewChild(ToastComponent) toastComponent!: ToastComponent;
      @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  apiUrl = environment.API_URL;
  loading = false

  constructor(private route: ActivatedRoute, private router: Router,private http: HttpClient, private fb: FormBuilder) { }

  registerForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, this.passwordValidator]],
    firstname: ['', [Validators.required]],
    lastname: ['', []],
    username: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const refreshToken = params['refreshToken'];
      const userJson = decodeURIComponent(params['user']);

      if (token && refreshToken) {
        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', userJson);
        this.router.navigate(['/home']);
      }
    });
  }

  passwordValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const value = control.value;
    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /[0-9]/.test(value);
    const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/.test(value);
    const isLengthValid = value.length > 8;
    
    const passwordValid = hasUpperCase && hasLowerCase && hasNumeric && hasSpecialChar && isLengthValid;
  
    return !passwordValid ? { 'password': true } : null;
  }

  register(firstname: string, lastname: string = "", username: string, email: string, password: string): void {
    const emailControl = this.registerForm.get('email');
    const passwordControl = this.registerForm.get('password');
    const firstNameControl = this.registerForm.get('firstname');
    const lastNameControl = this.registerForm.get('lastname');
    const usernameControl = this.registerForm.get('username');
this.loading = true
  
    if (lastNameControl?.valid && usernameControl?.valid && emailControl?.valid && passwordControl?.valid) {
      const body = {
        firstname: firstname,
        lastname: lastname,
        username: username,
        email: email,
        password: password
      }
      this.http.post(`${this.apiUrl}/auth/register`, body).subscribe({
        next: (response: any) => {
            this.toastGComponent.openToast('Registration successful, An email has been sent to you to verify your account');
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 5000);
            this.loading = false
        },
        error: (error: any) => {
          console.log(error.error);
          this.toastComponent.openToast(error.error.error);
          this.loading = false
        }
      });
    } else {
      this.toastComponent.openToast('Fill in all fields correctly');
      this.loading = false
    }
  
  }

  get email() { return this.registerForm.get('email'); }

  get password() { return this.registerForm.get('password'); }

  get firstname() { return this.registerForm.get('firstname'); }

  get lastname() { return this.registerForm.get('lastname'); }

  get username() { return this.registerForm.get('username'); }

  onSubmit() {
    this.register(this.registerForm.value.firstname!, this.registerForm.value.lastname!, this.registerForm.value.username!, this.registerForm.value.email!, this.registerForm.value.password!);
  }

  routeToLogin() {
    this.router.navigate(['/login']);
    }
  }