<script setup>
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()
</script>

<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background:#001529; display:flex; flex-direction:column">
      <div style="color:#fff; font-size:18px; font-weight:bold; padding:20px 16px">VagueAssistant</div>
      <el-menu
        :default-active="route.path"
        background-color="#001529"
        text-color="#ccc"
        active-text-color="#fff"
        router
      >
        <el-menu-item index="/"><el-icon><ChatDotRound /></el-icon>对话</el-menu-item>
        <el-menu-item index="/kb"><el-icon><Files /></el-icon>知识库</el-menu-item>
        <el-menu-item index="/agent"><el-icon><Avatar /></el-icon>Agent</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main style="padding:0; overflow:hidden">
      <router-view />
    </el-main>
  </el-container>
</template>
