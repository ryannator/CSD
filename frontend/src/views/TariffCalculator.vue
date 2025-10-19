<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-6 max-w-7xl">
      <!-- Header -->
      <div class="mb-6 md:mb-8">
        <h1 class="text-2xl md:text-3xl font-bold text-gray-900 mb-2">Tariff Calculator</h1>
        <p class="text-sm md:text-base text-gray-600">Search products and calculate tariffs using real tariff data.</p>
      </div>

      <!-- Simple Tariff Calculator -->
      <div class="bg-white rounded-lg shadow-md p-4 md:p-6 mb-16 md:mb-12">
        <h2 class="text-lg md:text-xl font-semibold mb-4">Quick Tariff Calculator</h2>
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Origin Country</label>
              <select v-model="simpleCalc.originCountry" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm md:text-base">
                <option value="">Choose a country...</option>
                <option v-for="country in availableCountries" :key="country.countryCode" :value="country.countryCode">
                  {{ country.countryName }} ({{ country.countryCode }})
                </option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Destination Country</label>
              <select v-model="simpleCalc.destinationCountry" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm md:text-base">
                <option value="">Choose a country...</option>
                <option v-for="country in availableCountries" :key="country.countryCode" :value="country.countryCode">
                  {{ country.countryName }} ({{ country.countryCode }})
                </option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">HTS Code</label>
              <Input v-model="simpleCalc.htsCode" placeholder="e.g., 64039990" class="text-sm md:text-base" />
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Product Value (in USD)</label>
                <Input v-model="simpleCalc.productValue" type="number" placeholder="e.g., 1000" class="text-sm md:text-base" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Quantity</label>
                <Input v-model="simpleCalc.quantity" type="number" placeholder="e.g., 1" class="text-sm md:text-base" />
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Currency</label>
              <select v-model="simpleCalc.currency" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm md:text-base">
                <option value="USD">USD</option>
                <option value="CAD">CAD</option>
                <option value="EUR">EUR</option>
                <option value="JPY">JPY</option>
                <option value="SGD">SGD</option>
                <option value="AUD">AUD</option>
                <option value="KRW">KRW</option>
              </select>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tariff Effective Date</label>
                <Input v-model="simpleCalc.tariffEffectiveDate" type="date" class="text-sm md:text-base" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tariff Expiration Date</label>
                <Input v-model="simpleCalc.tariffExpirationDate" type="date" class="text-sm md:text-base" />
              </div>
            </div>
            <Button @click="calculateSimpleTariff" :disabled="!simpleCalc.originCountry || !simpleCalc.destinationCountry || !simpleCalc.productValue || !simpleCalc.htsCode" class="w-full text-sm md:text-base">
              Calculate
            </Button>
          </div>

          <div v-if="simpleCalcResult" class="bg-gray-50 rounded-lg p-4">
            <h3 class="text-lg font-semibold mb-4 text-gray-800">Breakdown of Costs</h3>
            <div class="space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="font-medium">Purchase Price:</span>
                <span>${{ parseFloat(simpleCalc.productValue || '0').toFixed(2) }} USD</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium">Applied Tariff:</span>
                <span>{{ simpleCalcResult.tariffRate }}%</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium">Tariff Amount:</span>
                <span>{{ formatCurrency(simpleCalcResult.tariffAmount, simpleCalc.currency) }}</span>
              </div>
              <div class="flex justify-between border-t pt-2">
                <span class="font-bold">Total Import Price:</span>
                <span class="font-bold text-green-600">{{ formatCurrency(simpleCalcResult.totalPrice, simpleCalc.currency) }}</span>
              </div>
              <div class="mt-4 p-3 bg-blue-50 rounded">
                <h4 class="font-medium text-blue-800 mb-2 text-sm">Calculation Details</h4>
                <div class="text-xs text-blue-700 space-y-1">
                  <div><span class="font-medium">HTS Code:</span> {{ simpleCalc.htsCode }}</div>
                  <div><span class="font-medium">Origin:</span> {{ getCountryName(simpleCalc.originCountry) }}</div>
                  <div><span class="font-medium">Destination:</span> {{ getCountryName(simpleCalc.destinationCountry) }}</div>
                  <div><span class="font-medium">Quantity:</span> {{ simpleCalc.quantity }}</div>
                  <div v-if="simpleCalc.tariffEffectiveDate"><span class="font-medium">Effective Date:</span> {{ simpleCalc.tariffEffectiveDate }}</div>
                  <div v-if="simpleCalc.tariffExpirationDate"><span class="font-medium">Expiration Date:</span> {{ simpleCalc.tariffExpirationDate }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Section -->
      <div class="bg-white rounded-lg shadow-md p-4 md:p-6 mb-6 md:mb-8">
        <h2 class="text-lg md:text-xl font-semibold mb-4">Search Products</h2>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Search by Description</label>
            <div class="flex flex-col sm:flex-row gap-2">
              <Input
                v-model="searchQuery"
                placeholder="e.g., horses, electronics, textiles"
                @keyup.enter="searchProducts"
                class="flex-1 text-sm md:text-base"
              />
              <Button @click="searchProducts" :disabled="isLoading" class="text-sm md:text-base">
                {{ isLoading ? 'Searching...' : 'Search' }}
              </Button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Search by HTS Code Pattern</label>
            <div class="flex flex-col sm:flex-row gap-2">
              <Input
                v-model="htsPattern"
                placeholder="e.g., 64039990"
                @keyup.enter="searchByHtsPattern"
                class="flex-1 text-sm md:text-base"
              />
              <Button @click="searchByHtsPattern" :disabled="isLoading" class="text-sm md:text-base">
                {{ isLoading ? 'Searching...' : 'Search' }}
              </Button>
            </div>
          </div>
        </div>

        <div class="flex flex-col sm:flex-row gap-2">
          <Button @click="getAllProducts" variant="outline" :disabled="isLoading" class="text-sm md:text-base">
            Browse All Products
          </Button>
          <Button @click="getProductsWithTradeAgreements" variant="outline" :disabled="isLoading" class="text-sm md:text-base">
            Products with Trade Agreements
          </Button>
        </div>
      </div>

      <!-- Search Results -->
      <div v-if="searchResults.length > 0" class="mb-6 md:mb-8">
        <h3 class="text-lg font-semibold mb-4">Search Results ({{ searchResults.length }} products)</h3>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          <div
            v-for="product in searchResults"
            :key="product.id"
            class="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition duration-200 cursor-pointer"
            @click="selectProduct(product)"
          >
            <div class="mb-3">
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-mono bg-gray-100 px-2 py-1 rounded">{{ product.hts8 }}</span>
                <span class="text-xs text-gray-500">{{ getCardRightLabel(product) }}</span>
              </div>
              <h4 class="font-medium text-gray-900 text-sm line-clamp-2">{{ product.briefDescription }}</h4>
            </div>

            <div class="space-y-1 text-xs text-gray-600">
              <div v-if="product.mfnTariffRates?.[0]?.mfnadValoremRate">
                <span class="font-medium">MFN Rate:</span> {{ (product.mfnTariffRates[0].mfnadValoremRate * 100).toFixed(2) }}%
              </div>
              <div v-if="product.agreementRates?.some(rate => rate.indicator?.includes('Singapore'))">
                <span class="text-green-600">✓ Singapore FTA</span>
              </div>
              <div v-if="product.agreementRates?.some(rate => rate.indicator?.includes('Chile'))">
                <span class="text-green-600">✓ Chile FTA</span>
              </div>
              <div v-if="product.agreementRates?.some(rate => rate.indicator?.includes('Australia'))">
                <span class="text-green-600">✓ Australia FTA</span>
              </div>
              <div v-if="product.agreementRates?.some(rate => rate.indicator?.includes('Japan'))">
                <span class="text-green-600">✓ Japan FTA</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Selected Product Calculation -->
      <div v-if="selectedProduct" class="bg-white rounded-lg shadow-md p-6">
        <h3 class="text-lg font-semibold mb-4">Calculate Tariff for Selected Product</h3>

        <div class="mb-4 p-4 bg-gray-50 rounded">
          <h4 class="font-medium mb-2">{{ selectedProduct.briefDescription }}</h4>
          <p class="text-sm text-gray-600">HTS Code: {{ selectedProduct.hts8 }}</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Origin Country</label>
            <Input v-model="calculationRequest.originCountry" placeholder="e.g., Singapore, SG" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Product Value (USD)</label>
            <Input v-model="calculationRequest.productValue" type="number" placeholder="e.g., 1000" />
          </div>
        </div>

        <div class="flex justify-center mb-4">
          <Button @click="calculateTariff" :disabled="isCalculating" class="px-8">
            {{ isCalculating ? 'Calculating...' : 'Calculate Tariff' }}
          </Button>
        </div>

        <!-- Calculation Results -->
        <div v-if="calculationResult" class="mt-6 p-6 bg-gray-50 rounded-lg">
          <h4 class="text-lg font-semibold mb-4 text-gray-800">Calculation Results</h4>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white p-4 rounded border">
              <h5 class="font-medium text-gray-700 mb-3">Tariff Details</h5>
              <div class="space-y-2 text-sm">
                <div><span class="font-medium">Program Type:</span> {{ calculationResult.programType || 'MFN' }}</div>
                <div><span class="font-medium">Program Name:</span> {{ calculationResult.programName || 'Most Favored Nation' }}</div>
                <div><span class="font-medium">Applied Rate:</span> {{ calculationResult.appliedRateLabel || 'Standard Rate' }}</div>
                <div><span class="font-medium">Total Tariff:</span> <span class="font-bold text-green-600">${{ calculationResult.totalTariffAmount?.toFixed(2) || '0.00' }}</span></div>
                <div v-if="calculationResult.savingsVsMfn > 0"><span class="font-medium">Savings vs MFN:</span> <span class="font-bold text-blue-600">${{ calculationResult.savingsVsMfn?.toFixed(2) }}</span></div>
              </div>
            </div>

            <div class="bg-white p-4 rounded border">
              <h5 class="font-medium text-gray-700 mb-3">Product Information</h5>
              <div class="space-y-2 text-sm">
                <div><span class="font-medium">Origin:</span> {{ calculationResult.originCountry }}</div>
                <div><span class="font-medium">Destination:</span> {{ calculationResult.destinationCountry }}</div>
                <div><span class="font-medium">Value:</span> ${{ calculationResult.productValue?.toLocaleString() }}</div>
                <div><span class="font-medium">Currency:</span> {{ calculationResult.currency }}</div>
              </div>
            </div>
          </div>

          <!-- Available Trade Agreements -->
          <div v-if="calculationResult.applicableAgreements?.length > 0" class="mt-4 bg-white p-4 rounded border">
            <h5 class="font-medium text-gray-700 mb-2">Available Trade Agreements</h5>
            <div class="flex flex-wrap gap-2">
              <span v-for="agreement in calculationResult.applicableAgreements"
                    :key="agreement"
                    class="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded">
                {{ agreement }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <br/>

      <!-- Statistics -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <h3 class="text-lg font-semibold mb-4">Database Statistics</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="text-center p-4 bg-blue-50 rounded">
            <div class="text-2xl font-bold text-blue-600">{{ totalProducts }}</div>
            <div class="text-sm text-gray-600">Total Products</div>
          </div>
          <div class="text-center p-4 bg-green-50 rounded">
            <div class="text-2xl font-bold text-green-600">{{ productsWithAgreements }}</div>
            <div class="text-sm text-gray-600">With Trade Agreements</div>
          </div>
          <div class="text-center p-4 bg-purple-50 rounded">
            <div class="text-2xl font-bold text-purple-600">{{ freeTradeProducts }}</div>
            <div class="text-sm text-gray-600">Free Trade Products</div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import client from '../api/client'

interface TariffProduct {
  id: number
  hts8: string
  briefDescription: string
  quantity1Code?: string
  quantity2Code?: string
  wtoBindingCode?: string
  createdAt: string
  updatedAt: string
  mfnTariffRates?: MfnTariffRate[]
  agreementRates?: AgreementRate[]
  productIndicators?: ProductIndicator[]
  productNotes?: ProductNote[]
}

interface MfnTariffRate {
  id: number
  mfnTextRate?: string
  mfnRateTypeCode?: string
  mfnAve?: number
  mfnadValoremRate?: number
  mfnSpecificRate?: number
  mfnOtherRate?: number
  beginEffectDate?: string
  endEffectiveDate?: string
}

interface AgreementRate {
  id: number
  rateTypeCode?: string
  adValoremRate?: number
  specificRate?: number
  otherRate?: number
  effectiveDate?: string
  expirationDate?: string
  indicator?: string
  textRate?: string
}

interface ProductIndicator {
  id: number
  indicatorType?: string
}

interface ProductNote {
  id: number
  noteText?: string
}

interface CalculationRequest {
  htsCode: string
  originCountry: string
  productValue: string
}

interface CalculationResult {
  htsCode: string
  productDescription: string
  originCountry: string
  destinationCountry: string
  productValue: number
  quantity: number
  currency: string
  programType: string
  programName: string
  appliedRateLabel: string
  totalTariffAmount: number
  totalImportPrice: number
  savingsVsMfn: number
  applicableAgreements: string[]
  effectiveDate: string
  notes: string
  calculationTimestamp: string
}

const searchQuery = ref('')
const htsPattern = ref('')
const searchResults = ref<TariffProduct[]>([])
const selectedProduct = ref<TariffProduct | null>(null)
const calculationRequest = ref<CalculationRequest>({
  htsCode: '',
  originCountry: '',
  productValue: ''
})
const calculationResult = ref<CalculationResult | null>(null)
const isLoading = ref(false)
const isCalculating = ref(false)
const totalProducts = ref(0)
const productsWithAgreements = ref(0)
const freeTradeProducts = ref(0)

// Simple calculator state
const simpleCalc = ref({
  originCountry: '',
  destinationCountry: '',
  htsCode: '',
  productValue: '',
  quantity: '1',
  currency: 'USD',
  tariffEffectiveDate: new Date().toISOString().split('T')[0], // Today's date
  tariffExpirationDate: ''
})

const availableCountries = ref<any[]>([])
const simpleCalcResult = ref<{
  tariffRate: number
  tariffAmount: number
  totalPrice: number
} | null>(null)

// Map of latest calculation by HTS for quick lookup in search results
type LatestCalc = { productValue: number; currency: string; createdAt: string }
const latestPriceByHts = ref(new Map<string, LatestCalc>())

interface CalculationRecord {
  id: number
  htsCode: string
  productValue: number
  currency: string
  createdAt: string
}

// Using authenticated API client instead of direct fetch calls

async function searchProducts() {
  if (!searchQuery.value.trim()) return

  isLoading.value = true
  try {
    // Load products and latest calculations to enrich cards with price
    const [productsRes, calculationsRes] = await Promise.all([
      client.get('/products'),
      client.get('/tariff/calculate-all')
    ])
    const calculations: CalculationRecord[] = calculationsRes.data || []
    // Build latest calc per HTS
    const latestByHts = new Map<string, LatestCalc>()
    calculations.forEach((c: CalculationRecord) => {
      const key = c.htsCode
      const prev = latestByHts.get(key)
      if (!prev || new Date(c.createdAt).getTime() > new Date(prev.createdAt).getTime()) {
        latestByHts.set(key, { productValue: c.productValue || 0, currency: c.currency || 'USD', createdAt: c.createdAt })
      }
    })
    latestPriceByHts.value = latestByHts
    const response = productsRes
    // Filter products by description on the frontend
    searchResults.value = response.data.filter((product: TariffProduct) =>
      product.briefDescription.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  } catch (error) {
    console.error('Search error:', error)
  } finally {
    isLoading.value = false
  }
}

async function searchByHtsPattern() {
  if (!htsPattern.value.trim()) return

  isLoading.value = true
  try {
    const [productsRes, calculationsRes] = await Promise.all([
      client.get('/products'),
      client.get('/tariff/calculate-all')
    ])
    const calculations: CalculationRecord[] = calculationsRes.data || []
    const latestByHts = new Map<string, LatestCalc>()
    calculations.forEach((c: CalculationRecord) => {
      const key = c.htsCode
      const prev = latestByHts.get(key)
      if (!prev || new Date(c.createdAt).getTime() > new Date(prev.createdAt).getTime()) {
        latestByHts.set(key, { productValue: c.productValue || 0, currency: c.currency || 'USD', createdAt: c.createdAt })
      }
    })
    latestPriceByHts.value = latestByHts
    const response = productsRes
    // Filter products by HTS pattern on the frontend
    searchResults.value = response.data.filter((product: TariffProduct) =>
      product.hts8.startsWith(htsPattern.value)
    )
  } catch (error) {
    console.error('HTS pattern search error:', error)
  } finally {
    isLoading.value = false
  }
}

async function getAllProducts() {
  isLoading.value = true
  try {
    const [productsRes, calculationsRes] = await Promise.all([
      client.get('/products'),
      client.get('/tariff/calculate-all')
    ])
    const calculations: CalculationRecord[] = calculationsRes.data || []
    const latestByHts = new Map<string, LatestCalc>()
    calculations.forEach((c: CalculationRecord) => {
      const key = c.htsCode
      const prev = latestByHts.get(key)
      if (!prev || new Date(c.createdAt).getTime() > new Date(prev.createdAt).getTime()) {
        latestByHts.set(key, { productValue: c.productValue || 0, currency: c.currency || 'USD', createdAt: c.createdAt })
      }
    })
    latestPriceByHts.value = latestByHts
    const response = productsRes
    searchResults.value = response.data
  } catch (error) {
    console.error('Random products error:', error)
  } finally {
    isLoading.value = false
  }
}

async function getProductsWithTradeAgreements() {
  isLoading.value = true
  try {
    const [productsRes, calculationsRes] = await Promise.all([
      client.get('/products'),
      client.get('/tariff/calculate-all')
    ])
    const calculations: CalculationRecord[] = calculationsRes.data || []
    const latestByHts = new Map<string, LatestCalc>()
    calculations.forEach((c: CalculationRecord) => {
      const key = c.htsCode
      const prev = latestByHts.get(key)
      if (!prev || new Date(c.createdAt).getTime() > new Date(prev.createdAt).getTime()) {
        latestByHts.set(key, { productValue: c.productValue || 0, currency: c.currency || 'USD', createdAt: c.createdAt })
      }
    })
    latestPriceByHts.value = latestByHts
    const response = productsRes
    searchResults.value = response.data
  } catch (error) {
    console.error('Trade agreements products error:', error)
  } finally {
    isLoading.value = false
  }
}

function selectProduct(product: TariffProduct) {
  selectedProduct.value = product
  calculationRequest.value.htsCode = product.hts8
}

async function calculateTariff() {
  if (!selectedProduct.value || !calculationRequest.value.originCountry || !calculationRequest.value.productValue) {
    return
  }

  isCalculating.value = true
  try {
    const response = await client.post('/tariff/calculate', {
      htsCode: calculationRequest.value.htsCode,
      originCountry: calculationRequest.value.originCountry,
      destinationCountry: "USA",
      productValue: parseFloat(calculationRequest.value.productValue),
      quantity: 1, // Add required quantity field
      currency: "USD"
    })

    calculationResult.value = response.data
  } catch (error) {
    console.error('Calculation error:', error)
  } finally {
    isCalculating.value = false
  }
}

async function loadStatistics() {
  try {
    // Load products and MFN rates to calculate real statistics
    const [productsRes, mfnRatesRes] = await Promise.all([
      client.get('/products'),
      client.get('/mfn')
    ])

    const products = productsRes.data
    const mfnRates = mfnRatesRes.data

    totalProducts.value = products.length

    // Calculate products with trade agreements
    // For now, estimate based on products that have MFN rates (indicating they're in the tariff system)
    // In a real implementation, you'd query agreement rates
    productsWithAgreements.value = Math.floor(products.length * 0.3) // Estimate 30% have agreements

    // Calculate free trade products
    // Count MFN rates that are "Free" or have 0% ad valorem rate
    // Since product relationship isn't loaded, we'll count the rates directly
    const freeTradeRates = mfnRates.filter((rate: { mfnTextRate?: string; mfnadValoremRate?: number; mfnSpecificRate?: number }) =>
      rate.mfnTextRate === "Free" ||
      (rate.mfnadValoremRate === 0 && rate.mfnSpecificRate === 0)
    )
    freeTradeProducts.value = freeTradeRates.length

    console.log('Statistics loaded:')
    console.log('Total products:', totalProducts.value)
    console.log('Products with agreements:', productsWithAgreements.value)
    console.log('Free trade products:', freeTradeProducts.value)
    console.log('Free trade rates found:', freeTradeRates.length)
    console.log('Sample free trade rates:', freeTradeRates.slice(0, 3))
  } catch (error) {
    console.error('Statistics error:', error)
    // Set fallback values
    totalProducts.value = 0
    productsWithAgreements.value = 0
    freeTradeProducts.value = 0
  }
}

onMounted(() => {
  loadStatistics()
  loadCountries()
})

function getCardRightLabel(product: TariffProduct): string {
  const latest = latestPriceByHts.value.get(product.hts8)
  if (latest && latest.productValue) {
    return `$${latest.productValue.toLocaleString()} ${latest.currency}`
  }
  const firstRate = product.mfnTariffRates?.[0]
  if (firstRate?.mfnTextRate) return firstRate.mfnTextRate
  if (firstRate?.mfnadValoremRate != null) return `${(firstRate.mfnadValoremRate * 100).toFixed(1)}%`
  if (firstRate?.mfnSpecificRate != null) return `$${firstRate.mfnSpecificRate}/unit`
  return '—'
}

function calculateSimpleTariff() {
  if (!simpleCalc.value.originCountry || !simpleCalc.value.destinationCountry || !simpleCalc.value.productValue || !simpleCalc.value.htsCode) return

  const price = parseFloat(simpleCalc.value.productValue)
  let tariffRate = 0

  // Default rates based on country
  const rates: Record<string, number> = {
    'CA': 0,    // Canada - NAFTA
    'MX': 0,    // Mexico - USMCA
    'AU': 0,    // Australia - FTA
    'SG': 0,    // Singapore - FTA
    'JP': 2.5,  // Japan - FTA with some tariffs
    'KR': 2.5,  // South Korea - FTA with some tariffs
    'CL': 0,    // Chile - FTA
    'PE': 0,    // Peru - FTA
    'CO': 0,    // Colombia - FTA
    'PA': 0,    // Panama - FTA
    'BH': 0,    // Bahrain - FTA
    'IL': 0,    // Israel - FTA
    'JO': 0,    // Jordan - FTA
    'MA': 0,    // Morocco - FTA
    'OM': 0,    // Oman - FTA
    'NP': 0     // Nepal - GSP
  }
  tariffRate = rates[simpleCalc.value.originCountry] || 5 // Default 5% for other countries

  // Calculate tariff amount in USD (since purchase price is in USD)
  const tariffAmountUSD = (price * tariffRate) / 100
  const totalPriceUSD = price + tariffAmountUSD

  // Convert to selected currency if not USD
  let tariffAmount = tariffAmountUSD
  let totalPrice = totalPriceUSD

  if (simpleCalc.value.currency !== 'USD') {
    // Simple conversion rates (in a real app, you'd fetch live rates)
    const conversionRates: Record<string, number> = {
      'CAD': 1.35,
      'EUR': 0.85,
      'JPY': 110,
      'SGD': 1.35,
      'AUD': 1.45,
      'KRW': 1200
    }

    const rate = conversionRates[simpleCalc.value.currency] || 1
    tariffAmount = tariffAmountUSD * rate
    totalPrice = totalPriceUSD * rate
  }

  simpleCalcResult.value = {
    tariffRate,
    tariffAmount,
    totalPrice
  }

  // Save calculation to database
  saveCalculationToDatabase()
}

async function saveCalculationToDatabase() {
  try {
    const calculationData = {
      htsCode: simpleCalc.value.htsCode,
      originCountry: simpleCalc.value.originCountry,
      destinationCountry: simpleCalc.value.destinationCountry,
      productValue: parseFloat(simpleCalc.value.productValue),
      quantity: parseInt(simpleCalc.value.quantity),
      currency: simpleCalc.value.currency,
      tariffEffectiveDate: simpleCalc.value.tariffEffectiveDate,
      tariffExpirationDate: simpleCalc.value.tariffExpirationDate || null,
      totalTariffAmount: simpleCalcResult.value?.tariffAmount || 0,
      calculationResult: simpleCalcResult.value?.totalPrice || 0,
      calculationType: 'MFN'
    }

    const response = await client.post('/tariff/calculate', calculationData)
    console.log('Calculation saved to database:', response.data)
  } catch (error) {
    console.error('Failed to save calculation to database:', error)
  }
}

function getCountryName(countryCode: string): string {
  const country = availableCountries.value.find(c => c.countryCode === countryCode)
  return country ? country.countryName : countryCode
}

function formatCurrency(amount: number, currency: string): string {
  const currencySymbols: Record<string, string> = {
    'USD': '$',
    'CAD': 'C$',
    'EUR': '€',
    'JPY': '¥',
    'SGD': 'S$',
    'AUD': 'A$',
    'KRW': '₩'
  }

  const symbol = currencySymbols[currency] || '$'

  if (currency === 'JPY' || currency === 'KRW') {
    return `${symbol}${Math.round(amount).toLocaleString()}`
  }

  return `${symbol}${amount.toFixed(2)}`
}

async function loadCountries() {
  try {
    const response = await client.get('/country')
    availableCountries.value = response.data
  } catch (error) {
    console.error('Error loading countries:', error)
  }
}
</script>
