<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <div class="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-blue-100">
          <Shield class="h-8 w-8 text-blue-600" />
        </div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Welcome to TARIFF System
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Sign-in to acess to our unique and personalised TARIFF features!
        </p>
        <br/>
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
        <input type="hidden" name="remember" value="true" />
        <div class="rounded-md shadow-sm -space-y-px">
          <div>
            <label for="email-address" class="sr-only">Email address</label>
            <input
              id="email-address"
              name="email"
              type="email"
              autocomplete="email"
              required
              v-model="form.email"
              class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
              placeholder="Email address"
            />
          </div>


          <div>
            <label for="password" class="sr-only">Password</label>
            <div class="relative">
              <input
                id="password"
                name="password"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                required
                v-model="form.password"
                class="appearance-none rounded-none relative block w-full px-3 py-2 pr-10 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                placeholder="Password"
              />
              <button
                type="button"
                @click="togglePasswordVisibility"
                class="absolute inset-y-0 right-0 pr-3 flex items-center justify-center w-10 text-gray-400 hover:text-gray-600 focus:outline-none focus:text-gray-600 transition-colors duration-200"
                :title="showPassword ? 'Hide password' : 'Show password'"
              >
                <Eye v-if="!showPassword" class="h-5 w-5" />
                <EyeOff v-if="showPassword" class="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input
              id="remember-me"
              name="remember-me"
              type="checkbox"
              v-model="form.rememberMe"
              class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <br/><br/>
            <label for="remember-me" class="ml-2 block text-sm text-gray-900">
              Remember me
            </label>
          </div>

          <div class="text-sm">
            <a href="#" class="font-medium text-blue-600 hover:text-blue-500">
              Forgot your password?
            </a>
          </div>
        </div>

        <div>
          <br/>
          <button
            type="submit"
            :disabled="loading"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
              <Lock class="h-5 w-5 text-blue-500 group-hover:text-blue-400" aria-hidden="true" />
            </span>
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="rounded-md bg-red-50 p-4">
          <div class="flex">
            <AlertCircle class="h-5 w-5 text-red-400" />
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-800">
                Authentication Error
              </h3>
              <div class="mt-2 text-sm text-red-700">
                {{ error }}
              </div>
            </div>
          </div>
        </div>

        <!-- Demo Credentials -->
        <div class="rounded-md bg-blue-50 p-4">
          <div class="flex">
            <Info class="h-5 w-5 text-blue-400" />
            <div class="ml-3">
              <h3 class="text-sm font-medium text-blue-800">
                Demo Credentials
              </h3>
              <div class="mt-2 text-sm text-blue-700">
                <p><strong>Admin:</strong> admin@tariff.com / admin123</p>
                <p><strong>User:</strong> user@tariff.com / user123</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Signup Link -->
        <div class="text-center">
          <p class="text-sm text-gray-600">
            Don't have an account?
            <router-link to="/signup" class="font-medium text-blue-600 hover:text-blue-500">
              Create one here
            </router-link>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Shield, Lock, AlertCircle, Info, Github, Eye, EyeOff } from 'lucide-vue-next'
import { userService } from '../services/userServices';
import { setToken } from '../lib/token';
import { getEmailFromToken, getRolesFromToken } from '../lib/jwt';

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref('')
const showPassword = ref(false)

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const form = reactive({
  email: '',
  password: '',
  rememberMe: false
})

// Demo credentials for development
const demoCredentials = {
  admin: { email: 'admin@tariff.com', password: 'admin123', role: 'admin' },
  user: { email: 'user@tariff.com', password: 'user123', role: 'user' }
}

const handleLogin = async () => {
  loading.value = true;
  error.value = '';
  try {
    const { token } = await userService.signin(form.email, form.password);
    setToken(token);

    const roles = getRolesFromToken(token).map(role => role.toUpperCase())
    const isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')

    const userData = {
      id: 1,
      username: getEmailFromToken(token)?.split('@')[0] || 'user',
      email: getEmailFromToken(token) || form.email,
      role: isAdmin ? 'admin' : 'user',
      isAuthenticated: true,
      loginTime: new Date().toISOString(),
      provider: 'credentials'
    }

    localStorage.setItem('user', JSON.stringify(userData))

    const redirectTo = route.query.redirect as string | undefined

    if (redirectTo) {
      router.push(redirectTo)
      return
    }

    router.push(isAdmin ? '/admin-dashboard' : '/user-dashboard')

  } catch (e: any) {
        error.value = e?.response?.data?.error || e.message || 'Login failed';
  } finally {
    loading.value = false;
  }
};

const handleSocialLogin = async (provider: 'google' | 'github') => {
  loading.value = true
  error.value = ''

  try {
    // In a real app, this would redirect to OAuth provider
    console.log(`Social login with ${provider}`)

    // Simulate OAuth flow
    await new Promise(resolve => setTimeout(resolve, 2000))

    // Demo: automatically log in as user
    const userData = {
      id: 1,
      username: 'demo',
      email: `demo@${provider}.com`,
      role: 'user',
      isAuthenticated: true,
      loginTime: new Date().toISOString(),
      provider: provider
    }

    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('authToken', `${provider}-oauth-token-` + Date.now())

    router.push('/user-dashboard')

  } catch (err: any) {
    error.value = `${provider} login failed. Please try again.`
  } finally {
    loading.value = false
  }
}
</script>
