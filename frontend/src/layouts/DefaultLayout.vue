<template>
  <el-container class="layout">
    <el-aside width="240px" class="aside">
      <div class="brand">SIMAI</div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon><span>Dashboard</span>
        </el-menu-item>

        <el-sub-menu index="master">
          <template #title><el-icon><Setting /></el-icon><span>Master Data</span></template>
          <el-menu-item index="/master/departments">Departemen</el-menu-item>
          <el-menu-item index="/master/locations">Lokasi</el-menu-item>
          <el-menu-item index="/master/vendors">Vendor</el-menu-item>
          <el-menu-item index="/master/categories">Kategori</el-menu-item>
          <el-menu-item index="/master/users">Pengguna</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="asset">
          <template #title><el-icon><Monitor /></el-icon><span>Aset</span></template>
          <el-menu-item index="/assets">Daftar Aset</el-menu-item>
          <el-menu-item index="/assets/movements">Mutasi Aset</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="inventory">
          <template #title><el-icon><Box /></el-icon><span>Inventori</span></template>
          <el-menu-item index="/inventory/items">Item Inventori</el-menu-item>
          <el-menu-item index="/inventory/movements">Transaksi Stok</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/stock-opnames">
          <el-icon><DocumentChecked /></el-icon><span>Stock Opname</span>
        </el-menu-item>

        <el-menu-item index="/reports">
          <el-icon><TrendCharts /></el-icon><span>Laporan</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="page-title">{{ $route.meta.title || 'SIMAI' }}</span>
        <el-dropdown @command="onCommand">
          <span class="user-chip">
            {{ auth.user?.name || 'User' }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">Keluar</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function onCommand(command) {
  if (command === 'logout') {
    auth.logout()
    router.push({ name: 'login' })
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}
.aside {
  background: #001529;
  color: #fff;
}
.brand {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}
.menu {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.user-chip {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
.main {
  background: #f5f7fa;
}
</style>
