<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock, MapLocation, Camera, Star } from "@element-plus/icons-vue";
import http from "@/utils/http";
import config from "@/config/index.js";

const router = useRouter();
const registerFormRef = ref();
const loading = ref(false);

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: ""
});

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: "blur" }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
    loading.value = true;

    const res = await http.post("/register", {
      username: registerForm.username,
      password: registerForm.password
    });
    
    if (res.code === 200) {
      ElMessage.success("注册成功，请登录");
      await router.push("/login");
    } else {
      ElMessage.error(res.message || "注册失败");
    }
  } catch (error) {
    console.error("注册失败:", error);
    ElMessage.error("注册失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// 跳转到登录页面
const goToLogin = () => {
  router.push("/login");
};
</script>

<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decorations">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- 注册框 -->
    <div class="register-box">
      <!-- 左侧背景图片区域 -->
      <div class="register-left">
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

      <!-- 右侧注册表单 -->
      <div class="register-right">
        <div class="form-container">
          <h2 class="form-title">加入我们</h2>
          <p class="form-subtitle">开始您的旅行探索之旅</p>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-width="0"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="register-button"
                @click="handleRegister"
              >
                {{ loading ? "注册中..." : "注 册" }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <div class="login-link">
              <span>已有账号？</span>
              <el-button type="text" @click="goToLogin">立即登录</el-button>
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
.register-container {
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

.bg-decorations {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.6;
}

.bg-circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  left: -200px;
  background: linear-gradient(135deg, #1976d2 0%, #64b5f6 100%);
}

.bg-circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -100px;
  background: linear-gradient(135deg, #4fc3f7 0%, #b3e5fc 100%);
}

.bg-circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: 100px;
  background: linear-gradient(135deg, #bbdefb 0%, #e3f2fd 100%);
}

.bg-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.3) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.3) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.3;
}

/* 注册框样式 */
.register-box {
  position: relative;
  display: flex;
  width: 900px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: var(--shadow-medium);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  z-index: 2001;
  animation: modalFadeIn 0.3s ease-out;
}

/* 左侧背景图片区域 */
.register-left {
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
  z-index: 1;
}

.left-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  padding: 0 40px;
}

/* Logo 区域样式 */
.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

.logo-decoration {
  display: flex;
  gap: 15px;
  margin-left: 20px;
}

.decoration-icon {
  font-size: 24px;
  opacity: 0.8;
  animation: float 3s ease-in-out infinite;
}

.decoration-icon:nth-child(2) {
  animation-delay: 1s;
}

.decoration-icon:nth-child(3) {
  animation-delay: 2s;
}

.left-content .logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.left-content .system-name {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.left-content .welcome-text {
  font-size: 16px;
  opacity: 0.95;
  line-height: 1.6;
  margin-bottom: 25px;
  font-weight: 300;
}

/* 标语文字样式 */
.slogan-text {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 25px;
  opacity: 0.9;
  font-style: italic;
}

/* 功能列表样式 */
.features-list {
  margin-bottom: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  opacity: 0.85;
}

.feature-icon {
  margin-right: 10px;
  font-size: 16px;
}

/* 底部文字样式 */
.bottom-text {
  font-size: 14px;
  opacity: 0.8;
  line-height: 1.5;
  margin-top: auto;
}

/* 右侧注册表单 */
.register-right {
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
  text-align: center;
  margin-bottom: 8px;
  color: var(--text-primary);
  font-size: 32px;
  font-weight: 700;
}

.form-subtitle {
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-secondary);
  font-size: 16px;
  opacity: 0.8;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 12px;
  margin-top: 10px;
  background: var(--gradient-primary) !important;
  border: none !important;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.login-link {
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.login-link span {
  margin-right: 8px;
}

.login-link .el-button {
  color: var(--primary-color);
  font-weight: 500;
  padding: 0;
  text-decoration: none;
  transition: all 0.3s ease;
}

.login-link .el-button:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

.copyright {
  margin: 0;
  font-size: 12px;
  color: var(--text-tertiary);
  opacity: 0.8;
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border-light) inset;
  background-color: var(--bg-secondary);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--border-medium) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary-color) inset !important;
  background-color: var(--bg-primary);
}

/* 下拉框样式 */
:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border-light) inset;
  background-color: var(--bg-secondary);
  transition: all 0.3s ease;
}

:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--border-medium) inset;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary-color) inset !important;
  background-color: var(--bg-primary);
}

:deep(.el-input__inner) {
  height: 100%;
  font-size: 14px;
}

/* 表单项样式 */
:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

/* 浮动动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    flex-direction: column;
    width: 90%;
    height: auto;
    max-height: 90vh;
    overflow-y: auto;
  }

  .register-left {
    min-height: 200px;
    padding: 30px 20px;
  }

  .register-right {
    padding: 30px 20px;
  }

  .left-content .system-name {
    font-size: 24px;
  }

  .form-title {
    font-size: 28px;
  }

  .features-list {
    display: none;
  }

  .logo-decoration {
    gap: 10px;
  }

  .decoration-icon {
    font-size: 20px;
  }
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

/* 响应式设计 */
@media (max-width: 960px) {
  .register-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .register-left,
  .register-right {
    width: 100%;
  }

  .register-left {
    padding: 40px;
    height: 200px;
  }

  .register-right {
    padding: 40px 20px;
  }
}
</style>