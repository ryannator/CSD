<script setup lang="ts">
import AppSidebar from "@/components/AppSidebar.vue"
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
</script>

<template>
  <SidebarProvider>
    <div class="flex h-screen">
      <!-- Sidebar (no changes to your AppSidebar.vue) -->
      <AppSidebar />

      <!-- Main content area -->
      <div class="flex-1 flex flex-col">
        <!-- Optional top bar with sidebar toggle -->
        <header class="p-4 border-b bg-white shadow-sm flex items-center">
          <SidebarTrigger />
          <h1 class="ml-4 text-lg font-semibold">TARIFF System</h1>
        </header>

        <!-- Routed pages will render here -->
        <main class="flex-1 overflow-y-auto p-6 bg-gray-50">
          <router-view />
        </main>
      </div>
    </div>
  </SidebarProvider>
</template>
