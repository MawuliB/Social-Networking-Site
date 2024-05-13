import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs'

@Injectable({
  providedIn: 'root'
})
export class MyStompService {

  socket = new SockJS('http://localhost:8080/ws');
  stompClient = Stomp.over(this.socket);
}