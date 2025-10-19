<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h1 class="text-2xl font-bold text-gray-900 mb-6">Profile Settings</h1>

          <!-- Loading state -->
          <div v-if="loading" class="flex justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>

          <!-- Error state -->
          <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
            <p class="text-red-800">{{ error }}</p>
          </div>

          <!-- Profile form -->
          <div v-else class="space-y-6">
            <!-- User Info Section -->
            <div class="border-b border-gray-200 pb-6">
              <h2 class="text-lg font-medium text-gray-900 mb-4">Personal Information</h2>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Username</label>
                  <input
                    v-model="profileForm.username"
                    type="text"
                    class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
                  <input
                    v-model="profileForm.email"
                    type="email"
                    disabled
                    class="w-full border border-gray-300 rounded-md px-3 py-2 bg-gray-50 text-gray-500"
                  />
                  <p class="text-xs text-gray-500 mt-1">Email cannot be changed</p>
                </div>
              </div>

              <div class="mt-4">
                <button
                  @click="updateProfile"
                  :disabled="saving"
                  class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                >
                  {{ saving ? 'Saving...' : 'Update Profile' }}
                </button>
              </div>
            </div>

            <!-- Password Change Section -->
            <div class="border-b border-gray-200 pb-6">
              <h2 class="text-lg font-medium text-gray-900 mb-4">Change Password</h2>

              <div class="space-y-4 max-w-md">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Current Password</label>
                  <input
                    v-model="passwordForm.currentPassword"
                    type="password"
                    class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">New Password</label>
                  <input
                    v-model="passwordForm.newPassword"
                    type="password"
                    class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Confirm New Password</label>
                  <input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>
              </div>

              <div class="mt-4">
                <button
                  @click="changePassword"
                  :disabled="savingPassword"
                  class="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:opacity-50"
                >
                  {{ savingPassword ? 'Changing...' : 'Change Password' }}
                </button>
              </div>
            </div>

            <!-- Account Actions Section -->
            <div>
              <h2 class="text-lg font-medium text-gray-900 mb-4">Account Actions</h2>

              <div class="bg-red-50 border border-red-200 rounded-md p-4">
                <h3 class="text-sm font-medium text-red-800 mb-2">Danger Zone</h3>
                <p class="text-sm text-red-700 mb-4">
                  Once you delete your account, there is no going back. Please be certain.
                </p>
                <button
                  @click="deleteAccount"
                  :disabled="deleting"
                  class="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
                >
                  {{ deleting ? 'Deleting...' : 'Delete Account' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <div
      v-if="showToast"
      :class="toastType === 'success' ? 'bg-green-500' : 'bg-red-500'"
      class="fixed top-4 right-4 z-50 px-6 py-3 rounded-md shadow-lg text-white flex items-center space-x-2"
      @click="hideToast"
    >
      <svg v-if="toastType === 'success'" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
      </svg>
      <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
      </svg>
      <span>{{ toastMessage }}</span>
      <button @click="hideToast" class="ml-2 text-white hover:text-gray-200">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
        </svg>
      </button>
    </div>

    <!-- Delete Account Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-600 bg-opacity-50 p-4"
      @click.self="cancelDeleteAccount"
      @keydown.esc="cancelDeleteAccount"
      tabindex="0"
    >
      <div class="w-full max-w-md rounded-md bg-white shadow-lg p-6">
        <div class="flex flex-col items-center">
          <div class="flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
            <svg class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>

          <h3 class="text-lg font-medium text-gray-900 text-center mb-2">
            Delete Account
          </h3>

          <div class="text-center mb-6 w-full">
            <p class="text-sm text-gray-500 mb-2">
              Are you sure you want to delete your account?
            </p>
            <p class="text-xs text-red-600">
              This action cannot be undone and will permanently delete all your data.
            </p>
          </div>

          <div class="flex justify-center space-x-3 w-full">
            <button
              @click="cancelDeleteAccount"
              :disabled="deleting"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              @click="confirmDeleteAccount"
              :disabled="deleting"
              class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
            >
              {{ deleting ? 'Deleting...' : 'Delete Account' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import client from '../api/client'

const router = useRouter()

const loading = ref(true)
const error = ref('')
const saving = ref(false)
const savingPassword = ref(false)
const deleting = ref(false)
const showDeleteModal = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref<'success' | 'error'>('success')

const profileForm = ref({
  username: '',
  email: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Toast notification functions
const showToastNotification = (message: string, type: 'success' | 'error' = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true

  // Auto-hide after 3 seconds
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

const hideToast = () => {
  showToast.value = false
}

const loadProfile = async () => {
  try {
    loading.value = true
    error.value = ''

    // Get current user data from localStorage first
    const userData = localStorage.getItem('user')
    if (userData) {
      const user = JSON.parse(userData)
      profileForm.value = {
        username: user.username || '',
        email: user.email || ''
      }
    }

    // Try to get fresh data from backend
    try {
      const response = await client.get('/users/me')
      const userData = response.data
      profileForm.value = {
        username: userData.username || '',
        email: userData.email || ''
      }
    } catch {
      console.log('Using cached user data')
    }

  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to load profile'
    console.error('Error loading profile:', err)
  } finally {
    loading.value = false
  }
}

const updateProfile = async () => {
  try {
    saving.value = true
    error.value = ''

    await client.put('/users/profile', {
      username: profileForm.value.username
    })

    // Update localStorage
    const userData = localStorage.getItem('user')
    if (userData) {
      const user = JSON.parse(userData)
      user.username = profileForm.value.username
      localStorage.setItem('user', JSON.stringify(user))
    }

    showToastNotification('Profile updated successfully!')
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to update profile'
    showToastNotification(error.value, 'error')
    console.error('Error updating profile:', err)
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    error.value = 'New passwords do not match'
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    error.value = 'New password must be at least 6 characters long'
    return
  }

  try {
    savingPassword.value = true
    error.value = ''

    await client.put('/users/change-password', {
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword
    })

    // Clear password form
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }

    showToastNotification('Password changed successfully!')
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to change password'
    showToastNotification(error.value, 'error')
    console.error('Error changing password:', err)
  } finally {
    savingPassword.value = false
  }
}

const deleteAccount = async () => {
  showDeleteModal.value = true
}

const confirmDeleteAccount = async () => {
  try {
    deleting.value = true
    error.value = ''

    await client.delete('/users/account')

    // Clear local storage and redirect to login
    localStorage.removeItem('user')
    localStorage.removeItem('authToken')
    localStorage.removeItem('userData')

    showToastNotification('Account deleted successfully!')

    // Redirect to login after a short delay
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to delete account'
    showToastNotification(error.value, 'error')
    console.error('Error deleting account:', err)
  } finally {
    deleting.value = false
    showDeleteModal.value = false
  }
}

const cancelDeleteAccount = () => {
  showDeleteModal.value = false
}

onMounted(() => {
  loadProfile()
})
</script>
