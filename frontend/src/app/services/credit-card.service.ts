import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CreditCardService {

  constructor(private httpClient: HttpClient) { }

  insertCreditCard(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>(  `${environment.server}`+ ':' + `${environment.port}` + '/creditCard/insert?username=' + param, jsonObject, {headers})
  }

  updateCreditCard(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.put<any>(  `${environment.server}`+ ':' + `${environment.port}` + '/creditCard/update?username=' + param, jsonObject, {headers})
  }

  getCreditCard(headers: HttpHeaders, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.get<any>(  `${environment.server}`+ ':' + `${environment.port}` + '/creditCard/list?username=' + param, {headers})
  }

  deactivateCreditCard(headers: HttpHeaders, username: string, number: string): Observable<any> {  
    const param = encodeURIComponent(username);
    return this.httpClient.put<any>(  `${environment.server}`+ ':' + `${environment.port}` + '/creditCard/deactivate?username=' + param, number, {headers})
  }
}
