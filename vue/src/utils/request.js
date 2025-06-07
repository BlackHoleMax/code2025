import router from "@/router/index";
import axios from "axios";
import { ElMessage } from "element-plus";

// 创建 axios 实例
const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 20000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    config.headers["Content-Type"] = "application/json;charset=utf-8";
    const user = JSON.parse(localStorage.getItem("code_user") || "{}");
    config.headers["token"] = user.token;
    return config;
  },
  (error) => {
    console.log(error);
    Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    let res = response.data;
    if (typeof res === "string") {
      res = res ? JSON.parse(res) : res;
    }
    if (res.code === "401") {
      ElMessage.error(res.msg);
      router.push("/login")
    } else {
      return res;
    }
  },
  (error) => {
    if (error.response.status === 404) {
      ElMessage.error("未找到请求接口");
    } else if (error.response.status === 500) {
      ElMessage.error("系统异常，请查看后端控制台报错");
    } else {
      ElMessage.error(error.message);
    }
    return Promise.reject(error);
  }
);

export default request;
