import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { FeedbackInterface } from '../model/FeedbackInterface';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  constructor(private httpClient: HttpClient) { }

  createFeedback(headers: HttpHeaders, jsonObject: string){
    return this.httpClient.post( environment.server +':'+ environment.port + '/feedback/insert', jsonObject, {headers})
  }

  updateFeedback(headers: HttpHeaders, jsonObject: string){
    return this.httpClient.put( environment.server +':'+ environment.port + '/feedback/update', jsonObject, {headers})
  }

  deleteFeedback(headers: HttpHeaders, code: string){
    const param = encodeURIComponent(code);
    return this.httpClient.delete( environment.server +':'+ environment.port + '/feedback/delete?code=' + param, {headers})
  }

  getFeedback(headers: HttpHeaders): Observable<FeedbackInterface[]>{
    return this.httpClient.get<FeedbackInterface[]>( environment.server +':'+ environment.port + '/feedback/listFeedback', {headers})
  }

  getFeedbackUser(headers: HttpHeaders, username: string): Observable<FeedbackInterface[]>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<FeedbackInterface[]>( environment.server +':'+ environment.port + '/feedback/listFeedback/user?username=' + param, {headers})
  }
}
