import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  
  constructor(private http: HttpClient) { }

  insertChat(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.http.post<any>( environment.server +':'+ environment.port + '/chat/insert?username=' + param, {}, {headers});
  }

  getChatAccepted(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.http.get( environment.server + ':' + environment.port + '/chat/getChatAccepted?username=' + param, { headers });
  }

  getChatWaiting(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.http.get( environment.server + ':' + environment.port + '/chat/getChatWaiting?username=' + param, { headers });
  }

  updateChats(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.http.put<any>( environment.server + ':' + environment.port + '/chat/updateChats?username=' + param, null, { headers });
  }

  closeChat(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.http.put<Boolean>( environment.server + ':' + environment.port + '/chat/closeChat?username=' + param, null, { headers })
  }
}
