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
            <input
              id="password"
              name="password"
              type="password"
              autocomplete="current-password"
              required
              v-model="form.password"
              class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
              placeholder="Password"
            />
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

        <!-- Alternative Login Methods -->
        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-300" />
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-gray-50 text-gray-500">Or continue with</span>
            </div>
          </div>

          <div class="mt-6 grid grid-cols-2 gap-3">
            <button
              type="button"
              @click="handleSocialLogin('google')"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
            >
              <svg class="h-5 w-5" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              <span class="ml-2">Google</span>
            </button>

            <button
              type="button"
              @click="handleSocialLogin('github')"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
            >
              <Github class="h-5 w-5" />
              <span class="ml-2">GitHub</span>
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Shield, Lock, AlertCircle, Info, Github } from 'lucide-vue-next'
import { signin } from '../api/auth';
import { setToken } from '../lib/token';
import { getEmailFromToken, getRolesFromToken } from '../lib/jwt';

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref('')

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
    const { token } = await signin(form.email, form.password);
    setToken(token);

    const roles = getRolesFromToken(token).map(role => role.toUpperCase())
    const isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')

    const userData = {
      email: getEmailFromToken(token) || form.email,
      role: isAdmin ? 'admin' : 'user',
      firstName: '',
      lastName: '',
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
      email: `demo@${provider}.com`,
      role: 'user',
      firstName: 'Demo',
      lastName: 'User',
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
