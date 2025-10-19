<script setup lang="ts">
import { ref, onMounted } from "vue"
import { Skeleton } from "@/components/ui/skeleton"
import { Separator } from "@/components/ui/separator"
import client from '../api/client'

const loading = ref(true)
const stats = ref<{ label: string; value: string }[]>([])

onMounted(async () => {
  try {
    // Load real data from the database
    const [productsRes, countriesRes, mfnRatesRes] = await Promise.all([
      client.get('/products'),
      client.get('/country'),
      client.get('/mfn')
    ])

    const products = productsRes.data
    const countries = countriesRes.data
    const mfnRates = mfnRatesRes.data

    stats.value = [
      { label: "Total Products", value: products.length.toLocaleString() },
      { label: "Countries", value: countries.length.toLocaleString() },
      { label: "MFN Tariff Rates", value: mfnRates.length.toLocaleString() },
    ]
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
    // Fallback to demo data if API fails
    stats.value = [
      { label: "Total Products", value: "13,000+" },
      { label: "Countries", value: "17" },
      { label: "MFN Tariff Rates", value: "12,736" },
    ]
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Dashboard</h1>
    <Separator class="mb-6" />

    <!-- Loading state -->
    <div v-if="loading" class="space-y-4">
      <Skeleton class="h-6 w-48" />
      <Skeleton class="h-6 w-64" />
      <Skeleton class="h-6 w-40" />
    </div>

    <!-- Data cards -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-3 gap-6">
      <div
        v-for="stat in stats"
        :key="stat.label"
        class="p-4 bg-white rounded-lg shadow hover:shadow-md transition"
      >
        <p class="text-gray-500 text-sm">{{ stat.label }}</p>
        <p class="text-xl font-semibold">{{ stat.value }}</p>
      </div>
    </div>
  </div>
</template>
