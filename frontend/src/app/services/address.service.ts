import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  constructor(private httpClient: HttpClient) { }

  addAddress(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>( environment.server + ':' + environment.port + '/address/insert?username=' + param, jsonObject, {headers})
  }

  getAddress(headers: HttpHeaders, username: string): Observable<any>{ 
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/address/getAddress?username=' + param, {headers})
  }

  updateAddress(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{ 
    const param = encodeURIComponent(username);
    return this.httpClient.put<any>( environment.server +':'+ environment.port + '/address/update?username=' + param, jsonObject, {headers})
  }

  deactivateAddress(headers: HttpHeaders, code: string): Observable<any>{
    const param = encodeURIComponent(code);
    return this.httpClient.put<any>(`${environment.server}` + ':' + `${environment.port}` + '/address/deactivate', param, {headers})
  }
}
