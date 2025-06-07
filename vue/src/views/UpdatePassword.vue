<template>
    <div class="card" style="width: 50%;">
        <div style="font-size: 16px;">修改密码</div>
        <el-form ref="formRef" :model="data.user" :rules="data.rules" label-width="80px"
            style="padding: 20px 30px 10px 0">
            <el-form-item prop="oldPassword" label="原密码">
                <el-input show-password v-model="data.user.password" autocomplete="off" prefix-icon="Lock"
                    placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item prop="newPassword" label="新密码">
                <el-input show-password v-model="data.user.newPassword" autocomplete="off" prefix-icon="Lock"
                    placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item prop="confirmPassword" label="确认密码">
                <el-input show-password v-model="data.user.confirmPassword" autocomplete="off" prefix-icon="Lock"
                    placeholder="请确认新密码" />
            </el-form-item>
        </el-form>
        <div style="text-align: right;">
            <el-button type="primary" style="padding: 18px 35px;" @click="updatePassword">保存</el-button>
        </div>
    </div>
</template>
<script setup>
import request from '@/utils/request';
import { reactive, ref } from 'vue';

const validatePass = (rule, value, callback) => {
    if (value !== data.user.newPassword) {
        callback(new Error('两次输入的密码不匹配！'))
    } else {
        callback()
    }
}

const formRef = ref()

const data = reactive({
    user: JSON.parse(localStorage.getItem("code_user") || "{}"),
    rules: {
        oldPassword: [
        ],
        newPassword: [
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { pattern: /^\S*(?=\S{5,15})(?=\S*\d)(?=\S*[A-Z])(?=\S*[a-z])(?=\S*[!@#$%^&*? ])\S*$/, message: '密码必须是5-15位大写字母小写字母数字和特殊字符' },
        ],
        confirmPassword: [
            { required: true, message: '请再次确认密码', trigger: 'blur' },
            { validator: validatePass, trigger: 'blur' }
        ],
    }
});


const updatePassword = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/updatePassword', data.user).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功")
                    setInterval(() => {
                        localStorage.removeItem('code_user')
                        location.href = '/login'
                    }, 500);
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
</script>