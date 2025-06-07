<template>
    <div class="card" style="width: 50%;">
        <div style="font-size: 15px;">个人中心</div>
        <el-form ref="formRef" :model="data.user" label-width="80px" style="padding: 20px 30px 10px 0">
            <el-form-item prop="username" label="账号">
                <el-input v-model="data.user.username" autocomplete="off" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item prop="name" label="名称">
                <el-input v-model="data.user.name" autocomplete="off" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item prop="phone" label="电话">
                <el-input v-model="data.user.phone" autocomplete="off" placeholder="请输入电话" />
            </el-form-item>
            <el-form-item prop="email" label="邮箱">
                <el-input v-model="data.user.email" autocomplete="off" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item prop="avatar" label="头像">
                <el-upload action="http://localhost:8080/files/upload" :headers="{ token: data.user.token }"
                    :on-success="handleFileSuccess" list-type="picture" :limit="1" :on-exceed="handleExceed">
                    <el-button type="primary">上传头像</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
        <div style="text-align: right;">
            <el-button type="primary" style="padding: 18px 35px;" @click="update">保存</el-button>
        </div>
    </div>
</template>
<script setup>
import request from '@/utils/request';
import { reactive } from 'vue';

const data = reactive({
    user: JSON.parse(localStorage.getItem("code_user") || "{}"),
});

const handleFileSuccess = (res) => {
    data.user.avatar = res.data
}

const emit = defineEmits([`updateUser`])

const update = () => {
    let url
    if (data.user.role === "ADMIN") {
        url = "/admin/update"
    } else if (data.user.role === "USER") {
        url = "/user/update"
    } else {
        ElMessage.error("未知角色，无法保存")
        return
    }
    request.put(url, data.user).then(res => {
        if (res.code === '200') {
            ElMessage.success("更新成功")
            localStorage.setItem("code_user", JSON.stringify(data.user))
            emit('updateUser')
        } else {
            ElMessage.error(res.msg);
        }
    })
}

const handleExceed = () => {
    ElMessage.error('超出最大上传数量限制，最多只能上传 1 个文件')
}
</script>