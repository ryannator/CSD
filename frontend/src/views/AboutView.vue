<template>
  <div class="container mx-auto p-6">
    <div class="max-w-4xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <div class="mx-auto h-20 w-20 flex items-center justify-center rounded-full bg-blue-100 mb-6">
          <Globe class="h-12 w-12 text-blue-600" />
        </div>
        <h1 class="text-4xl font-bold text-gray-900 mb-4">About TARIFF System</h1>
        <p class="text-xl text-gray-600">Trade Agreements Regulating Imports and Foreign Fees</p>
      </div>

      <br/>

      <!-- Mission Statement -->
      <div class="bg-blue-50 rounded-lg p-8 mb-12">
        <h2 class="text-2xl font-semibold text-blue-900 mb-4">Our Mission</h2>
        <p class="text-lg text-blue-800 leading-relaxed">
          The TARIFF System is designed to simplify and streamline international trade calculations by providing
          accurate, real-time tariff computations for imports and exports between countries. We aim to make
          international trade more accessible and transparent for businesses of all sizes.
        </p>
      </div>

      <br/><br/>

      <!-- Features Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4">
            <Calculator class="w-6 h-6 text-green-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Accurate Calculations</h3>
          <p class="text-gray-600">
            Precise tariff calculations based on current trade agreements and regulations between countries.
          </p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4">
            <BarChart3 class="w-6 h-6 text-purple-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Comprehensive Analytics</h3>
          <p class="text-gray-600">
            Detailed insights and analytics to help businesses make informed trading decisions.
          </p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center mb-4">
            <Shield class="w-6 h-6 text-yellow-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Secure & Reliable</h3>
          <p class="text-gray-600">
            Enterprise-grade security with reliable uptime for critical business operations.
          </p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center mb-4">
            <Users class="w-6 h-6 text-red-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">User-Friendly</h3>
          <p class="text-gray-600">
            Intuitive interface designed for both trade professionals and newcomers to international commerce.
          </p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-indigo-100 rounded-lg flex items-center justify-center mb-4">
            <FileText class="w-6 h-6 text-indigo-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Compliance Ready</h3>
          <p class="text-gray-600">
            Stay compliant with international trade regulations and documentation requirements.
          </p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-teal-100 rounded-lg flex items-center justify-center mb-4">
            <Clock class="w-6 h-6 text-teal-600" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Real-Time Updates</h3>
          <p class="text-gray-600">
            Always up-to-date with the latest trade agreements and tariff changes worldwide.
          </p>
        </div>
      </div>

      <br/><br/>

      <!-- How It Works -->
      <div class="mb-12">
        <h2 class="text-3xl font-bold text-gray-900 text-center mb-8">How TARIFF System Works</h2>
        <div class="space-y-8">
          <div class="flex items-start space-x-4">
            <div class="flex-shrink-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold">1</div>
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Select Countries & Products</h3>
              <p class="text-gray-600">Choose your origin and destination countries, then select the specific product or industry category for your import/export.</p>
            </div>
          </div>

          <div class="flex items-start space-x-4">
            <div class="flex-shrink-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold">2</div>
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Enter Product Value</h3>
              <p class="text-gray-600">Input the monetary value of your goods to ensure accurate tariff calculations based on current market values.</p>
            </div>
          </div>

          <div class="flex items-start space-x-4">
            <div class="flex-shrink-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold">3</div>
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Get Instant Results</h3>
              <p class="text-gray-600">Receive detailed breakdown of all applicable tariffs, customs fees, and total costs with transparent calculation methodology.</p>
            </div>
          </div>

          <div class="flex items-start space-x-4">
            <div class="flex-shrink-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold">4</div>
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Track & Manage</h3>
              <p class="text-gray-600">Save calculations, track history, and generate reports for your business records and compliance documentation.</p>
            </div>
          </div>
        </div>
      </div>

      <br/><br/>

      <!-- Target Users -->
      <div class="bg-gray-50 rounded-lg p-8 mb-12">
        <h2 class="text-2xl font-semibold text-gray-900 mb-6 text-center">Who Uses TARIFF System?</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">Import/Export Businesses</h3>
            <p class="text-gray-600 mb-4">Companies engaged in international trade who need accurate cost calculations for budgeting and pricing decisions.</p>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">Trade Consultants</h3>
            <p class="text-gray-600 mb-4">Professional advisors helping clients navigate complex international trade regulations and costs.</p>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">Logistics Companies</h3>
            <p class="text-gray-600 mb-4">Freight forwarders and logistics providers calculating total shipping costs including tariffs and fees.</p>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">Government Agencies</h3>
            <p class="text-gray-600 mb-4">Trade offices and customs departments ensuring compliance with current tariff regulations.</p>
          </div>
        </div>
      </div>

      <br/><br/>

      <!-- Project Information -->
      <div class="bg-blue-600 text-white rounded-lg p-8 text-center">
        <h2 class="text-2xl font-semibold mb-4">Project Information</h2>
        <p class="text-lg mb-4">
          This system is part of a Collaborative Software Development project (CS203)
          focusing on modern web application development with enterprise-grade architecture.
        </p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
          <div>
            <div class="text-3xl font-bold">25+</div>
            <div class="text-blue-200">Countries Supported</div>
          </div>
          <div>
            <div class="text-3xl font-bold">150+</div>
            <div class="text-blue-200">Product Categories</div>
          </div>
          <div>
            <div class="text-3xl font-bold">40+</div>
            <div class="text-blue-200">Trade Agreements</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Globe, Calculator, BarChart3, Shield, Users, FileText, Clock } from 'lucide-vue-next'
</script>
