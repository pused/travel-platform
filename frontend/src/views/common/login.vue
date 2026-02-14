<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock, UserFilled, MapLocation, Camera, Star } from "@element-plus/icons-vue";
import useUserStore from "@/stores/userStore";
import http from "@/utils/http";
import { roles } from "@/utils/menu.js";
import config from "@/config/index.js";

const userStore = useUserStore();
const router = useRouter();
const loginFormRef = ref();
const loading = ref(false);

const loginForm = reactive({
  username: "caiya",
  password: "123",
});

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  try {
    await loginFormRef.value.validate();
    loading.value = true;

    const res = await http.post("/login", loginForm);
      if (res.code == 200) {
        const role = res.data.role;
        if (role === "admin") {
          userStore.setAdminSession(res.data.token, res.data, role);
        } else {
          userStore.setFrontSession(res.data.token, res.data, role);
        }
        ElMessage.success("登录成功");
        if (role == "admin") {
          router.push("/");
        } else {
          router.push("/front/home");
        }
      } else if (res.code === 520) {
        ElMessage.error("无权限");
      }

    
  } catch (error) {
    console.error("登录失败:", error);
    ElMessage.error("登录失败，请检查用户名和密码");
  } finally {
    loading.value = false;
  }
};

// 跳转到注册页面
const goToRegister = () => {
  router.push("/register");
};
</script>

<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decorations">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- 登录框 -->
    <div class="login-box">
      <!-- 左侧背景图片区域 -->
      <div class="login-left">
        <div class="left-overlay"></div>
        <div class="left-content">
          <div class="logo-section">
            <img src="@/assets/logo.png" alt="系统Logo" class="logo" />
            <div class="logo-decoration">
              <el-icon class="decoration-icon"><MapLocation /></el-icon>
              <el-icon class="decoration-icon"><Camera /></el-icon>
              <el-icon class="decoration-icon"><Star /></el-icon>
            </div>
          </div>
          <h2 class="system-name">{{ config.frontName }}</h2>
          <p class="welcome-text">{{ config.welcome }}</p>
          <p class="slogan-text">{{ config.slogan }}</p>
          
          <div class="features-list">
            <div 
              v-for="(feature, index) in config.features" 
              :key="index" 
              class="feature-item"
            >
              <el-icon class="feature-icon"><Star /></el-icon>
              <span>{{ feature }}</span>
            </div>
          </div>
          
          <div class="bottom-text">
            <p>{{ config.description }}</p>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="form-container">
          <h2 class="form-title">{{ config.loginTitle }}</h2>
          <p class="form-subtitle">{{ config.loginSubtitle }}</p>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            label-width="0"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                {{ loading ? "登录中..." : "登 录" }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <div class="register-link">
              <span>还没有账号？</span>
              <el-button type="text" @click="goToRegister">立即注册</el-button>
            </div>
            <p class="copyright">© {{ config.year }} {{ config.frontName }} 版权所有</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 背景样式 */
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  background: url("@/assets/bg.png") no-repeat;
  background-size: cover;
  overflow: hidden;
}

/* 登录框样式 */
.login-box {
  position: relative;
  display: flex;
  width: 900px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 32px var(--shadow-medium);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  z-index: 2001;
  animation: modalFadeIn 0.3s ease-out;
}

/* 左侧背景图片区域 */
.login-left {
  width: 45%;
  background-size: cover;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.left-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-primary);
  opacity: 0.95;
}

.left-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  padding: 0 40px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.left-content .logo {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  margin-bottom: 15px;
  box-shadow: 0 4px 16px rgba(255, 255, 255, 0.3);
}

.logo-decoration {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.decoration-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  animation: float 3s ease-in-out infinite;
}

.decoration-icon:nth-child(2) {
  animation-delay: 1s;
}

.decoration-icon:nth-child(3) {
  animation-delay: 2s;
}

.left-content .system-name {
  font-size: 32px;
  margin-bottom: 12px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.left-content .welcome-text {
  font-size: 18px;
  opacity: 0.95;
  margin-bottom: 8px;
  font-weight: 500;
}

.slogan-text {
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 25px;
  font-style: italic;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 25px;
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

.feature-icon {
  font-size: 16px;
  color: var(--secondary-color);
}

.bottom-text {
  margin-top: auto;
  text-align: center;
}

.bottom-text p {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
}

/* 右侧登录表单 */
.login-right {
  width: 55%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 350px;
}

.form-title {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 8px;
  text-align: center;
  font-weight: 700;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 30px;
  text-align: center;
  opacity: 0.8;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 12px;
  margin-top: 10px;
  background: var(--gradient-primary);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px var(--shadow-medium);
}

.remember-me {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.forgot-password {
  font-size: 14px;
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.register-link {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.register-link span {
  margin-right: 8px;
}

.copyright {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border-light) inset;
  background: var(--bg-secondary);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--border-medium) inset;
  background: var(--bg-primary);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset;
  background: var(--bg-primary);
}

:deep(.el-input__inner) {
  height: 100%;
}

/* 下拉框样式优化 */
:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__wrapper) {
  height: 48px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border-light) inset;
  background: var(--bg-secondary);
  transition: all 0.3s ease;
}

:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--border-medium) inset;
  background: var(--bg-primary);
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset;
  background: var(--bg-primary);
}

:deep(.el-select .el-input__inner) {
  height: 100%;
}

/* 动画 */
@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-8px);
  }
}

/* 响应式设计 */
@media (max-width: 960px) {
  .login-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .login-left,
  .login-right {
    width: 100%;
  }

  .login-left {
    padding: 40px;
    height: 200px;
  }

  .login-right {
    padding: 40px 20px;
  }
}
</style>
