import { Injectable } from '@angular/core';
import { UserInterface } from 'src/app/model/UserInterface';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { environment } from 'src/environments/environment.development';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/internal/Observable';
import { of } from 'rxjs/internal/observable/of';

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  user$: UserInterface[] = []
  error: string = ""

  constructor(private httpClient: HttpClient, private route: Router) {}

  authenticate(username: string, password: string){
    return this.httpClient.post<any>(`${environment.server}`+ ':' + `${environment.port}` + '/auth/authenticate', {username, password})
  }

  registration(user: UserInterface): Observable<any>{    
    return this.httpClient.post<any>(`${environment.server}`+ ':' + `${environment.port}` + '/auth/register', user)
  }

  modifyPassword(headers: HttpHeaders, jsonObject: string): Observable<any>{    
    return this.httpClient.put<any>(`${environment.server}`+ ':' + `${environment.port}` + '/user/updatePassword', jsonObject, {headers})
  }

  revokeToken(){
    const headers = this.getHeader();
    const username = sessionStorage.getItem("username");

    if(username !== null){
      const param = encodeURIComponent(username);
      return this.httpClient.put<any>(`${environment.server}`+ ':' + `${environment.port}` + '/auth/revokeToken?username=' + param, null, {headers})
    }
    else{
      return of(null);
    }
  }

  getToken(){
    let token : string = ""
    var tokenUser = sessionStorage.getItem("Token")

    if(tokenUser!=null)
        token = tokenUser
    
    return token;
  }

  getHeader(){
    const headers = new HttpHeaders({
      'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PUT, DELETE',
      'Access-Control-Allow-Headers': 'Content-Type, X-Auth-Token, Origin, Authorization',
      'Access-Control-Allow-Credentials': 'true',
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.getToken()
    })
    return headers
  }

  async validUser(): Promise<any> {
    const username = sessionStorage.getItem("username");
  
    if (username == null) {
      this.route.navigate(['/error'], {
        state: { status: 0 },
      });
    } else {
      try {
        const param = encodeURIComponent(username);
        const data: any = await this.httpClient.get<any>( environment.server +':'+ environment.port + '/auth/checkToken?username=' + param).toPromise();
      } catch (error) {
        this.route.navigate(['/error'], {
          state: { status: 0 },
        });
      }
    }
  }

  clearAll(){sessionStorage.clear()}
  loggedUser = () : string | null => (sessionStorage.getItem("username")) ? sessionStorage.getItem("username") : ""
  isLogged = () : boolean => (sessionStorage.getItem("username")) ? true : false
  isLoggedAdmin = () : boolean => (sessionStorage.getItem("role") == environment.ADMIN) ? true : false

  getRole(): string {
    const role = sessionStorage.getItem("role")
    if(role != null){
      return role
    }
    else{
      return ""
    }
  }
}
