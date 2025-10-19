import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

interface User {
  username: string
  email: string
  role: 'admin' | 'user'
  isAuthenticated: boolean
  loginTime: string
  provider?: string
}

// Global reactive state
const user = ref<User | null>(null)
const authToken = ref<string | null>(null)

export function useAuth() {
  const router = useRouter()

  const initAuth = () => {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('authToken')

    if (savedUser && savedToken) {
      try {
        user.value = JSON.parse(savedUser)
        authToken.value = savedToken
      } catch (error) {
        console.error('Error parsing saved user data:', error)
        clearAuth()
      }
    }
  }

  const clearAuth = () => {
    user.value = null
    authToken.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('authToken')
  }

  const logout = () => {
    clearAuth()
    router.push('/login')
  }

  const isAuthenticated = computed(() => {
    const savedToken = localStorage.getItem('authToken')
    return savedToken !== null
  })

  const isAdmin = computed(() => {
    return user.value?.role === 'admin'
  })

  const isUser = computed(() => {
    return user.value?.role === 'user'
  })

  const requiresAuth = (to: unknown) => {
    const publicRoutes = ['/login', '/', '/about']
    return !publicRoutes.includes((to as any).path)
  }

  const requiresAdmin = (to: unknown) => {
    const adminRoutes = ['/admin-dashboard']
    return adminRoutes.includes((to as any).path)
  }

  if (user.value === null) {
    initAuth()
  }

  return {
    user: computed(() => user.value),
    authToken: computed(() => authToken.value),
    isAuthenticated,
    isAdmin,
    isUser,
    logout,
    clearAuth,
    requiresAuth,
    requiresAdmin
  }
}
