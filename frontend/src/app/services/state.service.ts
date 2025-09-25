import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StateInterface } from '../model/StateInterface';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class StateService {

  constructor(private httpClient: HttpClient) { }

  getStates (headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/state/list', {headers})
  }
}
