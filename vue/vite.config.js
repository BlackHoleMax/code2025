import {fileURLToPath, URL} from "node:url";

import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import {ElementPlusResolver} from "unplugin-vue-components/resolvers";
import Components from "unplugin-vue-components/vite";
import {defineConfig} from "vite";

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver({importStyle: "sass"})],
        }),
        Components({
            resolvers: [ElementPlusResolver({importStyle: "sass"})],
        }),
    ],
    base: "/",
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: `@use "@/assets/css/index.scss" as *;`,
            },
        },
    },
    resolve: {
        alias: {
            "@": fileURLToPath(new URL("./src", import.meta.url)),
        },
    },
});
