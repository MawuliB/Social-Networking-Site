import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
    {path: '', redirectTo: 'login', pathMatch: 'full'},
    {path:'login', component: LoginComponent},
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
