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

  loading = false;
  loadingForUsers = false
  loadingForFileUpload = false;
  isModalOpen = false;

  user = JSON.parse(localStorage.getItem('user') || '{}');
  token = localStorage.getItem('token');
  receivedMessages: string[] = [];
  connectedUsers: any[] = [];
  messages: { senderId: string; content: string; fileType: string }[] = [];

  messageForm = document.querySelector('#messageForm');

  url = environment.API_URL;

  stompClient = null;
  id = this.user.id;
  selectedFile: File | null = null;

  selectedUser: any;

  constructor(
    private http: HttpClient,
    private stompService: MyStompService,
    private userService: UserService,
    private globalService: GlobalService
  ) {}

  ngOnInit() {
    this.findAndDisplayConnectedUsers(true);
    this.stompService.connected$.subscribe(() => {
      this.onConnected();
    });
  }

  onConnected() {
    console.log('Connected to WebSocket In Messages Component');

    console.log(this.id);
    // Subscriptions
    this.stompService.stompClient.subscribe(
      `/user/${this.id}/queue/messages`,
      this.onMessageReceived.bind(this)
    );
    this.stompService.stompClient.subscribe(
      `/user/public`,
      this.onMessageReceived.bind(this)
    );
    this.stompService.stompClient.subscribe(
      `/queue/errors`
    )

    // login user if offline
    this.stompService.stompClient.send(`/app/user.loginUser`, {}, this.id);
  }

  findAndDisplayConnectedUsers(shouldLoad: boolean) {
    this.loadingForUsers = shouldLoad;
    console.log(this.stompService.stompClient.connected);
    this.userService.getConnectedUsers(this.id).subscribe({
      next: (response: any[]) => {
        const updatedUsers = response.filter((user) => {
          return user.id.toString() !== this.id.toString();
        });
  
        this.connectedUsers = updatedUsers.map((user) => {
          const existingUser = this.connectedUsers.find(
            (connectedUser) => connectedUser.id === user.id
          );
  
          return existingUser
            ? { ...user, newMessageCount: existingUser.newMessageCount || 0 }
            : { ...user, newMessageCount: 0 };
        });

      // update status of selected user
      if (this.selectedUser) {
        this.selectedUser.status = this.connectedUsers.find(user => user.id === this.selectedUser.id).status;
      }

        this.loadingForUsers = false;
      },
      error: (error) => {
        console.error('An error occurred:', error);
        this.loadingForUsers = false;
      }
    });
  }

  async displayMessage(senderId: string, content: string, fileType: string) {
    this.setNewMessageCountToZero()
    this.messages.push({ senderId, content, fileType });
    this.scrollChatToBottom();
  }

  async fetchAndDisplayUserChat() {
    this.loading = true;
    try {
      const userChatResponse = await this.http
        .get<any>(`${this.url}/messages/${this.id}/${this.selectedUser.id}`)
        .toPromise();
      if (userChatResponse) {
        userChatResponse.forEach(
          (chat: { senderId: string; content: string; fileType: string }) => {
            this.displayMessage(chat.senderId, chat.content, chat.fileType);
          }
        );
        this.setNewMessageCountToZero();
      } else {
        console.error('User chat response is undefined');
      }
    } catch (error) {
      console.error('An error occurred:', error);
    } finally {
      this.loading = false;
      this.scrollChatToBottom();
    }
  }

  sendMessage(event: any) {
    const messageContent = this.messageInput.nativeElement.value.trim();
    if (messageContent) {
      const chatMessage = {
        senderId: this.id,
        recipientId: this.selectedUser.id,
        content: messageContent,
        fileType: 'TEXT',
        timestamp: new Date(),
      };
  
      // Check if the WebSocket connection is open
      this.handleSendMessage(chatMessage, messageContent);
    }
    this.scrollChatToBottom();
    event.preventDefault();
  }

  private handleSendMessage(chatMessage: { senderId: any; recipientId: any; content: any; fileType: string; timestamp: Date; }, messageContent: any) {
    if (this.stompService.stompClient.connected) {
      // If it's open, send the message
      this.stompService.stompClient.send(
        `/app/chat`,
        {},
        JSON.stringify(chatMessage)
      );
      this.displayMessage(this.id, messageContent, chatMessage.fileType);
      this.messageInput.nativeElement.value = '';
    } else {
      // If it's not open, wait for it to open and then send the message
      this.stompService.stompClient.connect({}, () => {
        this.stompService.stompClient.send(
          `/app/chat`,
          {},
          JSON.stringify(chatMessage)
        );
        this.displayMessage(this.id, messageContent, chatMessage.fileType);
        this.messageInput.nativeElement.value = '';
      });
    }
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files[0]) {
      this.selectedFile = event.target.files[0];
  
      if(this.selectedFile!.size < 20971520) {
      // Create a FileReader to read the contents of the selected file
      const reader = new FileReader();
  
      // Set the onload event to update the img src with the file content
      reader.onload = (e: any) => {
        const preview = document.getElementById('preview') as HTMLImageElement;
        const upload = document.getElementById('upload') as HTMLElement;
        if (this.selectedFile!.type.startsWith('image/')) {
          preview.src = e.target.result;
          preview.classList.remove('hidden');
        } else if (this.selectedFile!.type.startsWith('video/')) {
          const video = document.createElement('video');
          video.src = e.target.result;
          video.controls = true;
          preview.replaceWith(video);
        } else {
          this.toastComponent.openToast('Invalid file type. Please select an image or video file');
        }
        upload.classList.add('hidden');
      };
  
      // Read the contents of the file
      if (this.selectedFile) {
        reader.readAsDataURL(this.selectedFile);
      }
    }else {
      this.toastComponent.openToast('File size should not exceed 20MB');
    }
  }
  }

  onFileUpload() {
    if (this.selectedFile) {
      if (this.selectedFile.size < 20971520) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);

       // Get the token from localStorage
       const token = localStorage.getItem('token');

       // Create headers with the authorization token
       const headers = { Authorization: `Bearer ${token}` };

       this.loadingForFileUpload = true;
  
      this.http.post<{url: string, fileType: string}>(`${this.url}/file-sharing/upload`, formData, {
        headers,
      }).subscribe({
        next: (response) => {
          const chatMessage = {
            senderId: this.id,
            recipientId: this.selectedUser.id,
            content: response.url,
            fileType: response.fileType,
            timestamp: new Date(),
          };
          this.handleSendMessage(chatMessage, response.url);
          this.scrollChatToBottom();
          this.closeModal();
          this.loadingForFileUpload = false;
          this.clearFile();
        },
        error: (error) => {
          console.error('An error occurred:', error);
          this.closeModal();
          this.loadingForFileUpload = false;
          this.toastComponent.openToast('An error occurred while uploading the file');
        }
      }
      );
    } else {
      this.toastComponent.openToast('File size should not exceed 20MB');
      this.loadingForFileUpload = false;
    }
    } else {
      this.toastComponent.openToast('No file selected');
      this.loadingForFileUpload = false;
    }
  }

  clearFile() {
    // Clear the selected file
    this.selectedFile = null;
  
    // Reset the file input
    const fileInput = document.getElementById('dropzone-file') as HTMLInputElement;
    fileInput.value = '';
  
    // Hide the preview
    const preview = document.getElementById('preview') as HTMLImageElement;
    const upload = document.getElementById('upload') as HTMLElement;
    if (preview) {
      if (preview.tagName === 'IMG') {
        preview.src = '';
        preview.classList.add('hidden');
      } else {
        const img = document.createElement('img');
        img.id = 'preview';
        img.classList.add('hidden');
        preview.replaceWith(img);
      }
      upload.classList.remove('hidden');
    }
  }

  // Handle the user item click event
  async userItemClick(user: any) {
    this.selectedUser = user;
    console.log(this.selectedUser);

    // Reset the newMessageCount for the selected user
    if (this.selectedUser.newMessageCount) {
      this.selectedUser.newMessageCount = 0;
    }
    await this.fetchAndDisplayUserChat();
    this.scrollChatToBottom();
  }
  
  // Handle the back to contacts button click
  async backToContacts() {
    // Reset the newMessageCount for the selected user
    if (this.selectedUser && this.selectedUser.newMessageCount) {
      this.selectedUser.newMessageCount = 0;
      console.log(this.selectedUser);
    }

    if (
      this.connectedUsers &&
      this.connectedUsers.length &&
      this.connectedUsers.some(
        (user) => user && this.selectedUser && user.id.toString() === this.selectedUser.id.toString()
      )
    ) {
      const user = this.connectedUsers.find(
        (user) => user && this.selectedUser && user.id.toString() === this.selectedUser.id.toString()
      );
      if (user) {
        user.newMessageCount = 0;
      }
    }

    this.messages = [];
    this.selectedUser = null;
    this.findAndDisplayConnectedUsers(true);
  }

  // Handle the message received event
  async onMessageReceived(payload: any) {
    this.findAndDisplayConnectedUsers(false);
    const message = JSON.parse(payload.body);
    if (
      this.selectedUser && this.selectedUser.id && message && message.senderId &&
      this.selectedUser.id.toString() === message.senderId.toString()
    ) { // Check if the message is for the selected user and display it
      this.displayMessage(message.senderId, message.content, message.fileType);
      this.chatArea.nativeElement.scrollTop =
        this.chatArea.nativeElement.scrollHeight;
    }

    if (
      this.connectedUsers &&
      this.connectedUsers.length &&
      this.connectedUsers.some(
        (user) => user && message && message.senderId && user.id.toString() === message.senderId.toString()
      )
    ) {
      this.incrementNewMessageCount();
      const user = this.connectedUsers.find(
        (user) => user && message && message.senderId && user.id.toString() === message.senderId.toString()
      );
      if (user) {
        user.newMessageCount++;
      }
    }

  }

  // Scroll the chat area to the bottom
  scrollChatToBottom() {
    if (this.chatArea) {
      setTimeout(() => {
        this.chatArea.nativeElement.scrollTop = this.chatArea.nativeElement.scrollHeight;
      }, 0);
    }else {
      setTimeout(() => {
        this.chatArea.nativeElement.scrollTop = this.chatArea.nativeElement.scrollHeight;
      }, 500);
    }
  }

  openModal() {
    this.isModalOpen = true;
  }
  
  closeModal() {
    this.isModalOpen = false;
  }

  incrementNewMessageCount() {
    this.globalService.totalNewMessageCount++; // Modify totalNewMessageCount
  }

  setNewMessageCountToZero(){
    this.globalService.totalNewMessageCount = 0;
  }

  onKeyDown(event: KeyboardEvent) {
    // Handle keydown event
  }
  
  onKeyUp(event: KeyboardEvent) {
    // Handle keyup event
  }
  
  onKeyPress(event: KeyboardEvent) {
    // Handle keypress event
  }
}
