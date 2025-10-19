import { signin } from '../api/auth';
import { authService } from './authServices';

export const userService = {
  async signin(email: string, password: string) {
    return await signin(email, password);
  },

  async login(email: string, password: string) {
    return await authService.login({ email, password });
  },

  async register(username: string, email: string, password: string) {
    return await authService.register({ username, email, password });
  },

  async logout() {
    return await authService.logout();
  },

  async getCurrentUser() {
    return await authService.getCurrentUser();
  }
};
