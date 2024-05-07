import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../pages/toast/toast.component';
import { ToastGComponent } from '../pages/toast-g/toast-g.component';
import { FilterPipe } from './filter.pipe';

@NgModule({
  declarations: [ToastComponent, ToastGComponent, FilterPipe],
  imports: [CommonModule],
  exports: [ToastComponent, ToastGComponent, FilterPipe]
})
export class SharedModule { }