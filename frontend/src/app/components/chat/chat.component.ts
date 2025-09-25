import { interval } from 'rxjs/internal/observable/interval';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { ChatInterface } from 'src/app/model/ChatInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { ChatService } from 'src/app/services/chat.service';
import { environment } from 'src/environments/environment.development';
import { Subscription } from 'rxjs/internal/Subscription';
import { switchMap } from 'rxjs/internal/operators/switchMap';
import { MessageInterface } from 'src/app/model/MessageInterface';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy{  

  private socket: any;
  protected username: string = "";
  protected message: string = "";
  protected errorMessage: string = "";
  protected selectedChatId: number = 0;
  protected chatCode: string = "";
  protected flag: boolean = false;
  protected lastMessage: boolean = false;
  protected chatUser: ChatInterface = {
      code: '',
      clientDto: {
          name: '',
          surname: '',
          username: '',
          number: '',
          email: '',
          taxId: '',
          password: '',
          birthDate: '',
          role: ''
      },
      messages: [],
      activeChat: false
  }
  protected messages$: MessageInterface[] = [];
  protected clients$: string[] = [];
  protected subscriptions: string[] = [];
  private stompClient: any;
  protected subscription: Subscription = new Subscription;

  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  
  constructor(private chatService: ChatService, protected auth: AuthappService, private route: Router) {
    window.addEventListener('beforeunload', this.onBeforeUnload.bind(this));
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  async onBeforeUnload(event: BeforeUnloadEvent): Promise<void> {
    event.preventDefault();
    
    this.lastMessage = true;
    this.message = "Chat lasciata.";
    
    this.sendMessage();
    await this.closeChat();
    this.subscription.unsubscribe();
  }

  async ngOnDestroy(): Promise<void> {
    if(this.flag){
      this.lastMessage = true;
      this.message = "Chat lasciata.";
      
      this.sendMessage();
    }
    
    await this.closeChat();
    this.subscription.unsubscribe();
  }

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
  
    if(!this.auth.isLoggedAdmin()){
      this.chatService.insertChat(this.auth.getHeader(), this.username)
      .subscribe({
        next: () => {},
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
      this.startChatPollingUser();
    }
    else{
      this.startChatPollingAdmin();
    }
  }

  initConnection(){
    const url =   `${environment.server}`+ ':' + `${environment.port}` + '/chat-socket';
    this.socket = new SockJS(url);
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.debug = () => {};
  }

  startChatPollingUser() { 
    this.subscription = interval(1000) 
      .pipe(
        switchMap(() => this.chatService.getChatAccepted(this.auth.getHeader(), this.username))
      )
      .subscribe({
        next: (data: ChatInterface) => {
          if (data != null) {
            this.subscription.unsubscribe();
            this.chatUser = data;
            this.chatCode = data.code;
            this.flag = true;

            this.initConnection();
            this.stompClient.connect({Authorization: `Bearer ${this.auth.getToken()}`}, ()=>{
              this.stompClient.subscribe(`/topic/${data.code}`, (message: any) => {
                const messagesContent = JSON.parse(message.body);
                this.handleResponseMessages(messagesContent);
              })
            })
          }
        },
        error: async (error: any) => {
          await this.closeChat()
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      });
  }

  startChatPollingAdmin() {
    this.subscription = interval(5000)
      .pipe(
        switchMap(() => this.chatService.updateChats(this.auth.getHeader(), this.username)),
        switchMap(() => this.chatService.getChatWaiting(this.auth.getHeader(), this.username))
      )
      .subscribe({
        next: (data: ChatInterface) => {
          if(data != null){
            this.subscription.unsubscribe();
            this.chatUser = data;
            this.chatCode = data.code;
            this.initConnection();
            this.flag = true;

            this.stompClient.connect({Authorization: `Bearer ${this.auth.getToken()}`}, () => {
              this.stompClient.subscribe(`/topic/${data.code}`, (message: any) => {
                const messagesContent = JSON.parse(message.body);
                this.handleResponseMessages(messagesContent);
              });
            });
          }
        },
        error: async (error: any) => {
          await this.closeChat();
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      });
  }

  async sendMessage(){
    var messageDto: MessageInterface = {
      userDto: {
        name: '',
        surname: '',
        username: this.username,
        email: '',
        taxId: '',
        password: '',
        birthDate: '',
        role: this.auth.getRole(),
        number: ''
      },
      message: this.message,
      lastMessage: this.lastMessage
    }

    this.stompClient.send(`/app/chat/${this.chatCode}`, {}, JSON.stringify(messageDto));
    this.message = "";
  }

async closeChatSendMessage(){
  this.lastMessage = true;
  this.message = "Chat lasciata.";
  
  this.sendMessage();
  await this.closeChat();
}

 async closeChat(){
    this.chatService.closeChat(this.auth.getHeader(), this.username)
    .subscribe({
      next: () => {        
        this.stompClient.disconnect();
        this.socket.close();
        setTimeout(() => {
          this.route.navigate(['/welcome']);
        }, 1000);
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  //Set response
  handleResponseMessages(response: any){
    this.messages$.push(response);
  }

  closePopup() {
    this.message = '';
  }

  autoClosePopup() {
    setTimeout(() => {
      this.errorMessage = '';
    }, 3000); 
  }
}
