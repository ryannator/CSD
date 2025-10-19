<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import AppSidebar from "@/components/AppSidebar.vue";

const route = useRoute()
const hideSidebar = computed(() =>
  route.matched.some(r => r.meta?.hideSidebar)
)
</script>

<template>
  <SidebarProvider>
    <!-- sidebar -->
    <AppSidebar v-if="!hideSidebar" />

    <!-- Main content area -->
    <main :class="['flex-1', hideSidebar ? 'p-0' : 'p-6']">
      <!-- button to toggle sidebar-->
      <SidebarTrigger v-if="!hideSidebar" />

      <!-- where all routed pages will render-->
      <router-view />

      <!-- keep support for default slot content-->
      <slot />
    </main>
  </SidebarProvider>
</template>
