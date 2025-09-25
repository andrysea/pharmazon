import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(private httpClient: HttpClient) { }

  getProductsUser(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/cart/list?username=' + param, {headers});
  }

  insertProduct(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>( environment.server +':'+ environment.port + '/cart/insertProduct?username=' + param, jsonObject, {headers})
  }

  getCartItemDeliverd (headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/cart/getCartItem/delivered?username=' + param, {headers});
  }

  removeProduct(headers: HttpHeaders, code: string, username: string): Observable<any>{
    const paramCode = encodeURIComponent(code);
    const paramUsername = encodeURIComponent(username);
    return this.httpClient.delete<any>( environment.server +':'+ environment.port + '/cart/removeProduct?username=' + paramUsername + '&code=' + paramCode, {headers})
  }
}
