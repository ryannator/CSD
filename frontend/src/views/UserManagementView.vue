<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex justify-between items-center mb-6">
            <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
            <button
              @click="showCreateModal = true"
              class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              Add User
            </button>
          </div>

          <!-- Loading state -->
          <div v-if="loading" class="flex justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>

          <!-- Error state -->
          <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
            <p class="text-red-800">{{ error }}</p>
          </div>

          <!-- Users table -->
          <div v-else class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="user in users" :key="user.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center">
                        <span class="text-white font-medium text-sm">
                          {{ user.username.charAt(0).toUpperCase() }}
                        </span>
                      </div>
                      <div class="ml-3">
                        <div class="text-sm font-medium text-gray-900">{{ user.username }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.email }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      :class="user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'"
                      class="inline-flex px-2 py-1 text-xs font-semibold rounded-full"
                    >
                      {{ user.role }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                      @click="editUser(user)"
                      class="text-blue-600 hover:text-blue-900 mr-3"
                    >
                      Edit
                    </button>
                    <button
                      @click="deleteUser(user.id)"
                      class="text-red-600 hover:text-red-900"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
            <svg class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>

          <h3 class="text-lg font-medium text-gray-900 text-center mb-2">
            Delete User
          </h3>

          <div class="text-center mb-6">
            <p class="text-sm text-gray-500 mb-2">
              Are you sure you want to delete this user?
            </p>
            <div class="bg-gray-50 rounded-md p-3">
              <p class="text-sm font-medium text-gray-900">{{ userToDelete?.username }}</p>
              <p class="text-xs text-gray-500">{{ userToDelete?.email }}</p>
            </div>
            <p class="text-xs text-red-600 mt-2">
              This action cannot be undone.
            </p>
          </div>

          <div class="flex justify-center space-x-3">
            <button
              @click="cancelDelete"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
            >
              Cancel
            </button>
            <button
              @click="confirmDelete"
              class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
            >
              Delete User
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit User Modal -->
    <div
      v-if="showCreateModal || editingUser"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-600 bg-opacity-50 p-4"
      @click.self="cancelEdit"
      @keydown.esc="cancelEdit"
      tabindex="0"
    >
      <div class="w-full max-w-md rounded-md bg-white shadow-lg max-h-[90vh] overflow-y-auto p-5">
        <h3 class="text-lg font-medium text-gray-900 mb-4">
          {{ editingUser ? 'Edit User' : 'Create New User' }}
        </h3>

        <form @submit.prevent="saveUser" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Username</label>
            <input
              v-model="userForm.username"
              type="text"
              required
              class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Email</label>
            <input
              v-if="!editingUser"
              v-model="userForm.email"
              type="email"
              required
              class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
            <input
              v-else
              :value="userForm.email"
              type="email"
              readonly
              class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Password</label>
            <input
              v-model="userForm.password"
              type="password"
              :required="!editingUser"
              class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Role</label>
            <select
              v-model="userForm.role"
              required
              class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <div class="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              @click="cancelEdit"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
            >
              {{ saving ? 'Saving...' : (editingUser ? 'Update' : 'Create') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-600 bg-opacity-50 p-4"
      @click.self="cancelDelete"
      @keydown.esc="cancelDelete"
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
            Delete User
          </h3>

          <div class="text-center mb-6 w-full">
            <p class="text-sm text-gray-500 mb-2">
              Are you sure you want to delete this user?
            </p>
            <div class="bg-gray-50 rounded-md p-3">
              <p class="text-sm font-medium text-gray-900">{{ userToDelete?.username }}</p>
              <p class="text-xs text-gray-500">{{ userToDelete?.email }}</p>
            </div>
            <p class="text-xs text-red-600 mt-2">
              This action cannot be undone.
            </p>
          </div>

          <div class="flex justify-center space-x-3">
            <button
              @click="cancelDelete"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
            >
              Cancel
            </button>
            <button
              @click="confirmDelete"
              class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
            >
              Delete User
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import client from '../api/client'

interface User {
  id: number
  username: string
  email: string
  role: string
}

const users = ref<User[]>([])
const loading = ref(true)
const error = ref('')
const saving = ref(false)
const showCreateModal = ref(false)
const editingUser = ref<User | null>(null)
const showDeleteModal = ref(false)
const userToDelete = ref<User | null>(null)

const userForm = ref({
  username: '',
  email: '',
  password: '',
  role: 'USER'
})

const loadUsers = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await client.get('/users')
    // Sort users by ID
    users.value = response.data.sort((a: User, b: User) => a.id - b.id)
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to load users'
    console.error('Error loading users:', err)
  } finally {
    loading.value = false
  }
}

const saveUser = async () => {
  try {
    saving.value = true

    if (editingUser.value) {
      // Update existing user
      const updateData: { username: string; role: string; password?: string } = {
        username: userForm.value.username,
        role: userForm.value.role
      }

      if (userForm.value.password) {
        updateData.password = userForm.value.password
      }

      await client.put(`/users/${editingUser.value.id}`, updateData)
    } else {
      // Create new user
      await client.post('/users', {
        username: userForm.value.username,
        email: userForm.value.email,
        password: userForm.value.password,
        role: userForm.value.role
      })
    }

    await loadUsers()
    cancelEdit()
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to save user'
    console.error('Error saving user:', err)
  } finally {
    saving.value = false
  }
}

const editUser = (user: User) => {
  editingUser.value = user
  userForm.value = {
    username: user.username,
    email: user.email,
    password: '',
    role: user.role
  }
}

const deleteUser = async (userId: number) => {
  const user = users.value.find(u => u.id === userId)
  if (user) {
    userToDelete.value = user
    showDeleteModal.value = true
  }
}

const confirmDelete = async () => {
  if (!userToDelete.value) return

  try {
    await client.delete(`/users/${userToDelete.value.id}`)
    await loadUsers()
    showDeleteModal.value = false
    userToDelete.value = null
  } catch (err: unknown) {
    const errorResponse = err as { response?: { data?: { error?: string } } }
    error.value = errorResponse.response?.data?.error || 'Failed to delete user'
    console.error('Error deleting user:', err)
  }
}

const cancelDelete = () => {
  showDeleteModal.value = false
  userToDelete.value = null
}

const cancelEdit = () => {
  showCreateModal.value = false
  editingUser.value = null
  userForm.value = {
    username: '',
    email: '',
    password: '',
    role: 'USER'
  }
}

onMounted(() => {
  loadUsers()
})
</script>
