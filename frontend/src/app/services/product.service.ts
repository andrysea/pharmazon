import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private httpClient: HttpClient) { }

  getProducts (username: string, headers: HttpHeaders): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/product/list?username=' + param, {headers});
  }

  getProductsByName (name:string, headers: HttpHeaders): Observable<any>{
    const param = encodeURIComponent(name);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/product/listByName?name=' + param, {headers});
  }

  getProduct (code:string, headers: HttpHeaders): Observable<any>{
    const param = encodeURIComponent(code);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/product/getProduct?code=' + param, {headers});
  }

  getProductsByCategory (name: string, headers: HttpHeaders): Observable<any>{
    const param = encodeURIComponent(name);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/product/listByCategory?name=' + param, {headers});
  }

  createProduct (headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post( environment.server +':'+ environment.port + '/product/insert?username=' + param, jsonObject, {headers})
  }

  modifyProduct (headers: HttpHeaders, jsonObject: string, oldCode: string){
    const param = encodeURIComponent(oldCode);
    return this.httpClient.put( environment.server +':'+ environment.port + '/product/update?oldCode='+ param, jsonObject, {headers})
  }

  activateProduct (headers: HttpHeaders, code: string){
    return this.httpClient.put( environment.server +':'+ environment.port + '/product/activate', code, {headers})
  }
}
