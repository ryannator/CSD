import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

interface User {
  email: string
  role: 'admin' | 'user'
  firstName: string
  lastName: string
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

  const fullName = computed(() => {
    if (!user.value) return ''
    return `${user.value.firstName} ${user.value.lastName}`.trim()
  })

  const requiresAuth = (to: any) => {
    const publicRoutes = ['/login', '/', '/about']
    return !publicRoutes.includes(to.path)
  }

  const requiresAdmin = (to: any) => {
    const adminRoutes = ['/admin-dashboard']
    return adminRoutes.includes(to.path)
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
    fullName,
    logout,
    clearAuth,
    requiresAuth,
    requiresAdmin
  }
}
