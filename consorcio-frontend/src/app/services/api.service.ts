// src/app/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
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

  /**
   * Hotfix: força envio do Authorization a partir do token salvo no localStorage.
   * Isso protege a chamada inicial /users caso o interceptor não esteja registrado.
   */
  getUsers() {
    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    const options = headers ? { headers } : {};
    return this.http.get<any[]>('http://localhost:8080/users', options);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/users/${id}`);
  }

  postSignUp(payload: any) {
    const base = this.baseUrl || 'http://localhost:8080';
    const url = `${base}/api/auth/register`;
    return this.http.post(url, payload);
  }

  /**
   * Login: observamos a resposta completa para capturar token em headers ou body.
   */
  postLogin(body: any): Observable<HttpResponse<any>> {
    return this.http.post<any>(`${this.api}/api/auth/login`, body, { observe: 'response' });
  }

  /**
   * Endpoint que retorna dados do usuário autenticado com base no token (ou cookie).
   * Útil no boot do app para validar/popular UserService.
   */
  getMe(): Observable<User> {
    const token = localStorage.getItem('token');
    const options = token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};
    return this.http.get<User>(`${this.api}/api/auth/me`, options);
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
