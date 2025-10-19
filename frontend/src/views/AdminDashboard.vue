<template>
  <div class="container mx-auto p-6">
    <div class="max-w-7xl mx-auto">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Admin Dashboard</h1>
        <p class="text-gray-600">Manage tariff rates, countries, products, and trade agreements.</p>
      </div>

      <div v-if="loadError" class="mb-6 p-4 rounded-md bg-red-50 text-red-700 text-sm">
        {{ loadError }}
      </div>

      <div v-if="isLoading" class="mb-6 p-4 rounded-md bg-blue-50 text-blue-700 text-sm">
        Loading dashboard data...
      </div>

      <!-- Admin Stats -->
      <!-- <div class="grid grid-cols-1 md:grid-cols-5 gap-6 mb-8"> -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-6 mb-8">
        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600">
              <Users class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Users</p>
              <p class="text-2xl font-bold text-gray-900">{{ adminStats.totalUsers }}</p>
              <router-link
                to="/user-management"
                class="text-xs text-blue-600 hover:text-blue-800 underline"
              >
                Manage Users
              </router-link>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600">
              <Globe class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Countries</p>
              <p class="text-2xl font-bold text-gray-900">{{ adminStats.totalCountries }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
              <Package class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Products</p>
              <p class="text-2xl font-bold text-gray-900">{{ adminStats.totalProducts }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-purple-100 text-purple-600">
              <FileText class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Trade Agreements</p>
              <p class="text-2xl font-bold text-gray-900">{{ adminStats.totalAgreements }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-red-100 text-red-600">
              <Calculator class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Calculations Today</p>
              <p class="text-2xl font-bold text-gray-900">{{ adminStats.calculationsToday }}</p>
            </div>
          </div>
        </div>
      </div>

      <br/><br/>

      <!-- Management Sections -->
      <!-- <div class="grid grid-cols-1 lg:grid-cols-2 gap-8"> -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-8">

        <!-- Country Management -->
        <!-- <div class="bg-white rounded-lg shadow-md"> -->
        <section class="bg-white rounded-lg shadow-md flex flex-col">
          <div class="p-6 border-b border-gray-200">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-semibold text-gray-900">Country Management</h2>
              <button
                @click="openCountryModalForCreate"
                class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition duration-200 text-sm"
              >
                Add Country
              </button>
            </div>
          </div>
          <!-- <div class="p-6">
            <div class="space-y-4">
              <div v-for="country in countries" :key="country.id" class="flex items-center justify-between p-4 border border-gray-200 rounded-md"> -->
          <div class="p-6 flex-1">
            <div class="space-y-4 max-h-80 overflow-y-auto pr-2">
              <div
                v-for="country in countries"
                :key="country.id"
                class="flex items-start justify-between gap-4 p-4 border border-gray-200 rounded-md"
              >
                <div>
                  <h3 class="font-medium text-gray-900">{{ country.displayName }}</h3>
                  <p
                    v-if="country.countryNameShort && country.countryNameShort !== country.countryName"
                    class="text-xs text-gray-500"
                  >
                    Short name: {{ country.countryNameShort }}
                  </p>
                  <p class="text-sm text-gray-600">
                    Code: {{ country.countryCode || 'N/A' }}
                    <span v-if="country.currency"> • Currency: {{ country.currency }}</span>
                  </p>
                  <p v-if="country.region || country.continent" class="text-xs text-gray-500">
                    {{ [country.region, country.continent].filter(Boolean).join(' • ') }}
                  </p>
                </div>
                <!-- <div class="flex space-x-2">
                  <button class="text-blue-600 hover:text-blue-900 text-sm">Edit</button>
                  <button class="text-red-600 hover:text-red-900 text-sm">Delete</button> -->
                <div class="flex shrink-0 space-x-2">
                  <button class="text-blue-600 hover:text-blue-900 text-sm" @click="openCountryModalForEdit(country)">
                    Edit
                  </button>
                  <button class="text-red-600 hover:text-red-900 text-sm" @click="promptCountryDeletion(country)">
                    Delete
                  </button>
                </div>
              </div>
              <p v-if="!countries.length && !isLoading" class="text-sm text-gray-500">
                No countries available yet.
              </p>
            </div>
          </div>
        <!-- </div> -->
        </section>

        <!-- Product Management -->
        <section class="bg-white rounded-lg shadow-md flex flex-col">
          <div class="p-6 border-b border-gray-200">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-semibold text-gray-900">Product Management</h2>
              <button
                @click="openProductModalForCreate"
                class="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition duration-200 text-sm"
              >
                Add Product
              </button>
            </div>
          </div>
          <div class="p-6 flex-1">
            <div class="space-y-4 max-h-80 overflow-y-auto pr-2">
              <div
                v-for="product in products"
                :key="product.id"
                class="flex items-start justify-between gap-4 p-4 border border-gray-200 rounded-md"
              >
                <div>
                  <h3 class="font-medium text-gray-900">{{ product.displayName }}</h3>
                  <p class="text-sm text-gray-600">HTS Code: {{ product.hts8 || 'N/A' }}</p>
                  <p v-if="product.wtoBindingCode" class="text-sm text-gray-600">
                    WTO Binding: {{ product.wtoBindingCode }}
                  </p>
                  <p v-if="product.quantityDisplay" class="text-xs text-gray-500">
                    Units: {{ product.quantityDisplay }}
                  </p>
                </div>
                <div class="flex shrink-0 space-x-2">
                  <button class="text-blue-600 hover:text-blue-900 text-sm" @click="openProductModalForEdit(product)">
                    Edit
                  </button>
                  <button class="text-red-600 hover:text-red-900 text-sm" @click="promptProductDeletion(product)">
                    Delete
                  </button>
                </div>
              </div>
              <p v-if="!products.length && !isLoading" class="text-sm text-gray-500">
                No products available yet.
              </p>
            </div>
          </div>
         </section>

        <!-- Trade Agreements -->
        <section class="bg-white rounded-lg shadow-md flex flex-col">
          <div class="p-6 border-b border-gray-200">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-semibold text-gray-900">Trade Agreements</h2>
              <button
                @click="openAgreementModalForCreate"
                class="bg-purple-600 text-white px-4 py-2 rounded-md hover:bg-purple-700 transition duration-200 text-sm"
              >
                Add Agreement
              </button>
            </div>
          </div>
           <div class="p-6 flex-1">
            <div class="space-y-4 max-h-80 overflow-y-auto pr-2">
              <div
                v-for="agreement in tradeAgreements"
                :key="agreement.id"
                class="flex items-start justify-between gap-4 p-4 border border-gray-200 rounded-md"
              >
                <div>
                  <h3 class="font-medium text-gray-900">{{ agreement.displayName }}</h3>
                  <p class="text-sm text-gray-600">Code: {{ agreement.agreementCode }}</p>
                  <p v-if="agreement.agreementType" class="text-sm text-gray-600">
                    Type: {{ agreement.agreementType }}
                    <span v-if="agreement.isMultilateral" class="ml-1 text-xs uppercase tracking-wide text-purple-600">
                      • Multilateral
                    </span>
                  </p>
                  <p v-if="agreement.dateRange" class="text-xs text-gray-500">{{ agreement.dateRange }}</p>
                </div>
                <div class="flex shrink-0 space-x-2">
                  <button class="text-blue-600 hover:text-blue-900 text-sm" @click="openAgreementModalForEdit(agreement)">
                    Edit
                  </button>
                  <button class="text-red-600 hover:text-red-900 text-sm" @click="promptAgreementDeletion(agreement)">
                    Delete
                  </button>
                </div>
              </div>
              <p v-if="!tradeAgreements.length && !isLoading" class="text-sm text-gray-500">
                No trade agreements available yet.
              </p>
            </div>
          </div>
        </section>

        <!-- Recent Activity -->
        <section class="bg-white rounded-lg shadow-md flex flex-col">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-xl font-semibold text-gray-900">Recent User Activity</h2>
          </div>
          <div class="p-6 flex-1">
            <div class="space-y-4 max-h-80 overflow-y-auto pr-2">
              <div
                v-for="activity in userActivity"
                :key="activity.id"
                class="flex items-center justify-between p-4 border border-gray-200 rounded-md"
              >
                <div>
                  <h3 class="font-medium text-gray-900">{{ activity.username }}</h3>
                  <p class="text-sm text-gray-600">{{ activity.action }}</p>
                  <p class="text-xs text-gray-500">{{ activity.timestamp }}</p>
                </div>
                <span
                  :class="activity.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'"
                  class="px-2 py-1 rounded-full text-xs font-medium"
                >
                  {{ activity.status }}
                </span>
              </div>
              <p v-if="!userActivity.length && !isLoading" class="text-sm text-gray-500">
                No recent activity recorded yet.
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
  <!-- Country Modal -->
  <div
    v-if="showAddCountryModal"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="closeCountryModal"
  >
    <div class="w-full max-w-xl rounded-lg bg-white shadow-xl">
      <div class="flex items-start justify-between border-b border-gray-200 px-6 py-4">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">
            {{ isEditingCountry ? 'Edit Country' : 'Add Country' }}
          </h3>
          <p class="text-sm text-gray-500">
            {{ isEditingCountry ? 'Update the selected country details.' : 'Create a new country profile for tariff comparisons.' }}
          </p>
        </div>
        <button
          type="button"
          class="text-xl leading-none text-gray-400 hover:text-gray-600"
          @click="closeCountryModal"
        >
          ×
        </button>
      </div>
      <form class="px-6 py-5" @submit.prevent="submitCountry">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <label class="block text-sm font-medium text-gray-700">
            Country Name
            <input
              v-model="countryForm.countryName"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Singapore"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Country Code
            <input
              v-model="countryForm.countryCode"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="SGP"
              maxlength="3"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Short Name
            <input
              v-model="countryForm.countryNameShort"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Singapore"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Region
            <input
              v-model="countryForm.region"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Southeast Asia"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Continent
            <input
              v-model="countryForm.continent"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Asia"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Currency
            <input
              v-model="countryForm.currency"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="SGD"
              maxlength="3"
            />
          </label>
        </div>
        <p v-if="countryFormError" class="mt-3 text-sm text-red-600">{{ countryFormError }}</p>
        <div class="mt-6 flex justify-end space-x-3">
          <button
            type="button"
            class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="closeCountryModal"
            :disabled="isSubmittingCountry"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-blue-300"
            :disabled="isSubmittingCountry"
          >
            {{ isSubmittingCountry ? 'Saving…' : isEditingCountry ? 'Save Changes' : 'Save Country' }}
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- Product Modal -->
  <div
    v-if="showAddProductModal"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="closeProductModal"
  >
    <div class="w-full max-w-2xl rounded-lg bg-white shadow-xl">
      <div class="flex items-start justify-between border-b border-gray-200 px-6 py-4">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">
            {{ isEditingProduct ? 'Edit Product' : 'Add Product' }}
          </h3>
          <p class="text-sm text-gray-500">
            {{ isEditingProduct ? 'Update the product information for tariff calculations.' : 'Register a new product so tariff rates can be attached to it.' }}
          </p>
        </div>
        <button
          type="button"
          class="text-xl leading-none text-gray-400 hover:text-gray-600"
          @click="closeProductModal"
        >
          ×
        </button>
      </div>
      <form class="px-6 py-5" @submit.prevent="submitProduct">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <label class="block text-sm font-medium text-gray-700">
            HTS Code
            <input
              v-model="productForm.htsCode"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:outline-none focus:ring-1 focus:ring-green-500"
              placeholder="12345678"
              maxlength="8"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            WTO Binding Code
            <input
              v-model="productForm.wtoBindingCode"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:outline-none focus:ring-1 focus:ring-green-500"
              placeholder="A"
              maxlength="1"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700 sm:col-span-2">
            Brief Description
            <textarea
              v-model="productForm.briefDescription"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:outline-none focus:ring-1 focus:ring-green-500"
              placeholder="Describe the product"
              rows="3"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Quantity Unit 1
            <input
              v-model="productForm.quantity1Code"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:outline-none focus:ring-1 focus:ring-green-500"
              placeholder="KG"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Quantity Unit 2
            <input
              v-model="productForm.quantity2Code"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-green-500 focus:outline-none focus:ring-1 focus:ring-green-500"
              placeholder="L"
            />
          </label>
        </div>
        <p v-if="productFormError" class="mt-3 text-sm text-red-600">{{ productFormError }}</p>
        <div class="mt-6 flex justify-end space-x-3">
          <button
            type="button"
            class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="closeProductModal"
            :disabled="isSubmittingProduct"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-md bg-green-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-green-700 disabled:cursor-not-allowed disabled:bg-green-300"
            :disabled="isSubmittingProduct"
          >
            {{ isSubmittingProduct ? 'Saving…' : isEditingProduct ? 'Save Changes' : 'Save Product' }}
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- Agreement Modal -->
  <div
    v-if="showAddAgreementModal"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="closeAgreementModal"
  >
    <div class="w-full max-w-2xl rounded-lg bg-white shadow-xl">
      <div class="flex items-start justify-between border-b border-gray-200 px-6 py-4">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">
            {{ isEditingAgreement ? 'Edit Trade Agreement' : 'Add Trade Agreement' }}
          </h3>
          <p class="text-sm text-gray-500">
            {{ isEditingAgreement ? 'Update the trade agreement details.' : 'Publish a new trade agreement so rates can be negotiated against it.' }}
          </p>
        </div>
        <button
          type="button"
          class="text-xl leading-none text-gray-400 hover:text-gray-600"
          @click="closeAgreementModal"
        >
          ×
        </button>
      </div>
      <form class="px-6 py-5" @submit.prevent="submitAgreement">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <label class="block text-sm font-medium text-gray-700">
            Agreement Code
            <input
              v-model="agreementForm.agreementCode"
              type="text"
              class="mt-1 w-full uppercase rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
              placeholder="FTA-01"
              maxlength="20"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Agreement Type
            <input
              v-model="agreementForm.agreementType"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
              placeholder="FTA"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700 sm:col-span-2">
            Agreement Name
            <input
              v-model="agreementForm.agreementName"
              type="text"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
              placeholder="Regional Comprehensive Economic Partnership"
              required
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Effective Date
            <input
              v-model="agreementForm.effectiveDate"
              type="date"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
            />
          </label>
          <label class="block text-sm font-medium text-gray-700">
            Expiration Date
            <input
              v-model="agreementForm.expirationDate"
              type="date"
              class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
            />
          </label>
          <label class="flex items-center space-x-2 text-sm font-medium text-gray-700 sm:col-span-2">
            <input
              v-model="agreementForm.isMultilateral"
              type="checkbox"
              class="h-4 w-4 rounded border-gray-300 text-purple-600 focus:ring-purple-500"
            />
            <span>Multilateral agreement</span>
          </label>
        </div>
        <p v-if="agreementFormError" class="mt-3 text-sm text-red-600">{{ agreementFormError }}</p>
        <div class="mt-6 flex justify-end space-x-3">
          <button
            type="button"
            class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            @click="closeAgreementModal"
            :disabled="isSubmittingAgreement"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-md bg-purple-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-purple-700 disabled:cursor-not-allowed disabled:bg-purple-300"
            :disabled="isSubmittingAgreement"
          >
             {{ isSubmittingAgreement ? 'Saving…' : isEditingAgreement ? 'Save Changes' : 'Save Agreement' }}
          </button>
        </div>
      </form>
    </div>
  </div>
  <!-- Delete Confirmation Modals -->
  <div
    v-if="pendingCountryDeletion"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="cancelCountryDeletion"
  >
    <div class="w-full max-w-md rounded-lg bg-white shadow-xl">
      <div class="border-b border-gray-200 px-6 py-4">
        <h3 class="text-lg font-semibold text-gray-900">Delete Country</h3>
      </div>
      <div class="px-6 py-5 space-y-3">
        <p>Are you sure you want to delete <strong>{{ pendingCountryDeletion.displayName }}</strong>?</p>
        <p class="text-sm text-gray-500">This action cannot be undone.</p>
        <p v-if="countryDeletionError" class="text-sm text-red-600">{{ countryDeletionError }}</p>
      </div>
      <div class="flex justify-end space-x-3 border-t border-gray-200 px-6 py-4">
        <button
          type="button"
          class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
          @click="cancelCountryDeletion"
          :disabled="isDeletingCountry"
        >
          Cancel
        </button>
        <button
          type="button"
          class="rounded-md bg-red-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-red-700 disabled:cursor-not-allowed disabled:bg-red-300"
          @click="deleteCountry"
          :disabled="isDeletingCountry"
        >
          {{ isDeletingCountry ? 'Deleting…' : 'Delete' }}
        </button>
      </div>
    </div>
  </div>

  <div
    v-if="pendingProductDeletion"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="cancelProductDeletion"
  >
    <div class="w-full max-w-md rounded-lg bg-white shadow-xl">
      <div class="border-b border-gray-200 px-6 py-4">
        <h3 class="text-lg font-semibold text-gray-900">Delete Product</h3>
      </div>
      <div class="px-6 py-5 space-y-3">
        <p>Are you sure you want to delete <strong>{{ pendingProductDeletion.displayName }}</strong>?</p>
        <p class="text-sm text-gray-500">This action cannot be undone.</p>
        <p v-if="productDeletionError" class="text-sm text-red-600">{{ productDeletionError }}</p>
      </div>
      <div class="flex justify-end space-x-3 border-t border-gray-200 px-6 py-4">
        <button
          type="button"
          class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
          @click="cancelProductDeletion"
          :disabled="isDeletingProduct"
        >
          Cancel
        </button>
        <button
          type="button"
          class="rounded-md bg-red-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-red-700 disabled:cursor-not-allowed disabled:bg-red-300"
          @click="deleteProduct"
          :disabled="isDeletingProduct"
        >
          {{ isDeletingProduct ? 'Deleting…' : 'Delete' }}
        </button>
      </div>
    </div>
  </div>

  <div
    v-if="pendingAgreementDeletion"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 py-6"
    @click.self="cancelAgreementDeletion"
  >
    <div class="w-full max-w-md rounded-lg bg-white shadow-xl">
      <div class="border-b border-gray-200 px-6 py-4">
        <h3 class="text-lg font-semibold text-gray-900">Delete Trade Agreement</h3>
      </div>
      <div class="px-6 py-5 space-y-3">
        <p>Are you sure you want to delete <strong>{{ pendingAgreementDeletion.displayName }}</strong>?</p>
        <p class="text-sm text-gray-500">This action cannot be undone.</p>
        <p v-if="agreementDeletionError" class="text-sm text-red-600">{{ agreementDeletionError }}</p>
      </div>
      <div class="flex justify-end space-x-3 border-t border-gray-200 px-6 py-4">
        <button
          type="button"
          class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
          @click="cancelAgreementDeletion"
          :disabled="isDeletingAgreement"
        >
          Cancel
        </button>
        <button
          type="button"
          class="rounded-md bg-red-600 px-4 py-2 text-sm font-semibold text-white transition duration-150 hover:bg-red-700 disabled:cursor-not-allowed disabled:bg-red-300"
          @click="deleteAgreement"
          :disabled="isDeletingAgreement"
        >
          {{ isDeletingAgreement ? 'Deleting…' : 'Delete' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { Users, Globe, Package, FileText, Calculator } from 'lucide-vue-next'
import client from '../api/client'

type MaybeString = string | null | undefined

type UserRole = 'USER' | 'ADMIN'

interface UserSummary {
  id: number
  username: string
  email: string
  role: UserRole
}

interface CountrySummary {
  id: number
  countryName: string | null
  countryNameShort: string | null
  countryCode: string | null
  currency: string | null
  region: string | null
  continent: string | null
  displayName: string
}

interface ProductSummary {
  id: number
  hts8: string | null
  briefDescription: string | null
  quantity1Code: string | null
  quantity2Code: string | null
  wtoBindingCode: string | null
  displayName: string
  quantityDisplay: string | null
}

interface TradeAgreementSummary {
  id: number
  agreementName: string | null
  agreementCode: string
  agreementType: string | null
  effectiveDate: string | null
  expirationDate: string | null
  isMultilateral: boolean | null
  dateRange?: string
  displayName: string
}

interface TariffCalculation {
  id: number
  createdAt?: string | null
  htsCode?: string | null
  calculationType?: string | null
  countryCode?: string | null
}

interface ActivityEntry {
  id: number
  username: string
  action: string
  timestamp: string
  status: 'active' | 'inactive'
}

interface CountryFormData {
  countryName: string
  countryCode: string
  countryNameShort: string
  region: string
  continent: string
  currency: string
}

interface ProductFormData {
  htsCode: string
  briefDescription: string
  quantity1Code: string
  quantity2Code: string
  wtoBindingCode: string
}

interface AgreementFormData {
  agreementCode: string
  agreementName: string
  agreementType: string
  effectiveDate: string
  expirationDate: string
  isMultilateral: boolean
}

interface LoadOptions {
  showSpinner?: boolean
}

const showAddCountryModal = ref(false)
const showAddProductModal = ref(false)
const showAddAgreementModal = ref(false)

const countryModalMode = ref<'create' | 'edit'>('create')
const productModalMode = ref<'create' | 'edit'>('create')
const agreementModalMode = ref<'create' | 'edit'>('create')

const editingCountryId = ref<number | null>(null)
const editingProductId = ref<number | null>(null)
const editingAgreementCode = ref<string | null>(null)

const pendingCountryDeletion = ref<CountrySummary | null>(null)
const pendingProductDeletion = ref<ProductSummary | null>(null)
const pendingAgreementDeletion = ref<TradeAgreementSummary | null>(null)

const countryDeletionError = ref<string | null>(null)
const productDeletionError = ref<string | null>(null)
const agreementDeletionError = ref<string | null>(null)

const isDeletingCountry = ref(false)
const isDeletingProduct = ref(false)
const isDeletingAgreement = ref(false)

const countryForm = ref<CountryFormData>(defaultCountryForm())
const productForm = ref<ProductFormData>(defaultProductForm())
const agreementForm = ref<AgreementFormData>(defaultAgreementForm())

const countryFormError = ref<string | null>(null)
const productFormError = ref<string | null>(null)
const agreementFormError = ref<string | null>(null)

const isSubmittingCountry = ref(false)
const isSubmittingProduct = ref(false)
const isSubmittingAgreement = ref(false)

const isEditingCountry = computed(() => countryModalMode.value === 'edit')
const isEditingProduct = computed(() => productModalMode.value === 'edit')
const isEditingAgreement = computed(() => agreementModalMode.value === 'edit')

const adminStats = ref({
  totalUsers: 0,
  totalCountries: 0,
  totalProducts: 0,
  totalAgreements: 0,
  calculationsToday: 0
})

const countries = ref<CountrySummary[]>([])
const products = ref<ProductSummary[]>([])
const tradeAgreements = ref<TradeAgreementSummary[]>([])
const userActivity = ref<ActivityEntry[]>([])
const isLoading = ref(true)
const loadError = ref<string | null>(null)

function isSameDay(dateString: string | null | undefined) {
  if (!dateString) return false
  const date = new Date(dateString)
  if (Number.isNaN(date.getTime())) return false
  const now = new Date()
  return (
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate()
  )
}

function formatTimeAgo(dateString: string | null | undefined) {
  if (!dateString) return 'Unknown time'
  const date = new Date(dateString)
  if (Number.isNaN(date.getTime())) return 'Unknown time'

  const diffMs = Date.now() - date.getTime()
  const diffSeconds = Math.floor(diffMs / 1000)

  if (diffSeconds < 60) return `${diffSeconds}s ago`
  const diffMinutes = Math.floor(diffSeconds / 60)
  if (diffMinutes < 60) return `${diffMinutes}m ago`
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours}h ago`
  const diffDays = Math.floor(diffHours / 24)
  if (diffDays < 7) return `${diffDays}d ago`

  return new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(date)
}

function buildDateRange(start?: string | null, end?: string | null) {
  const hasStart = Boolean(start)
  const hasEnd = Boolean(end)

  if (!hasStart && !hasEnd) return undefined

  const format = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' })
  const startDate = hasStart ? new Date(start as string) : null
  const endDate = hasEnd ? new Date(end as string) : null

  const startLabel = startDate && !Number.isNaN(startDate.getTime()) ? format.format(startDate) : 'Unknown start'
  const endLabel = endDate && !Number.isNaN(endDate.getTime()) ? format.format(endDate) : 'No expiry'

  return `${startLabel} → ${endLabel}`
}

function sortByDisplayName<T extends { displayName: string }>(items: T[]): T[] {
  return [...items].sort((a, b) => a.displayName.localeCompare(b.displayName, undefined, { sensitivity: 'base' }))
}

function coerceString(value: MaybeString): string | null {
  if (value == null) {
    return null
  }

  const trimmed = String(value).trim()
  return trimmed.length ? trimmed : null
}

function mapCountry(country: any): CountrySummary {
  const countryName = coerceString(country?.countryName)
  const shortName = coerceString(country?.countryNameShort)
  const code = coerceString(country?.countryCode)
  const currency = coerceString(country?.currency)
  const region = coerceString(country?.region)
  const continent = coerceString(country?.continent)
  const displayName = countryName ?? shortName ?? code ?? 'Unknown country'

  return {
    id: typeof country?.id === 'number' ? country.id : Number(country?.id ?? 0),
    countryName,
    countryNameShort: shortName,
    countryCode: code,
    currency,
    region,
    continent,
    displayName
  }
}

function mapProduct(product: any): ProductSummary {
  const description = coerceString(product?.briefDescription)
  const hts = coerceString(product?.hts8)
  const quantity1 = coerceString(product?.quantity1Code)
  const quantity2 = coerceString(product?.quantity2Code)
  const binding = coerceString(product?.wtoBindingCode)
  const quantityDisplay = [quantity1, quantity2].filter(Boolean).join(' / ') || null
  const displayName = description ?? (hts ? `HTS ${hts}` : 'Unknown product')

  return {
    id: typeof product?.id === 'number' ? product.id : Number(product?.id ?? 0),
    hts8: hts,
    briefDescription: description,
    quantity1Code: quantity1,
    quantity2Code: quantity2,
    wtoBindingCode: binding,
    displayName,
    quantityDisplay
  }
}

function mapTradeAgreement(agreement: any): TradeAgreementSummary {
  const code = coerceString(agreement?.agreementCode) ?? ''
  const name = coerceString(agreement?.agreementName)
  const type = coerceString(agreement?.agreementType)
  const effectiveDate = coerceString(agreement?.effectiveDate)
  const expirationDate = coerceString(agreement?.expirationDate)
  const isMultilateral = typeof agreement?.isMultilateral === 'boolean' ? agreement.isMultilateral : null
  const displayName = (name ?? code) || 'Unnamed agreement'

  return {
    id: typeof agreement?.id === 'number' ? agreement.id : Number(agreement?.id ?? 0),
    agreementName: name,
    agreementCode: code,
    agreementType: type,
    effectiveDate,
    expirationDate,
    isMultilateral,
    dateRange: buildDateRange(effectiveDate, expirationDate),
    displayName
  }
}
async function loadDashboardData(options: LoadOptions = {}) {
  const shouldShowSpinner = options.showSpinner ?? true
  if (shouldShowSpinner) {
    isLoading.value = true
  }
  loadError.value = null

  try {
    const [usersRes, countriesRes, productsRes, agreementsRes, calculationsRes] = await Promise.all([
      client.get<UserSummary[]>('/users'),
      client.get('/country'),
      client.get('/products'),
      client.get('/trade-agreements'),
      client.get<TariffCalculation[]>('/tariff/calculate-all')
    ])

    const users = Array.isArray(usersRes.data) ? usersRes.data : []
    const countriesData = Array.isArray(countriesRes.data) ? countriesRes.data : []
    const productsData = Array.isArray(productsRes.data) ? productsRes.data : []
    const agreementsData = Array.isArray(agreementsRes.data) ? agreementsRes.data : []
    const calculationsData = Array.isArray(calculationsRes.data) ? calculationsRes.data : []

    countries.value = sortByDisplayName(countriesData.map(mapCountry))
    products.value = sortByDisplayName(productsData.map(mapProduct))
    tradeAgreements.value = sortByDisplayName(agreementsData.map(mapTradeAgreement))

    adminStats.value = {
      totalUsers: users.length,
      totalCountries: countries.value.length,
      totalProducts: products.value.length,
      totalAgreements: tradeAgreements.value.length,
      calculationsToday: calculationsData.filter(calc => isSameDay(calc.createdAt)).length
    }

    const sortedCalculations = [...calculationsData].sort((a, b) => {
      const aTime = a.createdAt ? new Date(a.createdAt).getTime() : 0
      const bTime = b.createdAt ? new Date(b.createdAt).getTime() : 0
      return bTime - aTime
    })

    userActivity.value = sortedCalculations.slice(0, 10).map(calc => ({
      id: calc.id,
      username: calc.htsCode ? `HTS ${calc.htsCode}` : `Calculation #${calc.id}`,
      action: calc.calculationType
        ? `Ran ${String(calc.calculationType).toLowerCase()} calculation`
        : 'Completed tariff calculation',
      timestamp: formatTimeAgo(calc.createdAt || null),
      status: isSameDay(calc.createdAt) ? 'active' : 'inactive'
    }))
  } catch (error) {
    console.error('Failed to load admin dashboard data:', error)
    loadError.value = 'Unable to load the latest admin data. Showing the most recent cached view if available.'
  } finally {
    isLoading.value = false
  }
}

async function submitCountry() {
  if (isSubmittingCountry.value) {
    return
  }

  const name = sanitizeString(countryForm.value.countryName)
  const code = sanitizeString(countryForm.value.countryCode)

  if (!name || !code) {
    countryFormError.value = 'Country name and code are required.'
    return
  }

  try {
    isSubmittingCountry.value = true
    countryFormError.value = null

    const payload = {
      countryName: name,
      countryCode: code.toUpperCase(),
      countryNameShort: sanitizeString(countryForm.value.countryNameShort),
      region: sanitizeString(countryForm.value.region),
      continent: sanitizeString(countryForm.value.continent),
      urrency: sanitizeString(countryForm.value.currency)?.toUpperCase() ?? null
    }

    const response = isEditingCountry.value && editingCountryId.value != null
      ? await client.put(`/country/${editingCountryId.value}`, payload)
      : await client.post('/country', payload)

    const summary = mapCountry(response.data)

    if (isEditingCountry.value) {
      countries.value = sortByDisplayName(countries.value.map(country => (country.id === summary.id ? summary : country)))
    } else {
      countries.value = sortByDisplayName([...countries.value, summary])
    }

    adminStats.value.totalCountries = countries.value.length

    showAddCountryModal.value = false
    resetCountryForm()
    countryModalMode.value = 'create'
    editingCountryId.value = null
  } catch (error) {
    countryFormError.value = getErrorMessage(error)
  } finally {
    isSubmittingCountry.value = false
  }
}

async function submitProduct() {
  if (isSubmittingProduct.value) {
    return
  }

  const htsCode = sanitizeString(productForm.value.htsCode)
  const description = sanitizeString(productForm.value.briefDescription)

  if (!htsCode || !description) {
    productFormError.value = 'HTS code and product description are required.'
    return
  }

  try {
    isSubmittingProduct.value = true
    productFormError.value = null

    const payload = {
      hts8: htsCode.toUpperCase(),
      briefDescription: description,
      quantity1Code: sanitizeString(productForm.value.quantity1Code)?.toUpperCase() ?? null,
      quantity2Code: sanitizeString(productForm.value.quantity2Code)?.toUpperCase() ?? null,
      wtoBindingCode: sanitizeString(productForm.value.wtoBindingCode)?.toUpperCase() ?? null
    }

    const response = isEditingProduct.value && editingProductId.value != null
      ? await client.put(`/products/${editingProductId.value}`, payload)
      : await client.post('/products', payload)

    const summary = mapProduct(response.data)

    if (isEditingProduct.value) {
      products.value = sortByDisplayName(products.value.map(product => (product.id === summary.id ? summary : product)))
    } else {
      products.value = sortByDisplayName([...products.value, summary])
    }

    adminStats.value.totalProducts = products.value.length

    showAddProductModal.value = false
    resetProductForm()
    productModalMode.value = 'create'
    editingProductId.value = null
  } catch (error) {
    productFormError.value = getErrorMessage(error)
  } finally {
    isSubmittingProduct.value = false
  }
}

async function submitAgreement() {
  if (isSubmittingAgreement.value) {
    return
  }

  const code = sanitizeString(agreementForm.value.agreementCode)
  const name = sanitizeString(agreementForm.value.agreementName)
  const type = sanitizeString(agreementForm.value.agreementType)

  if (!code || !name || !type) {
    agreementFormError.value = 'Agreement code, name, and type are required.'
    return
  }

  try {
    isSubmittingAgreement.value = true
    agreementFormError.value = null

    const payload = {
      agreementCode: code,
      agreementName: name,
      agreementType: type,
      effectiveDate: sanitizeString(agreementForm.value.effectiveDate),
      expirationDate: sanitizeString(agreementForm.value.expirationDate),
      isMultilateral: agreementForm.value.isMultilateral
    }

    const response = isEditingAgreement.value && editingAgreementCode.value
      ? await client.put(`/trade-agreements/${encodeURIComponent(editingAgreementCode.value)}`, payload)
      : await client.post('/trade-agreements', payload)

    const summary = mapTradeAgreement(response.data)

    if (isEditingAgreement.value) {
      tradeAgreements.value = sortByDisplayName(
        tradeAgreements.value.map(agreement => (agreement.id === summary.id ? summary : agreement))
      )
    } else {
      tradeAgreements.value = sortByDisplayName([...tradeAgreements.value, summary])
    }

    adminStats.value.totalAgreements = tradeAgreements.value.length

    showAddAgreementModal.value = false
    resetAgreementForm()
    agreementModalMode.value = 'create'
    editingAgreementCode.value = null
  } catch (error) {
    agreementFormError.value = getErrorMessage(error)
  } finally {
    isSubmittingAgreement.value = false
  }
}

function openCountryModalForCreate() {
  countryModalMode.value = 'create'
  editingCountryId.value = null
  resetCountryForm()
  showAddCountryModal.value = true
}

function openCountryModalForEdit(country: CountrySummary) {
  countryModalMode.value = 'edit'
  editingCountryId.value = country.id
  countryForm.value = {
    countryName: country.countryName ?? country.displayName,
    countryCode: country.countryCode ?? '',
    countryNameShort: country.countryNameShort ?? '',
    region: country.region ?? '',
    continent: country.continent ?? '',
    currency: country.currency ?? ''
  }
  countryFormError.value = null
  showAddCountryModal.value = true
}

function closeCountryModal() {
  if (isSubmittingCountry.value) {
    return
  }
  showAddCountryModal.value = false
  resetCountryForm()
  countryModalMode.value = 'create'
  editingCountryId.value = null
  countryFormError.value = null
}

function resetCountryForm() {
  countryForm.value = defaultCountryForm()
}

function openProductModalForCreate() {
  productModalMode.value = 'create'
  editingProductId.value = null
  resetProductForm()
  showAddProductModal.value = true
}

function openProductModalForEdit(product: ProductSummary) {
  productModalMode.value = 'edit'
  editingProductId.value = product.id
  productForm.value = {
    htsCode: product.hts8 ?? '',
    briefDescription: product.briefDescription ?? product.displayName,
    quantity1Code: product.quantity1Code ?? '',
    quantity2Code: product.quantity2Code ?? '',
    wtoBindingCode: product.wtoBindingCode ?? ''
  }
  productFormError.value = null
  showAddProductModal.value = true
}

function closeProductModal() {
  if (isSubmittingProduct.value) {
    return
  }
  showAddProductModal.value = false
  resetProductForm()
  productModalMode.value = 'create'
  editingProductId.value = null
  productFormError.value = null
}

function resetProductForm() {
  productForm.value = defaultProductForm()
}

function openAgreementModalForCreate() {
  agreementModalMode.value = 'create'
  editingAgreementCode.value = null
  resetAgreementForm()
  showAddAgreementModal.value = true
}

function openAgreementModalForEdit(agreement: TradeAgreementSummary) {
  agreementModalMode.value = 'edit'
  editingAgreementCode.value = agreement.agreementCode
  agreementForm.value = {
    agreementCode: agreement.agreementCode,
    agreementName: agreement.agreementName ?? agreement.displayName,
    agreementType: agreement.agreementType ?? '',
    effectiveDate: agreement.effectiveDate ?? '',
    expirationDate: agreement.expirationDate ?? '',
    isMultilateral: Boolean(agreement.isMultilateral)
  }
  agreementFormError.value = null
  showAddAgreementModal.value = true
}

function closeAgreementModal() {
  if (isSubmittingAgreement.value) {
    return
  }
  showAddAgreementModal.value = false
  resetAgreementForm()
  agreementModalMode.value = 'create'
  editingAgreementCode.value = null
  agreementFormError.value = null
}

function resetAgreementForm() {
  agreementForm.value = defaultAgreementForm()
}

function promptCountryDeletion(country: CountrySummary) {
  pendingCountryDeletion.value = country
  countryDeletionError.value = null
}

function cancelCountryDeletion() {
  if (isDeletingCountry.value) {
    return
  }
  pendingCountryDeletion.value = null
  countryDeletionError.value = null
}

async function deleteCountry() {
  if (!pendingCountryDeletion.value) {
    return
  }

  try {
    isDeletingCountry.value = true
    countryDeletionError.value = null

    await client.delete(`/country/${pendingCountryDeletion.value.id}`)
    countries.value = countries.value.filter(country => country.id !== pendingCountryDeletion.value?.id)
    adminStats.value.totalCountries = countries.value.length
    pendingCountryDeletion.value = null
  } catch (error) {
    countryDeletionError.value = getErrorMessage(error)
  } finally {
    isDeletingCountry.value = false
  }
}

function promptProductDeletion(product: ProductSummary) {
  pendingProductDeletion.value = product
  productDeletionError.value = null
}

function cancelProductDeletion() {
  if (isDeletingProduct.value) {
    return
  }
  pendingProductDeletion.value = null
  productDeletionError.value = null
}

async function deleteProduct() {
  if (!pendingProductDeletion.value) {
    return
  }

  try {
    isDeletingProduct.value = true
    productDeletionError.value = null

    await client.delete(`/products/${pendingProductDeletion.value.id}`)
    products.value = products.value.filter(product => product.id !== pendingProductDeletion.value?.id)
    adminStats.value.totalProducts = products.value.length
    pendingProductDeletion.value = null
  } catch (error) {
    productDeletionError.value = getErrorMessage(error)
  } finally {
    isDeletingProduct.value = false
  }
}

function promptAgreementDeletion(agreement: TradeAgreementSummary) {
  pendingAgreementDeletion.value = agreement
  agreementDeletionError.value = null
}

function cancelAgreementDeletion() {
  if (isDeletingAgreement.value) {
    return
  }
  pendingAgreementDeletion.value = null
  agreementDeletionError.value = null
}

async function deleteAgreement() {
  if (!pendingAgreementDeletion.value) {
    return
  }

  try {
    isDeletingAgreement.value = true
    agreementDeletionError.value = null

    await client.delete(`/trade-agreements/${encodeURIComponent(pendingAgreementDeletion.value.agreementCode)}`)
    tradeAgreements.value = tradeAgreements.value.filter(
      agreement => agreement.id !== pendingAgreementDeletion.value?.id
    )
    adminStats.value.totalAgreements = tradeAgreements.value.length
    pendingAgreementDeletion.value = null
  } catch (error) {
    agreementDeletionError.value = getErrorMessage(error)
  } finally {
    isDeletingAgreement.value = false
  }
}

function defaultCountryForm(): CountryFormData {
  return {
    countryName: '',
    countryCode: '',
    countryNameShort: '',
    region: '',
    continent: '',
    currency: ''
  }
}

function defaultProductForm(): ProductFormData {
  return {
    htsCode: '',
    briefDescription: '',
    quantity1Code: '',
    quantity2Code: '',
    wtoBindingCode: ''
  }
}

function defaultAgreementForm(): AgreementFormData {
  return {
    agreementCode: '',
    agreementName: '',
    agreementType: '',
    effectiveDate: '',
    expirationDate: '',
    isMultilateral: false
  }
}

function sanitizeString(value: string | null | undefined): string | null {
  if (value == null) {
    return null
  }

  const trimmed = value.trim()
  return trimmed.length ? trimmed : null
}

function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data
    if (typeof data === 'string') {
      return data
    }
    if (data && typeof data === 'object' && 'message' in data) {
      return String((data as { message: unknown }).message)
    }
    return error.message
  }

  if (error instanceof Error) {
    return error.message
  }

  return 'An unexpected error occurred.'
}

onMounted(() => {
  loadDashboardData()
})
</script>
