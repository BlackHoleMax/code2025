<template>
    <div class="bg">
        <div class="card">
            <div style="margin-bottom: 30px; font-size: 24px; text-align: center; font-weight: bold;">欢迎登录</div>
            <el-form ref="formRef" :model="data.form" :rules="data.rules">
                <el-form-item prop="username">
                    <el-input size="large" v-model="data.form.username" autocomplete="off" prefix-icon="User"
                        placeholder="请输入账号" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input size="large" show-password v-model="data.form.password" autocomplete="off"
                        prefix-icon="Lock" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item prop="role">
                    <el-select size="large" style="width: 100%;" v-model="data.form.role">
                        <el-option label="管理员" value="ADMIN" />
                        <el-option label="普通用户" value="USER" />
                    </el-select>
                </el-form-item>
            </el-form>
            <div>
                <el-button type="primary" size="large" style="width: 100%; margin-bottom: 20px;" @click="login">登
                    录</el-button>
            </div>
            <div style="text-align: right;">
                还没有账号？请<a href="/register" style="color: #274afa;">注册</a>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router/index';
import request from '@/utils/request';
import { reactive, ref } from 'vue';

const formRef = ref()
const data = reactive({
    form: { role: 'ADMIN' },
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur' },
            { min: 3, message: '账号最少三位', trigger: 'blur' }
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 5, message: '密码最少五位', trigger: 'blur' }
        ],
    }
})

const login = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/login', data.form).then(res => {
                if (res.code === '200') {
                    localStorage.setItem('code_user', JSON.stringify(res.data || '{}'))
                    ElMessage.success('登录成功')
                    router.push('/')
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
</script>

<style scoped>
.bg {
    width: 100%;
    height: 100vh;
    background: linear-gradient(to right, #6c80c7, #c37aff);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 350px;
    border-radius: 5px;
    box-shadow: 0 0 0 10px rgba(0, 0, 10px, 0.1);
    background-color: #fff;
    opacity: 0.8;
    padding: 30px 20px;
}
</style>