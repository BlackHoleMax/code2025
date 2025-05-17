<template>
    <div class="bg">
        <div class="card">
            <div style="margin-bottom: 30px; font-size: 24px; text-align: center; font-weight: bold;">欢迎注册</div>
            <el-form ref="formRef" :model="data.form" :rules="data.rules">
                <el-form-item prop="username">
                    <el-input size="large" v-model="data.form.username" autocomplete="off" prefix-icon="User"
                        placeholder="请输入账号" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input size="large" show-password v-model="data.form.password" autocomplete="off"
                        prefix-icon="Lock" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input size="large" show-password v-model="data.form.confirmPassword" autocomplete="off"
                        prefix-icon="Lock" placeholder="请再次确认密码" />
                </el-form-item>
            </el-form>
            <div>
                <el-button type="primary" size="large" style="width: 100%; margin-bottom: 20px;" @click="register">注
                    册</el-button>
            </div>
            <div style="text-align: right;">
                已有账号？请<a href="/login" style="color: #248243;" @click="router.push('/login')">登录</a>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router/index';
import request from '@/utils/request';
import { reactive, ref } from 'vue';

const validatePass = (rule, value, callback) => {
    if (value !== data.form.password) {
        callback(new Error('两次输入的密码不匹配！'))
    } else {
        callback()
    }
}

const formRef = ref()

const data = reactive({
    form: {},
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur' },
            { min: 5, max: 10, message: '用户名必须是5-10位的字符', trigger: 'blur' },
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { pattern: /^\S*(?=\S{5,15})(?=\S*\d)(?=\S*[A-Z])(?=\S*[a-z])(?=\S*[!@#$%^&*? ])\S*$/, message: '密码必须是5-15位大写字母小写字母数字和特殊字符' }, // Admin1@123
        ],
        confirmPassword: [
            { required: true, message: '请再次确认密码', trigger: 'blur' },
            { validator: validatePass, trigger: 'blur' }
        ]
    }
})

const register = () => {
    request.post('/register', data.form).then(res => {
        if (res.code === '200') {
            localStorage.setItem('user_data', JSON.stringify(res.data || '{}'))
            ElMessage.success('注册成功')
            router.push('/login')
        } else {
            ElMessage.error(res.msg)
        }
    })
}
</script>

<style scoped>
.bg {
    width: 100%;
    height: 100vh;
    background: linear-gradient(to right, #6cc77b, #abff7a);
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