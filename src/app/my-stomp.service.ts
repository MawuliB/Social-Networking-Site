import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { environment } from '../environments/environment';
import { ReplaySubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MyStompService {
  private url = environment.API_URL;
  private socket: any;
  public stompClient: any;
  private connectedSource = new ReplaySubject<void>(1);
  connected$ = this.connectedSource.asObservable();

  constructor() {
    this.initializeWebSocketConnection();
  }

  private initializeWebSocketConnection() {
    this.socket = new SockJS(`${this.url}/ws`);
    this.stompClient = Stomp.over(this.socket);

    this.stompClient.connect(
      {},
      this.onConnected.bind(this),
      this.onError.bind(this)
    );
  }

  private onConnected() {
    console.log('Connected to WebSocket');
    
    this.connectedSource.next();

    this.stompClient.ws.onclose = this.onWebSocketClose.bind(this);  // Handle WebSocket close
  }

  private onError(error: any) {
    console.error('WebSocket connection error:', error);
    // Attempt to reconnect after 1 minute
    setTimeout(() => {
      this.initializeWebSocketConnection();
    }, 1000);
  }

  private onWebSocketClose() {
    console.log('WebSocket connection closed');
    // Reconnect after WebSocket closes
    this.initializeWebSocketConnection();
  }
}
