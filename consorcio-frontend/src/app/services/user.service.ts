import { Injectable } from '@angular/core';
import { User } from '../../models/User/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  user!: User;

  setUser(user: User) {
    this.user = user;
  }

  getUser(): User {
    return this.user;
  }
}
