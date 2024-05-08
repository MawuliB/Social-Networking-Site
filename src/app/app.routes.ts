import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './services/auth.guard';
import { RegisterComponent } from './pages/register/register.component';
import { ActivateCodeComponent } from './pages/activate-code/activate-code.component';

export const routes: Routes = [
    {path: '', redirectTo: 'login', pathMatch: 'full'},
    {path:'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'activate', component: ActivateCodeComponent},
    {
        path:'', 
        component: HomeComponent,
        children: [
            {
                path: 'home', 
                component: HomeComponent, 
                canActivate: [authGuard]

            },
        ]
            },
    {path: 'home', component: HomeComponent},
];
