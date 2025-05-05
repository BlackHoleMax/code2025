<template>
  <div class="not-found">
    <div style="width: 50%">
      <img style="width: 100%" src="@/assets/imgs/404.jpg" alt="" />
      <div style="color: purple; font-size: 1.625rem; text-align: center; position: relative; bottom: 6.25rem;">对不起您访问的页面不存在</div>
      <div style="text-align: center; font-size: 1.25rem">
        <a href="/">返回主页</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  background: linear-gradient(to bottom, #ebebeb, #f5f5f5);
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
</style>
