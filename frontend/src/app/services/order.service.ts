import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private httpClient: HttpClient) { }

  insertOrder(headers: HttpHeaders, username:string, jsonObject: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>( environment.server +':'+ environment.port + '/order/insert?username=' + param, jsonObject, {headers})
  }

  approvedOrNotOrder(headers: HttpHeaders, code: string, value: boolean): Observable<any>{
    const paramCode = encodeURIComponent(code);
    return this.httpClient.put<any>( environment.server +':'+ environment.port + '/order/checkOrder?code=' + paramCode + '&value=' + value, null, {headers})
  }

  getListOrderNoSomeStates(headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/order/listOrder/noSomeStates', {headers})
  }

  getListOrderHistory(headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/order/listOrder/history', {headers})
  }

  getListOrderClient(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/order/listOrder/user?username=' + param, {headers})
  }

  getListOrderWaiting(headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/order/listOrder/waiting', {headers})
  }

  getListUserOrderWaiting(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/order/listUserOrdersWaiting?username=' + param, {headers})
  }

  updateOrder(headers: HttpHeaders, jsonObject: string, flag: boolean): Observable<any>{
    return this.httpClient.put<any>( environment.server +':'+ environment.port + '/order/update?flag=' + flag, jsonObject, {headers})
  }
}
