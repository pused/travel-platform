<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import Header from "./Header.vue";
import Footer from "./Footer.vue";

// 页面加载状态
const loading = ref(true);

const route = useRoute();
const isFullScreenPage = computed(() => route.meta.hideFooter === true);
const showFooter = computed(() => !isFullScreenPage.value);

// 组件挂载后
onMounted(() => {
  // 模拟页面加载完成
  setTimeout(() => {
    loading.value = false;
  }, 300);
});
</script>

<template>
  <div :class="['front-layout', { 'front-layout--full': isFullScreenPage }]">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
    </div>

    <!-- 主布局 -->
    <div v-else class="layout-container">
      <!-- 头部 -->
      <Header />

      <!-- 主要内容区域 -->
      <main class="main-content">
        <router-view />
      </main>

      <!-- 底部 -->
      <Footer v-if="showFooter" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.front-layout {
  min-height: 100vh;
  height: auto;
  display: flex;
  flex-direction: column;
  background: #ffffff;

  .layout-container {
    flex: 1;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  .main-content {
    flex: 1;
    padding-top: 0;
    padding-bottom: 20px;
    background: #ffffff;
    min-height: calc(100vh - 70px);
  }
}

.front-layout--full {
  height: 100vh;
  overflow: hidden;

  .layout-container {
    min-height: 100%;
  }

  .main-content {
    padding-bottom: 0;
    min-height: 100%;
  }
}

.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;

  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;

    .loading-spinner {
      width: 50px;
      height: 50px;
      border: 4px solid #f0f0f0;
      border-top: 4px solid #1890ff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .loading-text {
      color: #595959;
      font-size: 14px;
      margin: 0;
    }
  }

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
}
</style>