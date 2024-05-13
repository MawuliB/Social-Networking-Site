import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MyStompService } from '../../my-stomp.service';
import { Frame } from 'stompjs';
import { UserService } from '../../services/user.service';
import { SharedModule } from '../../services/shared.module';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { environment } from '../../../environments/environment';
import { GlobalService } from '../../services/global.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule, SharedModule],
  templateUrl: './messages.component.html',
  styleUrl: './messages.component.css',
})
export class MessagesComponent implements OnInit {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;
  @ViewChild('chatArea', { static: false }) chatArea!: ElementRef;
  @ViewChild('messageInput', { static: false }) messageInput!: ElementRef;

  user = JSON.parse(localStorage.getItem('user') || '{}');
  receivedMessages: string[] = [];
  connectedUsers: any[] = [];
  messages: { senderId: string; content: string }[] = [];

  messageForm = document.querySelector('#messageForm');

  url = environment.API_URL;

  stompClient = null;
  id = this.user.id;

  selectedUser: any;

  constructor(
    private http: HttpClient,
    private stompService: MyStompService,
    private userService: UserService,
    private globalService: GlobalService
  ) {}

  ngOnInit() {
    this.findAndDisplayConnectedUsers();
  }

  async findAndDisplayConnectedUsers() {
    this.stompService.stompClient.connect(
      {},
      () => this.onConnected(),
      () => this.onError()
    );
    console.log(this.stompService.stompClient.connected);
    this.userService.getConnectedUsers(this.id).subscribe({
      next: (response: any[]) => {
        this.connectedUsers = response.filter((user) => {
          return user.id.toString() !== this.id.toString();
        })
      },
    });
  }

  onError():
    | ((frame?: Frame) => any)
    | ((error: Frame | string) => any)
    | undefined {
    return (frame: Frame | string) => {
      console.error('Error:', frame);
    };
  }

  onConnected() {
    this.stompService.stompClient.subscribe(
      `/user/${this.id}/queue/messages`,
      this.onMessageReceived.bind(this)
    );
    this.stompService.stompClient.subscribe(
      `/user/public`,
      this.onMessageReceived.bind(this)
    );
    this.stompService.stompClient.subscribe(
      `/queue/errors`,
      this.onError.bind(this)
    )

    // login user if offline
    this.stompService.stompClient.send(`/app/user.loginUser`, {}, this.id);
  }

  displayMessage(senderId: string, content: string) {
    this.messages.push({ senderId, content });
  }

  async fetchAndDisplayUserChat() {
    try {
      const userChatResponse = await this.http
        .get<any>(`${this.url}/messages/${this.id}/${this.selectedUser.id}`)
        .toPromise();
      if (userChatResponse) {
        userChatResponse.forEach(
          (chat: { senderId: string; content: string }) => {
            this.displayMessage(chat.senderId, chat.content);
          }
        );
        this.chatArea.nativeElement.scrollTop =
          this.chatArea.nativeElement.scrollHeight;
          this.setNewMessageCountToZero();
      } else {
        console.error('User chat response is undefined');
      }
    } catch (error) {
      console.error('An error occurred:', error);
    }
  }

  sendMessage(event: any) {
    const messageContent = this.messageInput.nativeElement.value.trim();
    if (messageContent) {
      const chatMessage = {
        senderId: this.id,
        recipientId: this.selectedUser.id,
        content: messageContent,
        timestamp: new Date(),
      };
      this.stompService.stompClient.send(
        `/app/chat`,
        {},
        JSON.stringify(chatMessage)
      );
      this.displayMessage(this.id, messageContent);
      this.messageInput.nativeElement.value = '';
    }
    this.chatArea.nativeElement.scrollTop =
      this.chatArea.nativeElement.scrollHeight;
    event.preventDefault();
  }

  userItemClick(user: any) {
    this.selectedUser = user;
    this.fetchAndDisplayUserChat();
  }

  async backToContacts() {
    await this.fetchAndDisplayUserChat()
    this.messages = [];
    this.selectedUser = null;
    this.findAndDisplayConnectedUsers()
  }

  async onMessageReceived(payload: any) {
    this.findAndDisplayConnectedUsers();
    console.log('Message received', payload.body);
    const message = JSON.parse(payload.body);
    if (
      this.selectedUser && this.selectedUser.id && message && message.senderId &&
      this.selectedUser.id.toString() === message.senderId.toString()
    ) {
      
   this.incrementNewMessageCount()
      this.displayMessage(message.senderId, message.content);
      this.chatArea.nativeElement.scrollTop =
        this.chatArea.nativeElement.scrollHeight;
    }
  }

  incrementNewMessageCount() {
    this.globalService.totalNewMessageCount++; // Modify totalNewMessageCount
  }

  setNewMessageCountToZero(){
    this.globalService.totalNewMessageCount = 0;
  }
}
