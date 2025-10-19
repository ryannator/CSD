import axios from 'axios'

const API_URL = '/auth'

export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export interface LoginResponse {
  token: string
  user: {
    id: string
    email: string
    firstName: string
    lastName: string
    role: string
  }
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
}

export class AuthService {

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await axios.post(`${API_URL}/login`, credentials)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Login failed')
    }
  }

  async register(userData: RegisterRequest): Promise<LoginResponse> {
    try {
      const response = await axios.post(`${API_URL}/register`, userData)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Registration failed')
    }
  }

  async logout(): Promise<void> {
    try {
      await axios.post(`${API_URL}/logout`)
    } catch (error) {
      console.error('Logout error:', error)
    }
  }

  async getCurrentUser(): Promise<any> {
    try {
      const response = await axios.get(`${API_URL}/me`)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to get user')
    }
  }

  async refreshToken(): Promise<string> {
    try {
      const response = await axios.post(`${API_URL}/refresh`)
      return response.data.token
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Token refresh failed')
    }
  }

  async forgotPassword(email: string): Promise<void> {
    try {
      await axios.post(`${API_URL}/forgot-password`, { email })
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Password reset failed')
    }
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    try {
      await axios.post(`${API_URL}/reset-password`, { token, newPassword })
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Password reset failed')
    }
  }

  // Utility methods
  setAuthToken(token: string): void {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    localStorage.setItem('authToken', token)
  }

  removeAuthToken(): void {
    delete axios.defaults.headers.common['Authorization']
    localStorage.removeItem('authToken')
  }

  getStoredToken(): string | null {
    return localStorage.getItem('authToken')
  }
}

export const authService = new AuthService()

// Setup axios interceptors for automatic token handling
axios.interceptors.request.use(
  (config) => {
    const token = authService.getStoredToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Handle token expiration
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired, try to refresh
      try {
        const newToken = await authService.refreshToken()
        authService.setAuthToken(newToken)
        // Retry the original request
        return axios.request(error.config)
      } catch (refreshError) {
        // Refresh failed, redirect to login
        authService.removeAuthToken()
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)
