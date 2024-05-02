import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../pages/toast/toast.component';
import { ToastGComponent } from '../pages/toast-g/toast-g.component';

@NgModule({
  declarations: [ToastComponent, ToastGComponent],
  imports: [CommonModule],
  exports: [ToastComponent, ToastGComponent]
})
export class SharedModule { }