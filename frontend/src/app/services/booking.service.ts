import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  constructor(private httpClient: HttpClient) { }

  insertBooking(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    return this.httpClient.post<any>( environment.server + ':' + environment.port + '/booking/insert?username=' + param, jsonObject, { headers })
  }

  getBookingsNotAccepted(headers: HttpHeaders): Observable<any>{ 
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/booking/list/notAccepted', {headers})
  }

  getBookingsAccepted(headers: HttpHeaders): Observable<any>{
    return this.httpClient.get<any>( environment.server +':'+ environment.port + '/booking/list/accepted', {headers})
  }

  update(headers: HttpHeaders, jsonObject: string): Observable<any>{  
    return this.httpClient.put<any>( environment.server +':'+ environment.port + '/booking/update', jsonObject, {headers})
  }

  deleteBooking(headers: HttpHeaders, jsonObject: string, username: string): Observable<any>{
    const param = encodeURIComponent(username);
    const options = {
      headers,
      body: jsonObject
    };

    return this.httpClient.delete<any>( environment.server +':'+ environment.port + '/booking/delete?username=' + param, options)
  }
}


