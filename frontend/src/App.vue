<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { SidebarProvider } from "@/components/ui/sidebar";
import AppSidebar from "@/components/AppSidebar.vue";
import AppHeader from "@/components/AppHeader.vue";

const route = useRoute()

const hideSidebar = computed(() =>
  route.matched.some(r => r.meta?.hideSidebar)
)
</script>

<template>
  <SidebarProvider>
    <div class="flex min-h-screen w-full">
      <!-- Sidebar -->
      <AppSidebar v-if="!hideSidebar" />

      <!-- Main content area -->
      <div class="flex-1 flex flex-col min-w-0">
        <!-- Header with profile icon -->
        <AppHeader v-if="!hideSidebar" />

        <!-- Main content where all routed pages will render -->
        <main class="flex-1 overflow-x-hidden">
          <router-view />
        </main>

        <!-- keep support for default slot content -->
        <slot />
      </div>
    </div>
  </SidebarProvider>
</template>
