<script setup lang="ts">
import { computed } from 'vue'
import { Calculator, Home, Shield, Users, BarChart3, Globe, LogOut, LogIn } from "lucide-vue-next"
import { useAuth } from '../composables/useAuth'
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "../components/ui/sidebar"

const { isAuthenticated, isAdmin, logout } = useAuth()

const items = [
  {
    title: "Home",
    url: "/",
    icon: Home,
    public: true
  },
  {
    title: "Tariff Calculator",
    url: "/calculator",
    icon: Calculator,
    requiresAuth: true
  },
  {
    title: "User Dashboard",
    url: "/user-dashboard",
    icon: BarChart3,
    requiresAuth: true,
    userOnly: true
  },
  {
    title: "Admin Dashboard",
    url: "/admin-dashboard",
    icon: Shield,
    requiresAuth: true,
    adminOnly: true
  },
  {
    title: "Countries & Products",
    url: "/countries-products",
    icon: Globe,
    public: true
  },
  {
    title: "About",
    url: "/about",
    icon: Users,
    public: true
  },
];

const filteredItems = computed(() => {
  return items.filter(item => {

    if (item.public) {
      return true
    }

    if (item.requiresAuth && !isAuthenticated.value) {
      return false
    }

    if (item.adminOnly && !isAdmin.value) {
      return false
    }

    if (item.userOnly && isAdmin.value) {
      return false
    }

    return true
  })
})

const handleLogout = () => {
  logout()
}
</script>

<template>
  <Sidebar>
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>Application</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
              <SidebarMenuItem v-for="item in filteredItems" :key="item.title">
                <SidebarMenuButton asChild>
                    <RouterLink :to="item.url" class="flex items-center space-x-2">
                      <component :is="item.icon" class="w-4 h-4" />
                      <span>{{item.title}}</span>
                    </RouterLink>
                </SidebarMenuButton>
              </SidebarMenuItem>

              <!-- Auth Actions -->
              <SidebarMenuItem v-if="isAuthenticated">
                <SidebarMenuButton @click="handleLogout" class="flex items-center space-x-2 text-red-600 hover:text-red-800">
                  <LogOut class="w-4 h-4" />
                  <span>Logout</span>
                </SidebarMenuButton>
              </SidebarMenuItem>

          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>
  </Sidebar>
</template>
