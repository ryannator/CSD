<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-6 max-w-7xl">
      <!-- Header -->
      <div class="mb-6 md:mb-8">
        <h1 class="text-2xl md:text-3xl font-bold text-gray-900 mb-2">Countries & Products</h1>
        <p class="text-sm md:text-base text-gray-600">Browse available countries and product categories for tariff calculations.</p>
      </div>

      <!-- Tabs -->
      <div class="bg-white rounded-lg shadow-md mb-6 md:mb-8">
        <div class="border-b border-gray-200">
          <nav class="flex space-x-4 md:space-x-8 px-4 md:px-6" aria-label="Tabs">
            <button
              @click="activeTab = 'countries'"
              :class="[
                'py-3 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'countries'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              <div class="flex items-center">
                <Globe class="w-4 h-4 mr-2" />
                <span class="hidden sm:inline">Countries</span>
              </div>
            </button>
            <button
              @click="activeTab = 'products'"
              :class="[
                'py-3 px-1 border-b-2 font-medium text-sm transition-colors',
                activeTab === 'products'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              <div class="flex items-center">
                <Package class="w-4 h-4 mr-2" />
                <span class="hidden sm:inline">Products & Industries</span>
              </div>
            </button>
          </nav>
        </div>

        <!-- Countries Tab Content -->
        <div v-if="activeTab === 'countries'" class="p-4 md:p-6">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-4 sm:space-y-0 mb-6">
            <h2 class="text-lg md:text-xl font-semibold text-gray-900">Available Countries</h2>
            <div class="relative w-full sm:w-auto">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search countries..."
                v-model="countrySearch"
                class="w-full sm:w-64 pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm md:text-base"
              >
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
            <div
              v-for="country in filteredCountries"
              :key="country.id"
              class="bg-white border border-gray-200 rounded-lg p-4 md:p-6 hover:shadow-md transition duration-200"
            >
              <div class="flex items-start justify-between mb-3">
                <div class="flex-1">
                  <div class="flex items-center mb-3">
                    <div class="w-6 h-6 md:w-8 md:h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                      <Globe class="w-3 h-3 md:w-4 md:h-4 text-blue-600" />
                    </div>
                    <h3 class="text-sm md:text-lg font-semibold text-gray-900">{{ country.countryName }}</h3>
                  </div>
                  <div class="space-y-2">
                    <div class="flex justify-between">
                      <span class="text-xs md:text-sm text-gray-600">Region:</span>
                      <span class="text-xs md:text-sm font-medium text-gray-900">{{ country.region }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-xs md:text-sm text-gray-600">Currency:</span>
                      <span class="text-xs md:text-sm font-medium text-gray-900">{{ country.currency }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-xs md:text-sm text-gray-600">Continent:</span>
                      <span class="text-xs md:text-sm font-medium text-gray-900">{{ country.continent }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="mt-4">
                <div class="flex items-center justify-between">
                  <span class="text-xs text-gray-500">Country Code: {{ country.countryCode }}</span>
                  <RouterLink
                    to="/calculator"
                    class="text-blue-600 hover:text-blue-900 text-xs md:text-sm font-medium"
                  >
                    Calculate Tariff →
                  </RouterLink>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Products Tab Content -->
        <div v-if="activeTab === 'products'" class="p-4 md:p-6">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-4 sm:space-y-0 mb-6">
            <h2 class="text-lg md:text-xl font-semibold text-gray-900">Product Categories & Industries</h2>
            <div class="relative w-full sm:w-auto">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search products..."
                v-model="productSearch"
                class="w-full sm:w-64 pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm md:text-base"
              >
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 md:gap-6">
            <div
              v-for="product in filteredProducts"
              :key="product.id"
              class="bg-white border border-gray-200 rounded-lg p-4 md:p-6 hover:shadow-md transition duration-200"
            >
              <div class="flex items-start justify-between mb-3">
                <div class="flex-1">
                  <div class="flex items-center mb-3">
                    <div class="w-6 h-6 md:w-8 md:h-8 bg-green-100 rounded-full flex items-center justify-center mr-3">
                      <Package class="w-3 h-3 md:w-4 md:h-4 text-green-600" />
                    </div>
                    <h3 class="text-sm md:text-lg font-semibold text-gray-900 line-clamp-2">{{ product.briefDescription }}</h3>
                  </div>
                  <div class="space-y-2">
                    <div class="flex justify-between">
                      <span class="text-xs md:text-sm text-gray-600">HTS Code:</span>
                      <span class="text-xs md:text-sm font-medium text-gray-900 font-mono">{{ product.hts8 }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-xs md:text-sm text-gray-600">Category:</span>
                      <span class="text-xs md:text-sm font-medium text-gray-900">{{ product.category }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="mt-4">
                <p class="text-xs text-gray-600 mb-3 line-clamp-2">{{ product.description }}</p>
                <div class="flex items-center justify-between">
                  <span class="text-xs text-gray-500">Product ID: {{ product.id }}</span>
                  <RouterLink
                    to="/calculator"
                    class="text-green-600 hover:text-green-900 text-xs md:text-sm font-medium"
                  >
                    Calculate Tariff →
                  </RouterLink>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Globe, Package, Search } from 'lucide-vue-next'
import client from '../api/client'

interface Country {
  id: number
  countryName: string
  countryCode: string
  region: string
  continent: string
  currency: string
}

interface ProductCategory {
  id: number
  briefDescription: string
  hts8: string
  description: string
  category: string
}

const activeTab = ref('countries')
const countrySearch = ref('')
const productSearch = ref('')

const countries = ref<Country[]>([])

const products = ref<ProductCategory[]>([])

const filteredCountries = computed(() => {
  if (!countrySearch.value) return countries.value
  return countries.value.filter(country =>
    country.countryName?.toLowerCase().includes(countrySearch.value.toLowerCase()) ||
    country.region?.toLowerCase().includes(countrySearch.value.toLowerCase()) ||
    country.continent?.toLowerCase().includes(countrySearch.value.toLowerCase())
  )
})

const filteredProducts = computed(() => {
  if (!productSearch.value) return products.value
  return products.value.filter(product =>
    product.briefDescription?.toLowerCase().includes(productSearch.value.toLowerCase()) ||
    product.description?.toLowerCase().includes(productSearch.value.toLowerCase()) ||
    product.hts8?.includes(productSearch.value)
  )
})

// Load real data from database
async function loadRealData() {
  try {
    // Load countries and products from database
    const [countriesRes, productsRes] = await Promise.all([
      client.get('/country'),
      client.get('/products')
    ])

    // Load countries data
    countries.value = countriesRes.data

    // Load products data - show actual products instead of categories
    const allProducts = productsRes.data
    // Take first 20 products to show actual product descriptions
    products.value = allProducts.slice(0, 20).map((product: { id: number; briefDescription: string; hts8: string }) => ({
      id: product.id,
      briefDescription: product.briefDescription,
      hts8: product.hts8,
      description: `HTS Code: ${product.hts8} - ${product.briefDescription}`,
      category: product.hts8?.substring(0, 2) || '00'
    }))

    console.log('Loaded real data:')
    console.log('Countries:', countries.value.length)
    console.log('Product categories:', products.value.length)

  } catch (error) {
    console.error('Error loading real data:', error)
  }
}

onMounted(() => {
  loadRealData()
})
</script>
