import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder,  ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  apiUrl = environment.API_URL;

  constructor(private route: ActivatedRoute, private router: Router,private http: HttpClient, private fb: FormBuilder) { }

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
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

  login(email: string, password: string): void {
    const body = {
      email: email,
      password: password
    }
    this.http.post(`${this.apiUrl}/auth/authenticate`, body).subscribe((response: any) => {
      const token = response['token'];
      const refreshToken = response['refreshToken'];
      const user = response['user'];
  
      if (token && refreshToken) {
        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        this.router.navigate(['/home']);
      }
    });
  }

  get email() { return this.loginForm.get('email'); }

  get password() { return this.loginForm.get('password'); }

  onSubmit() {
    this.login(this.loginForm.value.email!, this.loginForm.value.password!);
  }

}