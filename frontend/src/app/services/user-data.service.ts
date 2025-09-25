import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { UserInterface } from 'src/app/model/UserInterface';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class UserService{ 

  constructor(private httpClient: HttpClient) { }

  getUser(username: string, headers: HttpHeaders): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/getUser?username=' + param, {headers})
  }

  getUsers(headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/list', {headers})
  }

  sendEmail(email: string): Observable<any>{
    const param = encodeURIComponent(email);
    return this.httpClient.put<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/sendEmail?email=' + param, null)
  }

  modifyUser(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.put<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/update?username=' + param, jsonObject, {headers})
  }

  deleteUser(headers: HttpHeaders,  username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.delete<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/delete?username=' + param, {headers})
  }
}
