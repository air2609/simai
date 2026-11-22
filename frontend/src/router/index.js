import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/auth/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: 'Dashboard' }
      },
      // Master Data
      {
        path: 'master/departments',
        name: 'departments',
        component: () => import('../views/master/DepartmentsView.vue'),
        meta: { title: 'Departemen' }
      },
      {
        path: 'master/locations',
        name: 'locations',
        component: () => import('../views/master/LocationsView.vue'),
        meta: { title: 'Lokasi' }
      },
      {
        path: 'master/vendors',
        name: 'vendors',
        component: () => import('../views/master/VendorsView.vue'),
        meta: { title: 'Vendor' }
      },
      {
        path: 'master/categories',
        name: 'categories',
        component: () => import('../views/master/CategoriesView.vue'),
        meta: { title: 'Kategori' }
      },
      {
        path: 'master/users',
        name: 'users',
        component: () => import('../views/master/UsersView.vue'),
        meta: { title: 'Pengguna' }
      },
      // Aset
      {
        path: 'assets',
        name: 'assets',
        component: () => import('../views/asset/AssetListView.vue'),
        meta: { title: 'Aset' }
      },
      {
        path: 'assets/movements',
        name: 'asset-movements',
        component: () => import('../views/asset/AssetMovementView.vue'),
        meta: { title: 'Mutasi Aset' }
      },
      // Inventori
      {
        path: 'inventory/items',
        name: 'items',
        component: () => import('../views/inventory/ItemListView.vue'),
        meta: { title: 'Item Inventori' }
      },
      {
        path: 'inventory/movements',
        name: 'stock-movements',
        component: () => import('../views/inventory/StockMovementView.vue'),
        meta: { title: 'Transaksi Stok' }
      },
      // Stock Opname
      {
        path: 'stock-opnames',
        name: 'stock-opnames',
        component: () => import('../views/stockopname/StockOpnameListView.vue'),
        meta: { title: 'Stock Opname' }
      },
      {
        path: 'stock-opnames/new',
        name: 'stock-opname-form',
        component: () => import('../views/stockopname/StockOpnameFormView.vue'),
        meta: { title: 'Buat Stock Opname' }
      },
      // Laporan
      {
        path: 'reports',
        name: 'reports',
        component: () => import('../views/report/ReportView.vue'),
        meta: { title: 'Laporan' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthenticated) {
    return { name: 'login' }
  }
  if (to.name === 'login' && auth.isAuthenticated) {
    return { name: 'dashboard' }
  }
})

export default router
