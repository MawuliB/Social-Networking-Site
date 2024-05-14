import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder,  ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../toast/toast.component';
import { SharedModule } from '../../services/shared.module';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { MyStompService } from '../../my-stomp.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, SharedModule],
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  apiUrl = environment.API_URL;

  loading = false;

  constructor(private route: ActivatedRoute, private router: Router,private http: HttpClient, private fb: FormBuilder, private stompService: MyStompService) { }

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, this.passwordValidator]]
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

  login(email: string, password: string): void {
    const emailControl = this.loginForm.get('email');
    const passwordControl = this.loginForm.get('password');
    this.loading = true
  
    if (emailControl?.valid && passwordControl?.valid) {
      const body = {
        email: email,
        password: password
      }
      
      this.http.post(`${this.apiUrl}/auth/authenticate`, body).subscribe({
        next: (response: any) => {
          const token = response['token'];
          const refreshToken = response['refreshToken'];
          const user = response['user'];
      
          if (token && refreshToken) {
            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(user));
            this.toastGComponent.openToast('Login successful');
            this.loading = false
            this.router.navigate(['/home']);
          }
        },
        error: (error: any) => {
          console.log(error.error);
          this.toastComponent.openToast(error.error.error);
          this.loading = false
        }
      });
    
      } else {
      this.toastComponent.openToast('Invalid email or password');
      this.loading = false
    }
  }

  get email() { return this.loginForm.get('email'); }

  get password() { return this.loginForm.get('password'); }

  onSubmit() {
    this.login(this.loginForm.value.email!, this.loginForm.value.password!);
  }

  routeToRegister() {
    this.router.navigate(['/register']);
    }
}