import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ServiceInterface } from '../model/ServiceInterface';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ServiceService {

  constructor(private httpClient: HttpClient) { }

  createService (headers: HttpHeaders, username: string, jsonObject: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>( environment.server +':'+ environment.port + '/service/insert?username=' + param, jsonObject, {headers})
  }

  getServices (headers: HttpHeaders, username:string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/service/list?username=' + param, {headers})
  }

  getServicesByCode(headers: HttpHeaders, code: string): Observable<any>{
    const param = encodeURIComponent(code);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/service/getService?code=' + param, {headers})
  }
  
  getServicesListByName(headers: HttpHeaders, username: string, name: string): Observable<any>{
    const usernameParam = encodeURIComponent(username);
    const nameParam = encodeURIComponent(name);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/service/getService/name?username=' + usernameParam +'&name=' + nameParam, {headers})
  }

  deleteService (headers: HttpHeaders, code: string): Observable<any>{
    const param = encodeURIComponent(code);
    return this.httpClient.delete<any>( environment.server +':'+ environment.port + '/service/delete?code='+ param, {headers})
  }

  modifyService(headers: HttpHeaders, code: string, jsonObject: string): Observable<any>{
    const param = encodeURIComponent(code);
    return this.httpClient.put<any>( environment.server +':'+ environment.port + '/service/update?oldCode='+ param, jsonObject, {headers})
  }
}
