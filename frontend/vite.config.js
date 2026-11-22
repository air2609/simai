import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Target backend untuk proxy. Bisa di-override via env, mis.
// VITE_PROXY_TARGET=http://localhost:8081 npm run dev
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VITE_PROXY_TARGET || 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
