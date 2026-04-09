import vue from "@vitejs/plugin-vue";
import { defineConfig } from "vite";

export default defineConfig({
  base: "/snake-vue/",
  plugins: [vue()],
});
