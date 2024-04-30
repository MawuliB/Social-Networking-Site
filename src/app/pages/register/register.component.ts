import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder,  ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  apiUrl = environment.API_URL;

  constructor(private route: ActivatedRoute, private router: Router,private http: HttpClient, private fb: FormBuilder) { }

  registerForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });


  register(email: string, password: string): void {
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

  onSubmit() {
    this.register(this.registerForm.value.email!, this.registerForm.value.password!);
  }
}
