import client from '../api/client'

export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export interface LoginResponse {
  token: string
  user: {
    id: string
    username: string
    email: string
    role: string
  }
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export class AuthService {

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await client.post('/auth/signin', credentials)
      return response.data
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Login failed')
    }
  }

  async register(userData: RegisterRequest): Promise<LoginResponse> {
    try {
      const response = await client.post('/auth/signup', userData)
      return response.data
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Registration failed')
    }
  }

  async logout(): Promise<void> {
    try {
      await client.post('/auth/logout')
    } catch (error) {
      console.error('Logout error:', error)
    }
  }

  async getCurrentUser(): Promise<unknown> {
    try {
      const response = await client.get('/users/me')
      return response.data
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Failed to get user')
    }
  }

  async refreshToken(): Promise<string> {
    try {
      const response = await client.post('/auth/refresh')
      return response.data.token
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Token refresh failed')
    }
  }

  async forgotPassword(email: string): Promise<void> {
    try {
      await client.post('/auth/forgot-password', { email })
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Password reset failed')
    }
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    try {
      await client.post('/auth/reset-password', { token, newPassword })
    } catch (error: unknown) {
      const errorResponse = error as { response?: { data?: { message?: string } } }
      throw new Error(errorResponse.response?.data?.message || 'Password reset failed')
    }
  }

  // Utility methods
  setAuthToken(token: string): void {
    client.defaults.headers.common['Authorization'] = `Bearer ${token}`
    localStorage.setItem('authToken', token)
  }

  removeAuthToken(): void {
    delete client.defaults.headers.common['Authorization']
    localStorage.removeItem('authToken')
  }

  getStoredToken(): string | null {
    return localStorage.getItem('authToken')
  }
}

export const authService = new AuthService()

// Setup axios interceptors for automatic token handling
client.interceptors.request.use(
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
client.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired, try to refresh
      try {
        const newToken = await authService.refreshToken()
        authService.setAuthToken(newToken)
        // Retry the original request
        return client.request(error.config)
      } catch (refreshError) {
        // Refresh failed, redirect to login
        console.error('Token refresh failed:', refreshError)
        authService.removeAuthToken()
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)
