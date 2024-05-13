import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../pages/toast/toast.component';
import { ToastGComponent } from '../pages/toast-g/toast-g.component';
import { FilterPipe } from './filter.pipe';
import { MyStompService } from '../my-stomp.service';

@NgModule({
  declarations: [ToastComponent, ToastGComponent, FilterPipe],
  imports: [CommonModule],
  exports: [ToastComponent, ToastGComponent, FilterPipe],
  providers: [
    {
      provide: MyStompService,
    },
  ]
})
export class SharedModule { }