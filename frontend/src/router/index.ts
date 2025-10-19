import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import TariffCalculator from '@/views/TariffCalculator.vue'
import UserDashboard from '@/views/UserDashboard.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import CountriesProducts from '@/views/CountriesProducts.vue'
import { getToken } from '@/lib/token';
import { getRolesFromToken } from '@/lib/jwt.ts';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true, guestOnly: true, hideSidebar: true },
    },
    {
      path: '/calculator',
      name: 'calculator',
      component: TariffCalculator,
    },
    {
      path: '/user-dashboard',
      name: 'user-dashboard',
      component: UserDashboard,
    },
    {
      path: '/admin-dashboard',
      name: 'admin-dashboard',
      component: AdminDashboard,
      meta: { requiresAdmin: true }
    },
    {
      path: '/countries-products',
      name: 'countries-products',
      component: CountriesProducts,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

router.beforeEach((to) => {
  const token = getToken();
  const authed = !!token

  if (to.matched.some(r => r.meta?.public)) {
    return true
  }

  if (!authed) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  const roles = token ? getRolesFromToken(token) : []
  const isAdmin = roles.some(role => role.toUpperCase() === 'ROLE_ADMIN' || role.toUpperCase() === 'ADMIN')

  if (to.matched.some(r => r.meta?.requiresAdmin) && !isAdmin) {
    return { path: '/user-dashboard' }
  }

  if (to.matched.some(r => r.meta?.guestOnly) && authed) {
    return { path: isAdmin ? '/admin-dashboard' : '/user-dashboard' }
  }

  return true;
})

export default router
