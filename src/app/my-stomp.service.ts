import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs'
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MyStompService {
    url = environment.API_URL

  socket = new SockJS(`${this.url}/ws`);
  stompClient = Stomp.over(this.socket);
}