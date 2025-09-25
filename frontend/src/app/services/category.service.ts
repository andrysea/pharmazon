import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CategoryInterface } from '../model/CategoryInterface';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(private httpClient: HttpClient) { }

  insertCategory(headers: HttpHeaders, jsonObject: string): Observable<CategoryInterface[]>{
    return this.httpClient.post<CategoryInterface[]>(environment.server +':'+ environment.port + '/category/insert', jsonObject, {headers});
  }

  updateCategory(headers: HttpHeaders, jsonObject: string): Observable<CategoryInterface[]>{
    return this.httpClient.put<CategoryInterface[]>(environment.server +':'+ environment.port + '/category/update', jsonObject, {headers});
  }

  getCategories(headers: HttpHeaders): Observable<CategoryInterface[]>{
    return this.httpClient.get<CategoryInterface[]>(environment.server +':'+ environment.port + '/category/list', {headers});
  }
}
