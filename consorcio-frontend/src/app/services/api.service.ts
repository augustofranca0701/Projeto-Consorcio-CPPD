import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { User } from '../../models/User/user.model';
import { UpdateUser } from '../../models/User/update-user.model';
import { Observable } from 'rxjs';
import { UpdateLogin } from '../../models/User/update-login.model';
import { UserPayments } from '../../models/Payment/user-payments.model';
import { MakePayment } from '../../models/Payment/make-payment.model';
import { Group } from '../../models/Group/group.model';
import { CreateGroup } from '../../models/Group/createGroup.model';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private url = environment.api;
  constructor(private httpClient: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.httpClient.get<User[]>(`${this.url}/users`);
  }

  // Método para obter um usuário específico
  getUser(userId: number): Observable<User> {
    return this.httpClient.get<User>(`${this.url}/users/${userId}`);
  }

  updateUser(updateUser: UpdateUser,userId: number) {
    return this.httpClient.put<UpdateUser>(`${this.url}/users/${userId}/update`, updateUser);
  }

  updateLogin(updateLogin: UpdateLogin,userId: number) {
    return this.httpClient.put<UpdateUser>(`${this.url}/users/${userId}/updatelogin`, updateLogin);
  }

  getGroup(): Observable<Group[]> {
    return this.httpClient.get<Group[]>(`${this.url}/groups`);
  }

  postGroup(userId: number, grupo: CreateGroup): Observable<CreateGroup> {
    return this.httpClient.post<CreateGroup>(`${this.url}/groups/${userId}/create`, grupo);
  }

  postSignUp(user: User): Observable<User> {
    return this.httpClient.post<User>(`${this.url}/users/signup`, user);
  }

  postLogin(body: any): Observable<User> {
    return this.httpClient.post<User>(`${this.url}/users/login`, body);
  }

  getPayments(userId: number): Observable<UserPayments[]> {
    return this.httpClient.get<UserPayments[]>(`${this.url}/payments/${userId}`);
  }

  makePayment(idBoleto: number,userId: number): Observable<MakePayment> {
    return this.httpClient.put<MakePayment>(`${this.url}/payments/${userId}/${idBoleto}`, {});
  }
}
