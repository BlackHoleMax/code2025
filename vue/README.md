# vue

## 配置element plus

### 安装需要的包

```shell
npm i element-plus @element-plus/icons-vue
```

### sass全局主题

#### npm安装所需包配置自动装配

```shell
npm i sass unplugin-auto-import unplugin-vue-components -D
```

#### 相关配置

```js
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
        // 如果不是两个插件都配置sass支持会出问题，这样配置在使用element plus组件的时候就不需要额外导入
        AutoImport({
            resolvers: [ElementPlusResolver({importStyle: "sass"})],  // 配置导入样式支持
        }),
        Components({
            resolvers: [ElementPlusResolver({importStyle: "sass"})], // 配置导入样式支持
        }),
    ],
    base: "/",
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: `@use "@/assets/css/index.scss" as *;`, // 导入路径下的scss文件
            },
        },
    },
    resolve: {
        alias: {
            "@": fileURLToPath(new URL("./src", import.meta.url)),
        },
    },
});
```

#### scss样式覆盖
```scss
@forward "element-plus/theme-chalk/src/common/var.scss" with (
  $colors: (
    "primary": (
      "base": #2c82ff,
    ),
    "success": (
      "base": #00d68f,
    ),
    "warning": (
      "base": #ffaa00,
    ),
    "danger": (
      "base": #ff4961,
    ),
    "info": (
      "base": #9915c2,
    ),
  )
);

```

### 配置图标组件

**首先导入所有图标文件作为一个对象**
```js
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
```

**将 ElementPlusIconsVue 对象中的所有图标组件全局注册到 Vue 应用 app 中**
```js
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
```

## vue-router4配置路由

```js
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // 从vite.config.js中导入基地址
  routes: [
    {
      path: "/",
      redirect: "/manager/home",  // 配置路由转发，打开根路径之后跳转到管理界面首页
    },
    {
      path: "/manager",
      component: () => import("../views/Manager.vue"),  // 导入的时候需要使用匿名函数，不然vue会出现警告
      children: [
        {
          path: "home",
          meta: { name: "主页" },
          component: () => import("../views/Home.vue"),
        },
        {
          path: "admin",
          meta: { name: "管理员信息" },
          component: () => import("../views/Admin.vue"),
        },
      ],
    },
    {
      path: "/notFound",
      component: () => import("../views/404.vue"),
    },
    { path: "/:pathMatch(.*)*", redirect: "/notFound" },  // 无匹配路由跳转到404页面
  ],
});

export default router;
```

## axios配置

```js
import axios from "axios";
import {ElMessage} from "element-plus";

// 创建 axios 实例
const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 20000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // const token = localStorage.getItem("token");
    // if (token) {
    //   config.headers["Authorization"] = `Bearer ${token}`;
    // }
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
      return response.data;
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

```

## el-form表单校验

- 在表单el-form上有三个必须的属性
```
ref = "formRef" :model="data.form" :rules="data.rules"
```

- 在el-form-item上 写上表单项的prop 
- 在rules里面定义验证规则 
- 定义formRef对象，作为表单的引用 
- 通过formRef对象进行表单的验证

## 表格crud业务流程

### 查询功能
业务流程：
用户在查询表单输入条件。
点击查询按钮，前端收集条件并验证。
向后端发送查询请求。
后端处理请求并返回结果。
前端展示结果。
代码实现：

```vue
<template>
  <div>
    <el-form :model="queryForm" ref="queryFormRef" label-width="80px">
      <el-form-item label="姓名">
        <el-input v-model="queryForm.name"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData">
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="handleEdit(scope.row)">编辑</el-button>
          <el-button @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const queryFormRef = ref(null);
const queryForm = ref({
  name: ''
});
const tableData = ref([]);

const handleQuery = async () => {
  // 简单验证
  if (!queryForm.value.name) {
    return;
  }
  // 模拟请求
  try {
    const response = [
      { id: 1, name: '张三', age: 20 },
      { id: 2, name: '李四', age: 25 }
    ];
    tableData.value = response;
  } catch (error) {
    console.error('查询失败:', error);
  }
};

const handleReset = () => {
  queryForm.value = {
    name: ''
  };
  queryFormRef.value.resetFields();
  tableData.value = [];
};
</script>
```
### 新增功能
业务流程：

点击新增按钮，弹出新增表单。
用户输入数据并验证。
发送新增请求。
后端处理并返回结果。
前端更新列表。
代码实现：
```vue
<template>
  <div>
    <el-button @click="openAddDialog">新增</el-button>
    <el-dialog :visible.sync="addDialogVisible" title="新增数据">
      <template #content>
        <el-form :model="addForm" ref="addFormRef" label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="addForm.name"></el-input>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model.number="addForm.age"></el-input>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
    <el-table :data="tableData">
      <!-- 表格列定义 -->
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const addDialogVisible = ref(false);
const addFormRef = ref(null);
const addForm = ref({
  name: '',
  age: 0
});
const tableData = ref([]);

const openAddDialog = () => {
  addDialogVisible.value = true;
};

const handleAdd = async () => {
  // 简单验证
  if (!addForm.value.name ||!addForm.value.age) {
    return;
  }
  // 模拟请求
  try {
    const newData = {
      id: Date.now(),
      ...addForm.value
    };
    tableData.value.push(newData);
    addDialogVisible.value = false;
    addForm.value = {
      name: '',
      age: 0
    };
    addFormRef.value.resetFields();
  } catch (error) {
    console.error('新增失败:', error);
  }
};
</script>
```
### 删除功能
业务流程：

点击删除按钮，弹出确认框。
用户确认后，发送删除请求。
后端处理并返回结果。
前端更新列表。
代码实现：
```vue
<template>
  <div>
    <el-table :data="tableData">
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessageBox } from 'element-plus';

const tableData = ref([
  { id: 1, name: '张三', age: 20 },
  { id: 2, name: '李四', age: 25 }
]);

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该条数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    // 模拟请求
    tableData.value = tableData.value.filter(item => item.id!== id);
  } catch (error) {
    console.log('取消删除');
  }
};
</script>
```
### 批量删除功能
业务流程：

用户勾选要删除的数据。
点击批量删除按钮，弹出确认框。
用户确认后，发送批量删除请求。
后端处理并返回结果。
前端更新列表。
代码实现：
```vue
<template>
  <div>
    <el-table :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
    </el-table>
    <el-button @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessageBox } from 'element-plus';

const tableData = ref([
  { id: 1, name: '张三', age: 20 },
  { id: 2, name: '李四', age: 25 }
]);
const selectedRows = ref([]);

const handleSelectionChange = (rows) => {
  selectedRows.value = rows;
};

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除所选数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    // 模拟请求
    const ids = selectedRows.value.map(item => item.id);
    tableData.value = tableData.value.filter(item =>!ids.includes(item.id));
    selectedRows.value = [];
  } catch (error) {
    console.log('取消批量删除');
  }
};
</script>
```
### 编辑功能
业务流程：

点击编辑按钮，弹出编辑表单并填充数据。
用户修改数据并验证。
发送编辑请求。
后端处理并返回结果。
前端更新列表。
代码实现：

```vue
<template>
  <div>
    <el-table :data="tableData">
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="openEditDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="editDialogVisible" title="编辑数据">
      <template #content>
        <el-form :model="editForm" ref="editFormRef" label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="editForm.name"></el-input>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model.number="editForm.age"></el-input>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const editDialogVisible = ref(false);
const editFormRef = ref(null);
const editForm = ref({
  id: null,
  name: '',
  age: 0
});
const tableData = ref([
  { id: 1, name: '张三', age: 20 },
  { id: 2, name: '李四', age: 25 }
]);

const openEditDialog = (row) => {
  editForm.value = { ...row };
  editDialogVisible.value = true;
};

const handleEdit = async () => {
  // 简单验证
  if (!editForm.value.name ||!editForm.value.age) {
    return;
  }
  // 模拟请求
  try {
    const index = tableData.value.findIndex(item => item.id === editForm.value.id);
    if (index!== -1) {
      tableData.value[index] = { ...editForm.value };
    }
    editDialogVisible.value = false;
    editForm.value = {
      id: null,
      name: '',
      age: 0
    };
    editFormRef.value.resetFields();
  } catch (error) {
    console.error('编辑失败:', error);
  }
};
</script>
```
### Tips

关于插槽获取整行数据在 el-table 的自定义列中，使用插槽可以获取整行数据。如上述代码中的操作列：

```vue
<el-table-column label="操作">
  <template #default="scope">
    <!-- scope.row 即为当前行的数据 -->
    <el-button @click="handleEdit(scope.row)">编辑</el-button>
    <el-button @click="handleDelete(scope.row.id)">删除</el-button>
  </template>
</el-table-column>
```

通过 scope.row 可以访问当前行的所有数据。

关于 @selection-change 批量获取数据在 el-table 中使用 @selection-change 事件可以监听用户勾选数据的变化，从而批量获取数据。如批量删除功能中的代码：

```vue
<el-table :data="tableData" @selection-change="handleSelectionChange">
<el-table-column type="selection"></el-table-column>
  <!-- 其他列 -->
</el-table>
const selectedRows = ref([]);

const handleSelectionChange = (rows) => {
selectedRows.value = rows;
};
```

handleSelectionChange 方法会在用户勾选或取消勾选数据时被调用，rows 参数即为当前被勾选的数据数组。
