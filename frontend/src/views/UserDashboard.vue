<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-6 max-w-7xl">
      <!-- Header -->
      <div class="mb-6 md:mb-8">
        <h1 class="text-2xl md:text-3xl font-bold text-gray-900 mb-2">User Dashboard</h1>
        <p class="text-sm md:text-base text-gray-600">Manage your tariff calculations and view your calculation history.</p>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 md:gap-6 mb-6 md:mb-8">
        <div class="bg-white p-4 md:p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-2 md:p-3 rounded-full bg-blue-100 text-blue-600">
              <Calculator class="w-5 h-5 md:w-6 md:h-6" />
            </div>
            <div class="ml-3 md:ml-4">
              <p class="text-xs md:text-sm font-medium text-gray-600">Total Calculations</p>
              <p class="text-xl md:text-2xl font-bold text-gray-900">{{ stats.totalCalculations }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-4 md:p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-2 md:p-3 rounded-full bg-yellow-100 text-yellow-600">
              <Globe class="w-5 h-5 md:w-6 md:h-6" />
            </div>
            <div class="ml-3 md:ml-4">
              <p class="text-xs md:text-sm font-medium text-gray-600">Countries Used</p>
              <p class="text-xl md:text-2xl font-bold text-gray-900">{{ stats.countriesUsed }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-4 md:p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-2 md:p-3 rounded-full bg-purple-100 text-purple-600">
              <Package class="w-5 h-5 md:w-6 md:h-6" />
            </div>
            <div class="ml-3 md:ml-4">
              <p class="text-xs md:text-sm font-medium text-gray-600">Product Categories</p>
              <p class="text-xl md:text-2xl font-bold text-gray-900">{{ stats.productCategories }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-4 md:p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-2 md:p-3 rounded-full bg-green-100 text-green-600">
              <DollarSign class="w-5 h-5 md:w-6 md:h-6" />
            </div>
            <div class="ml-3 md:ml-4">
              <p class="text-xs md:text-sm font-medium text-gray-600">Total Tariffs (USD)</p>
              <p class="text-xl md:text-2xl font-bold text-gray-900">${{ stats.totalTariffs.toFixed(2) }}</p>
            </div>
          </div>
        </div>
      </div>

      <br/>

      <!-- Quick Actions -->
      <div class="bg-white rounded-lg shadow-md p-4 md:p-6 mb-6 md:mb-8">
        <h2 class="text-lg md:text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <RouterLink
            to="/calculator"
            class="flex items-center p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition duration-200"
          >
            <Calculator class="w-6 h-6 md:w-8 md:h-8 text-blue-600 mr-3" />
            <div>
              <h3 class="font-medium text-gray-900 text-sm md:text-base">New Calculation</h3>
              <p class="text-xs md:text-sm text-gray-600">Calculate tariffs for a new product</p>
            </div>
          </RouterLink>

          <button @click="showAllCalculations = true" class="flex items-center p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition duration-200">
            <History class="w-6 h-6 md:w-8 md:h-8 text-green-600 mr-3" />
            <div>
              <h3 class="font-medium text-gray-900 text-sm md:text-base">View History</h3>
              <p class="text-xs md:text-sm text-gray-600">Review past calculations</p>
            </div>
          </button>
        </div>
      </div>

      <br/>

      <!-- Recent Calculations -->
      <div class="bg-white rounded-lg shadow-md">
        <div class="p-4 md:p-6 border-b border-gray-200">
          <h2 class="text-lg md:text-xl font-semibold text-gray-900">Recent Calculations</h2>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">HTS Code</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin Country</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Destination Country</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Value</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Currency</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tariff</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="calculation in recentCalculations" :key="calculation.id">
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ new Date(calculation.createdAt).toLocaleDateString() }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900 font-mono">{{ calculation.htsCode }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.originCountry || calculation.countryCode }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.destinationCountry || 'US' }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">${{ calculation.productValue.toLocaleString() }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.currency }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm font-medium text-green-600">${{ calculation.totalTariffAmount.toFixed(2) }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-500">
                  <button class="text-blue-600 hover:text-blue-900">View</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- All Calculations Modal -->
    <div v-if="showAllCalculations" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg shadow-xl max-w-7xl w-full max-h-[90vh] overflow-hidden">
        <div class="p-4 md:p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h2 class="text-lg md:text-xl font-semibold text-gray-900">All Calculations</h2>
            <button @click="showAllCalculations = false" class="text-gray-400 hover:text-gray-600">
              <svg class="w-5 h-5 md:w-6 md:h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>
        <div class="overflow-x-auto max-h-[70vh]">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50 sticky top-0">
              <tr>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">HTS Code</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin Country</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Destination Country</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Value</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Currency</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tariff</th>
                <th class="px-3 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="calculation in allCalculations" :key="calculation.id">
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ new Date(calculation.createdAt).toLocaleDateString() }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900 font-mono">{{ calculation.htsCode }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.originCountry || calculation.countryCode }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.destinationCountry || 'US' }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">${{ calculation.productValue.toLocaleString() }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-900">{{ calculation.currency }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm font-medium text-green-600">${{ calculation.totalTariffAmount.toFixed(2) }}</td>
                <td class="px-3 md:px-6 py-4 whitespace-nowrap text-xs md:text-sm text-gray-500">
                  <button class="text-blue-600 hover:text-blue-900">View</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Calculator, DollarSign, Globe, Package, History, Download } from 'lucide-vue-next'
import client from '../api/client'

interface CalculationHistory {
  id: number
  createdAt: string
  htsCode: string
  originCountry?: string
  destinationCountry?: string
  countryCode: string
  productValue: number
  totalTariffAmount: number
  calculationResult: number
  currency: string
  calculationType: string
}

const stats = ref({
  totalCalculations: 0,
  totalTariffs: 0,
  countriesUsed: 0,
  productCategories: 0
})

const recentCalculations = ref<CalculationHistory[]>([])
const allCalculations = ref<CalculationHistory[]>([])
const showAllCalculations = ref(false)
const isLoading = ref(true)

onMounted(async () => {
  try {
    // Load real data from the database including calculations
    // const [productsRes, countriesRes, calculationsRes] = await Promise.all([
    //   client.get('/products'),
    //   client.get('/country'),
    //   client.get('/tariff/calculate-all')
    // ])
    const calculationsRes = await client.get('/tariff/calculate-all')

    // const products = productsRes.data
    // const countries = countriesRes.data
    const calculations = calculationsRes.data

    // Calculate stats from real calculation data
    const totalCalculations = calculations.length
    const totalTariffs = calculations.reduce((sum: number, calc: any) => sum + (calc.totalTariffAmount || 0), 0)
    const countriesUsed = new Set(calculations.map((calc: any) => calc.countryCode)).size
    // const productCategories = new Set(products.map((p: any) => p.hts8?.substring(0, 2))).size
    const productCategories = new Set(
      calculations
        .map((calc: any) => calc.htsCode)
        .filter((htsCode: string | null | undefined) => Boolean(htsCode))
        .map((htsCode: string) => htsCode.substring(0, 2))
    ).size

    // Update stats with real data
    stats.value = {
      totalCalculations,
      totalTariffs,
      countriesUsed,
      productCategories
    }

           // Load recent calculations from database (last 10)
           const allCalcs = calculations
             .reverse()
             .map((calc: any) => ({
               id: calc.id,
               createdAt: calc.createdAt,
               htsCode: calc.htsCode,
               originCountry: calc.originCountry,
               destinationCountry: calc.destinationCountry,
               countryCode: calc.countryCode,
               productValue: calc.productValue,
               totalTariffAmount: calc.totalTariffAmount,
               calculationResult: calc.calculationResult,
               currency: calc.currency,
               calculationType: calc.calculationType
             }))

    allCalculations.value = allCalcs
    recentCalculations.value = allCalcs.slice(0, 10)

    console.log('User Dashboard loaded with real data:')
    console.log('Total calculations:', totalCalculations)
    console.log('Total tariffs:', totalTariffs)
    console.log('Countries used:', countriesUsed)
    console.log('Recent calculations:', recentCalculations.value.length)

  } catch (error) {
    console.error('Failed to load user dashboard data:', error)
    // Fallback to demo data if API fails
    stats.value = {
      totalCalculations: 0,
      totalTariffs: 0,
      countriesUsed: 0,
      productCategories: 0
    }

    recentCalculations.value = []
  } finally {
    isLoading.value = false
  }
})
</script>
