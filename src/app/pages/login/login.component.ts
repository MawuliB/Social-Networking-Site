import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder,  ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [ReactiveFormsModule],
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router,private http: HttpClient, private fb: FormBuilder) { }

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const refreshToken = params['refreshToken'];

      if (token && refreshToken) {
        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', refreshToken);
        this.router.navigate(['/home']);
      }
    });
  }

  login(email: string, password: string): void {
    const body = {
      email: email,
      password: password
    }
    this.http.post('http://localhost:8080/auth/authenticate', body).subscribe((response: any) => {
      const token = response['token'];
      const refreshToken = response['refreshToken'];
  
      if (token && refreshToken) {
        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', refreshToken);
        this.router.navigate(['/home']);
      }
    });
  }

  onSubmit() {
    this.login(this.loginForm.value.email!, this.loginForm.value.password!);
  }

}