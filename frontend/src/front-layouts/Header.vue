<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { User, ArrowDown, House, MapLocation, Document, CreditCard, 
Service } from "@element-plus/icons-vue";
import { getImageUrl } from "@/utils/system";
import config from "@/config";
import useUserStore from "@/stores/userStore";

const router = useRouter();
const route = useRoute();

// 用户信息
const userStore = useUserStore();
const userInfo = computed(() => userStore.frontUserInfo || null);
const isLoggedIn = computed(() => !!userStore.frontToken);

// 菜单数据
const menuList = ref([
  { name: "首页", path: "/front/home", icon: House },
  { name: "景点列表", path: "/front/attractions", icon: MapLocation },
  { name: "攻略列表", path: "/front/strategies", icon: Document },
  { name: "门票列表", path: "/front/tickets", icon: CreditCard },
  { name: "AI 助手", path: "/front/ai-chat", icon: Service }
]);

// 当前激活的菜单
const activeMenu = ref("/front/home");

// 滚动状态
const isScrolled = ref(false);

// 滚动监听
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  isScrolled.value = scrollTop > 50; // 滚动超过50px时显示背景
};

// 获取用户信息（使用已持久化的前台会话）
const getUserInfo = () => {
  if (userStore.frontToken && userStore.frontUserInfo) {
    return;
  }
};

// 菜单点击
const handleMenuClick = (path: string) => {
  activeMenu.value = path;
  router.push(path);
};

// 登录
const handleLogin = () => {
  router.push("/login");
};

// 注册
const handleRegister = () => {
  router.push("/register");
};

// 退出登录
const handleLogout = () => {
  userStore.logoutFront();
  ElMessage.success("退出登录成功");
  router.push("/login");
};

// 个人中心
const handleProfile = () => {
  router.push("/front/person");
};

// 跳转页面
const handleJump = (path: string) => {
  router.push(path);
};



// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      handleProfile();
      break;
    case 'myTicketOrders':
      handleJump('/front/myTicketOrders');
      break;
    case 'myCollections':
      handleJump('/front/myCollections');
      break;
    case 'myLikes':
      handleJump('/front/myLikes');
      break;
    case 'myStrategy':
      handleJump('/front/myStrategy');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};

onMounted(() => {
  getUserInfo();
  window.addEventListener('scroll', handleScroll);
  handleScroll(); // 初始化滚动状态
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

watch(() => route.path, newPath => {
  activeMenu.value = newPath;
}, {immediate: true})
</script>

<template>
  <header class="front-header" :class="{ 'scrolled': isScrolled }">
    <div class="header-container">
      <!-- 左侧：Logo + 系统名称 -->
      <div class="header-left">
        <div class="logo-section" @click="handleMenuClick('/front')">
          <img src="../assets/logo.png" alt="Logo" class="logo" />
          <h1 class="system-name">{{ config.frontName }}</h1>
        </div>
      </div>

      <!-- 中间：菜单栏 -->
      <nav class="header-center">
        <ul class="nav-menu">
          <li
            v-for="menu in menuList"
            :key="menu.path"
            class="nav-item"
            :class="{ active: activeMenu === menu.path }"
            @click="handleMenuClick(menu.path)"
          >
            <el-icon class="menu-icon">
              <component :is="menu.icon" />
            </el-icon>
            <span class="menu-text">{{ menu.name }}</span>
          </li>
        </ul>
      </nav>

      <!-- 右侧：登录操作 -->
      <div class="header-right">
        <!-- 未登录状态 -->
        <div v-if="!isLoggedIn" class="auth-buttons">
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button @click="handleRegister">注册</el-button>
        </div>

        <!-- 已登录状态 -->
        <div v-else class="user-info">
          <el-dropdown @command="handleCommand">
            <div class="user-profile">
              <el-avatar
                :src="userInfo.avatar ? getImageUrl(userInfo.avatar) : ''"
                :icon="User"
                :size="32"
              />
              <span class="username">{{ userInfo.nickName || userInfo.username }}</span>
              <el-icon class="dropdown-icon">
                <ArrowDown />
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="myStrategy">我的攻略</el-dropdown-item>
                <el-dropdown-item command="myTicketOrders">我的订单</el-dropdown-item>
                <el-dropdown-item command="myCollections">我的收藏</el-dropdown-item>
                <el-dropdown-item command="myLikes">我的点赞</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<style lang="scss" scoped>
.front-header {
  background: transparent;
  backdrop-filter: none;
  box-shadow: none;
  border-bottom: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: all 0.3s ease;

  &.scrolled {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);

    .header-left .logo-section {
      &:hover {
        background: rgba(16, 185, 129, 0.05);
      }

      .system-name {
        color: var(--primary-color);
        text-shadow: none;
        background: linear-gradient(135deg, var(--primary-600), var(--primary-400));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .header-center .nav-menu .nav-item {
      color: var(--gray-600);
      text-shadow: none;

      &:hover {
        color: var(--primary-color);
        background: rgba(16, 185, 129, 0.1);
      }

      &.active {
        color: var(--primary-color);
        background: rgba(16, 185, 129, 0.15);
      }
    }

    .header-right {
      .auth-buttons .el-button:not(.el-button--primary) {
        color: var(--gray-600);
        border-color: var(--border-medium);
        background: #ffffff;
        backdrop-filter: none;

        &:hover {
          color: var(--primary-color);
          border-color: var(--primary-color);
          background: rgba(16, 185, 129, 0.05);
        }
      }

      .user-info .user-profile {
        &:hover {
          background: rgba(16, 185, 129, 0.05);
          backdrop-filter: none;
        }

        .username {
          color: var(--text-primary);
          text-shadow: none;
        }
      }
    }
  }

  .header-container {
    max-width: 85%;
    margin: 0 auto;
    padding: 0 30px;
    height: 75px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(90deg, 
        rgba(255, 255, 255, 0.08) 0%, 
        rgba(255, 255, 255, 0.04) 50%, 
        rgba(255, 255, 255, 0.08) 100%);
      border-radius: 0 0 20px 20px;
      opacity: 0;
      transition: opacity 0.3s ease;
      pointer-events: none;
    }

    &:hover::before {
      opacity: 1;
    }
  }

  .header-left {
    .logo-section {
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s ease;
      padding: 8px 16px;
      border-radius: 12px;

      &:hover {
        background: rgba(255, 255, 255, 0.15);
        transform: translateY(-1px);
        box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
      }

      .logo {
        width: 40px;
        height: 40px;
        margin-right: 12px;
        transition: transform 0.3s ease;
      }

      &:hover .logo {
        transform: scale(1.05) rotate(5deg);
      }

      .system-name {
        font-size: 24px;
        font-weight: 700;
        color: #ffffff;
        margin: 0;
        white-space: nowrap;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        transition: all 0.3s ease;
        background: linear-gradient(135deg, #ffffff, rgba(16, 185, 129, 0.8));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
  }

  .header-center {
    flex: 1;
    display: flex;
    justify-content: center;

    .nav-menu {
      display: flex;
      list-style: none;
      margin: 0;
      padding: 0;
      gap: 40px;

      .nav-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 20px;
        font-size: 16px;
        font-weight: 500;
        color: #ffffff;
        cursor: pointer;
        border-radius: 12px;
        transition: all 0.3s ease;
        position: relative;
        // text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        // backdrop-filter: blur(10px);

        .menu-icon {
          font-size: 18px;
          transition: all 0.3s ease;
        }

        .menu-text {
          transition: all 0.3s ease;
        }

        &:hover {
          color: #ffffff;
          background: rgba(255, 255, 255, 0.15);
          backdrop-filter: blur(20px);
          transform: translateY(-2px);
          box-shadow: 0 8px 25px rgba(255, 255, 255, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.3);
          
          .menu-icon {
            transform: scale(1.1);
            color: #ffffff;
          }
        }

        &.active {
          color: #ffd700;
          background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 223, 0, 0.3));
          backdrop-filter: blur(20px);
          font-weight: 600;
          box-shadow: 0 8px 25px rgba(255, 215, 0, 0.3);
          border: 1px solid rgba(255, 215, 0, 0.4);

          .menu-icon {
            color: #ffd700;
            transform: scale(1.1);
          }

          &::after {
            content: "";
            position: absolute;
            bottom: -2px;
            left: 50%;
            transform: translateX(-50%);
            width: 30px;
            height: 3px;
            background: linear-gradient(90deg, #ffd700, #ffed4e);
            border-radius: 2px;
            box-shadow: 0 2px 8px rgba(255, 215, 0, 0.6);
          }
        }
      }
    }
  }

  .header-right {
    .auth-buttons {
      display: flex;
      gap: 12px;

      .el-button {
        padding: 8px 20px;
        font-weight: 500;
        border-radius: 8px;
        
        &.el-button--primary {
          background: linear-gradient(135deg, var(--primary-400), var(--primary-600));
          border: none;
          color: #ffffff;
          box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
          
          &:hover {
            background: linear-gradient(135deg, var(--primary-500), var(--primary-700));
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
          }
        }
        
        &:not(.el-button--primary) {
          color: #ffffff;
          border-color: rgba(255, 255, 255, 0.3);
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          
          &:hover {
            color: #ffd700;
            border-color: rgba(255, 215, 0, 0.6);
            background: rgba(255, 215, 0, 0.1);
            backdrop-filter: blur(20px);
          }
        }
      }
    }

    .user-info {
      .user-profile {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 6px 12px;
        border-radius: 20px;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.15);
          backdrop-filter: blur(20px);
          border: 1px solid rgba(255, 255, 255, 0.3);
        }

        .username {
          font-size: 14px;
          font-weight: 500;
          color: #ffffff;
          max-width: 100px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .dropdown-icon {
          font-size: 12px;
          color: #999;
          transition: transform 0.3s;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .front-header {
    .header-container {
      padding: 0 15px;
      height: 60px;
    }

    .header-left {
      .logo-section {
        .logo {
          width: 32px;
          height: 32px;
          margin-right: 8px;
        }

        .system-name {
          font-size: 18px;
        }
      }
    }

    .header-center {
      .nav-menu {
        gap: 20px;

        .nav-item {
          padding: 8px 12px;
          font-size: 14px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .front-header {
    .header-container {
      flex-wrap: wrap;
      height: auto;
      padding: 10px 15px;
    }

    .header-center {
      order: 3;
      width: 100%;
      margin-top: 10px;

      .nav-menu {
        justify-content: center;
        flex-wrap: wrap;
        gap: 15px;
      }
    }
  }
}
</style>