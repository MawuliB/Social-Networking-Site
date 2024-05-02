import { Component, NgZone } from '@angular/core';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent {
  open = false;
  message = '';
  timer: any;

  constructor(private ngZone: NgZone) { }

  openToast(message: string) {
    this.message = message;
    this.open = true;
  
    clearTimeout(this.timer);
  
    this.timer = this.ngZone.runOutsideAngular(() => {
      return setTimeout(() => {
        this.ngZone.run(() => {
          this.open = false;
        });
      }, 5000);
    });
  }

  closeToast() {
    clearTimeout(this.timer);
    this.open = false;
  }
}