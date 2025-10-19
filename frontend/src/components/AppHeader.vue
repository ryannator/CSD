<template>
  <header class="bg-white shadow-sm border-b border-gray-200">
    <div class="px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Left side - Sidebar toggle and breadcrumbs -->
        <div class="flex items-center space-x-4">
          <!-- Sidebar toggle button -->
          <SidebarTrigger />

          <!-- Breadcrumbs or page title could go here -->
          <!-- <h1 class="text-lg font-semibold text-gray-900">{{ pageTitle }}</h1> -->
        </div>

        <!-- Right side - User profile and actions -->
        <div class="flex items-center space-x-4">
          <!-- User profile dropdown -->
          <div class="relative">
            <button
              @click="toggleProfileMenu"
              class="flex items-center space-x-2 text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <div class="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center">
                <span class="text-white font-medium text-sm">
                  {{ userInitials }}
                </span>
              </div>
            </button>

            <!-- Profile dropdown menu -->
            <div
              v-if="showProfileMenu"
              class="absolute right-0 mt-2 w-64 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200"
            >
              <!-- User info section -->
              <div class="px-4 py-3 border-b border-gray-100">
                <div class="flex items-center space-x-3">
                  <div class="h-10 w-10 rounded-full bg-blue-500 flex items-center justify-center">
                    <span class="text-white font-medium text-sm">
                      {{ userInitials }}
                    </span>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ username }}</p>
                    <p class="text-xs text-gray-500 truncate">{{ userEmail }}</p>
                  </div>
                </div>
              </div>

              <!-- Menu items -->
              <div class="py-1">
                <router-link
                  to="/profile"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  @click="showProfileMenu = false"
                >
                  Profile
                </router-link>
                <button
                  @click="handleLogout"
                  class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Sign out
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { SidebarTrigger } from '@/components/ui/sidebar'
import { userService } from '../services/userServices'

const router = useRouter()
const showProfileMenu = ref(false)

// Reactive user data that updates when localStorage changes
const userData = ref<{ username?: string; email?: string } | null>(null)

// Get user data from localStorage or token
const getUserData = () => {
  const stored = localStorage.getItem('user')
  userData.value = stored ? JSON.parse(stored) : null
}

// Watch for localStorage changes
const watchStorage = () => {
  const originalSetItem = localStorage.setItem
  const originalRemoveItem = localStorage.removeItem

  localStorage.setItem = function(key: string, value: string) {
    originalSetItem.apply(this, [key, value])
    if (key === 'user') {
      getUserData()
    }
  }

  localStorage.removeItem = function(key: string) {
    originalRemoveItem.apply(this, [key])
    if (key === 'user') {
      getUserData()
    }
  }
}

const username = computed(() => {
  return userData.value?.username || 'User'
})

const userEmail = computed(() => {
  return userData.value?.email || 'user@example.com'
})

const userInitials = computed(() => {
  const name = username.value
  return name.split(' ').map((n: string) => n[0]).join('').toUpperCase().slice(0, 2)
})

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value
}

const handleLogout = async () => {
  try {
    await userService.logout()
  } catch (error) {
    console.error('Logout error:', error)
  } finally {
    // Clear local storage
    localStorage.removeItem('user')
    localStorage.removeItem('authToken')

    // Redirect to login
    router.push('/login')
  }
}

// Close dropdown when clicking outside
const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.relative')) {
    showProfileMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  getUserData() // Load initial user data
  watchStorage() // Start watching for localStorage changes
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
