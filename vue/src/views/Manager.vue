<template>
  <div>
    <!-- 头部区域开始 -->
    <div class="header">
      <div class="logo-container">
        <img src="@/assets/imgs/logo.jpg" alt="Logo" class="logo" />
        <span class="title">后台管理系统</span>
      </div>
      <div class="header-spacer" style="
          display: flex;
          align-items: center;
          padding-left: 20px;
          color: #fff;
        ">
        <span @click="router.push('/manager/home')" style="margin-right: 5px; cursor: pointer;">首页</span> / <span
          style="margin-left: 5px;">{{ router.currentRoute.value.meta.name }}</span>
      </div>
      <div style="
          width: fit-content;
          display: flex;
          align-items: center;
          padding-right: 20px;
        ">
        <el-dropdown>
          <div style="display: flex; align-items: center">
            <img v-if="data.user?.avatar" :src="data.user?.avatar" alt="" style="
                width: 40px;
                height: 40px;
                border-radius: 50%;
                margin-right: 5px;
              " />
            <img v-else src="@/assets/imgs/avatar.jpg" alt="" style="
                width: 40px;
                height: 40px;
                border-radius: 50%;
                margin-right: 5px;
              " />
            <span>{{ data.user?.name }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/manager/person')">
                <span>个人信息</span>
              </el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/updatePassword')">
                <span>修改密码</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- 头部区域结束 -->

    <!-- 下方区域开始 -->
    <div class="main-container">
      <!-- 菜单区域开始 -->
      <div class="menu-container">
        <el-menu router :default-openeds="['1', '2']" :default-active="router.currentRoute.value.path" class="menu">
          <el-menu-item index="/manager/home">
            <el-icon>
              <House />
            </el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <Monitor />
              </el-icon>
              <span>信息管理</span>
            </template>
            <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">系统公告</el-menu-item>
            <el-menu-item index="/manager/notice" v-else>公告信息</el-menu-item>
            <el-menu-item index="/manager/introduction">旅游攻略</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon>
                <User />
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
            <el-menu-item index="/manager/user">普通用户信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      <!-- 菜单区域结束 -->

      <!-- 数据渲染区域开始 -->
      <div style="
          flex: 1;
          background-color: #efefff;
          padding: 10px;
          width: 0;
          min-height: calc(100vh - 60px);
        ">
        <Router-view @updateUser="updateUser" />
      </div>
      <!-- 数据渲染区域结束 -->
    </div>
    <!-- 下方区域结束 -->
  </div>
</template>

<script setup>
import router from '@/router/index.js';
import { reactive } from 'vue';
//
const data = reactive({ user: JSON.parse(localStorage.getItem('code_user') || "{}") })

const logout = () => {
  localStorage.removeItem('code_user')
  location.href = '/login'
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('code_user') || "{}")
}
</script>

<style>
:root {
  --primary-bg-color: #3a456b;
  --primary-hover-color: #7a9fff;
  --primary-active-color: #537bee;
  --primary-text-color: #ddd;
  --active-text-color: #fff;
  --hover-text-color: #333;
}

.el-menu--inline {
  background-color: var(--primary-bg-color);
  border-right: 1px solid var(--primary-active-color);
  color: var(--primary-text-color);
}

.el-menu--inline .el-menu-item {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  color: var(--primary-text-color);
}

.el-menu--inline .el-menu-item:hover {
  background-color: var(--primary-hover-color);
  color: var(--hover-text-color);
}

.el-menu--inline .el-menu-item.is-active {
  background-color: var(--primary-active-color);
  color: var(--active-text-color);
}

.el-menu--inline .el-sub-menu__title {
  color: var(--primary-text-color);
  background-color: var(--primary-bg-color);
}

.el-menu--inline .el-sub-menu__title:hover {
  background-color: var(--primary-hover-color);
  color: var(--hover-text-color);
}

.el-menu--inline .el-sub-menu__title.is-opened {
  background-color: var(--primary-active-color);
  color: var(--active-text-color);
}

.header {
  height: 60px;
  display: flex;
  background-color: var(--primary-bg-color);
}

.header span {
  color: var(--primary-text-color);
}

.logo-container {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding-left: 20px;
  width: 240px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  padding-left: 10px;
}

.header-spacer {
  flex: 1;
}

.main-container {
  display: flex;
  align-items: flex-start;
  height: 100%;
}

.menu-container {
  width: 240px;
}

.menu {
  min-height: calc(100vh - 60px);
  background: var(--primary-bg-color);
  border-color: var(--primary-bg-color);
}

.el-sub-menu__title {
  color: var(--primary-text-color);
  background-color: var(--primary-bg-color);
}

.el-sub-menu__title.is-opened {
  background-color: var(--primary-active-color);
  color: var(--active-text-color);
}

.el-menu-item {
  height: 50px;
  color: var(--primary-text-color);
}

.el-menu-item:hover {
  background-color: var(--primary-hover-color);
  color: var(--hover-text-color);
}

.el-menu-item.is-active {
  background-color: var(--primary-active-color);
  color: var(--active-text-color);
}

.el-menu .is-active {
  background-color: var(--primary-active-color);
  color: var(--active-text-color);
}

.el-sub-menu__title:hover {
  background: var(--primary-bg-color);
}

.el-menu-item:not(.is-active):hover {
  background: var(--primary-hover-color);
  color: var(--hover-text-color);
}

.el-menu--popup {
  background-color: var(--primary-bg-color) !important;
  color: var(--primary-text-color) !important;
}

.el-menu--popup .el-menu-item {
  color: var(--primary-text-color);
}

.el-menu--popup .el-menu-item:hover {
  background-color: var(--primary-hover-color);
  color: var(--hover-text-color);
}

.el-tooltip__trigger {
  outline: none;
}

.el-dropdown {
  cursor: pointer;
}
</style>
