import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import { createApp } from "vue";
import App from "./App.vue";
import "./assets/css/global.css";
import router from "./router";

const app = createApp(App);

app.use(router);

app.mount("#app");

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}