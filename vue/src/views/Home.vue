<template>
    <div>
        <div class="card">您好！欢迎您使用本系统，祝您使用愉快！</div>
        <div class="card" style="margin-top: 10px; width: 50%;">
            <el-timeline style="max-width: 600px">
                <el-timeline-item v-for="(item, index) in data.noticeData" color="#0bbd87" :key="index"
                    :timestamp="item.time">
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.content }}</p>
                </el-timeline-item>
            </el-timeline>
        </div>
    </div>
</template>

<script setup>
import request from '@/utils/request';
import { reactive } from 'vue';

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || "{}"),
    noticeData: []
})

const loadNotice = () => {
    request.get('/notice/selectAll').then(res => {
        if (res.code === '200') {
            data.noticeData = res.data
            if (data.noticeData.length > 3) {
                data.noticeData = data.noticeData.slice(0, 3)
            }
        } else {
            ElMessage.error(res.msg)
        }
    })
}
loadNotice()
</script>