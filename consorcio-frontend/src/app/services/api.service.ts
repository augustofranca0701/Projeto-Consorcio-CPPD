import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

import { User } from '../../models/User/user.model';
import { UpdateUser } from '../../models/User/update-user.model';
import { UpdateLogin } from '../../models/User/update-login.model';

import { UserPayments } from '../../models/Payment/user-payments.model';
import { MakePayment } from '../../models/Payment/make-payment.model';

import { Group } from '../../models/Group/group.model';
import { CreateGroup } from '../../models/Group/createGroup.model';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private api = environment.api;
  baseUrl: string | undefined;

  constructor(private http: HttpClient) {}

  // USERS
  getUsers() {
    const token = localStorage.getItem('token');
    const options = token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};
    return this.http.get<any[]>('http://localhost:8080/users', options);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/users/${id}`);
  }

  /**
   * Registra um usuário no backend.
   * Retorno é do tipo any porque o backend devolve um Map com "message" ou erro.
   */
  postSignUp(payload: any) {
    const base = this.baseUrl || 'http://localhost:8080';
    const url = `${base}/api/auth/register`;
    return this.http.post(url, payload);
  }


  /**
   * Login (ajustado para o endpoint do backend)
   */
  postLogin(body: any): Observable<any> {
    return this.http.post<any>(`${this.api}/api/auth/login`, body);
  }


  updateUser(data: UpdateUser, userId: number): Observable<UpdateUser> {
    return this.http.put<UpdateUser>(
      `${this.api}/users/${userId}/update`,
      data
    );
  }

  updateLogin(data: UpdateLogin, userId: number): Observable<UpdateUser> {
    return this.http.put<UpdateUser>(
      `${this.api}/users/${userId}/updatelogin`,
      data
    );
  }

  // GROUPS
  getGroup(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.api}/groups`);
  }

  postGroup(userId: number, group: CreateGroup): Observable<CreateGroup> {
    return this.http.post<CreateGroup>(
      `${this.api}/groups/${userId}/create`,
      group
    );
  }

  // PAYMENTS
  getPayments(userId: number): Observable<UserPayments[]> {
    return this.http.get<UserPayments[]>(`${this.api}/payments/${userId}`);
  }

  makePayment(idBoleto: number, userId: number): Observable<MakePayment> {
    return this.http.put<MakePayment>(
      `${this.api}/payments/${userId}/${idBoleto}`,
      {}
    );
  }
}
