import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss()
  ],
  server: {
    port: 5173,
    host: '0.0.0.0',
    allowedHosts: ["kiwibellako.com", "dev.kiwibellako.com", "localhost", "127.0.0.1"],
    watch: {
      usePolling: true,
    }
  },
  define: {
    'process.env': process.env
  }
})
