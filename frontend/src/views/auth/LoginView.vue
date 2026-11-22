<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">SIMAI</h2>
      <p class="subtitle">Sistem Informasi Manajemen Aset &amp; Inventori</p>

      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item>
          <el-input v-model="form.email" placeholder="Email" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="Password"
            size="large"
            show-password
          >
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          class="login-btn"
          native-type="submit"
          :loading="loading"
        >
          Masuk
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ email: '', password: '' })

async function onSubmit() {
  if (!form.email || !form.password) {
    ElMessage.warning('Email dan password wajib diisi')
    return
  }
  loading.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    ElMessage.success('Login berhasil')
    router.push({ name: 'dashboard' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3b73, #2f6db3);
}
.login-card {
  width: 380px;
  padding: 12px;
}
.title {
  text-align: center;
  margin: 0;
}
.subtitle {
  text-align: center;
  color: #888;
  margin: 8px 0 24px;
}
.login-btn {
  width: 100%;
}
</style>
