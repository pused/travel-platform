<script setup>
import http from "@/utils/http";
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getImageUrl } from "@/utils/system";
import AttractionCard from "@/components/AttractionCard.vue";
import StrategyCard from "@/components/StrategyCard.vue";
import TicketCard from "@/components/TicketCard.vue";
import { useRouter } from "vue-router";

const router = useRouter();

// 轮播图数据
const bannerList = ref([]);

// 景点推荐数据
const attractionList = ref([]);

// 攻略推荐数据
const strategyList = ref([]);

// 门票展示数据
const ticketList = ref([]);

// 加载状态
const loading = ref(false);

// 获取轮播图数据
const getBannerList = async () => {
  try {
    const res = await http.get("/banner/list");
    if (res.code === 200) {
      bannerList.value = res.data || [];
    }
  } catch (error) {
    console.error("获取轮播图失败:", error);
  }
};

// 获取景点推荐数据
const getAttractionList = async () => {
  try {
    const res = await http.get("/attraction/page?pageNum=1&pageSize=8");
    if (res.code === 200) {
      attractionList.value = res.data.records || [];
    }
  } catch (error) {
    console.error("获取景点数据失败:", error);
  }
};

// 获取攻略推荐数据
const getStrategyList = async () => {
  try {
    const res = await http.get("/strategy/page?pageNum=1&pageSize=6");
    if (res.code === 200) {
      strategyList.value = res.data.records || [];
    }
  } catch (error) {
    console.error("获取攻略数据失败:", error);
  }
};

// 获取门票展示数据
const getTicketList = async () => {
  try {
    const res = await http.get("/ticket/page?pageNum=1&pageSize=8");
    if (res.code === 200) {
      ticketList.value = res.data.records || [];
    }
  } catch (error) {
    console.error("获取门票数据失败:", error);
  }
};

// 初始化页面数据
const initPageData = async () => {
  loading.value = true;
  try {
    await Promise.all([
      getBannerList(),
      getAttractionList(),
      getStrategyList(),
      getTicketList()
    ]);
  } catch (error) {
    ElMessage.error("页面数据加载失败");
  } finally {
    loading.value = false;
  }
};

// 查看更多功能
const viewMoreAttractions = () => {
  // 这里可以跳转到景点列表页
  router.push("/front/attractions");
};

const viewMoreStrategies = () => {
  console.log("查看更多攻略");
  // 这里可以跳转到攻略列表页
    router.push("/front/strategies");
};

const viewMoreTickets = () => {
  console.log("查看更多门票");
  // 这里可以跳转到门票列表页
    router.push("/front/tickets");
};

onMounted(() => {
  initPageData();
});
</script>

<template>
  <div class="home-container" v-loading="loading">
    <!-- 轮播图区域 -->
    <section class="banner-section">
      <el-carousel 
        v-if="bannerList.length > 0" 
        height="100vh" 
        :interval="4000"
        indicator-position="outside"
      >
        <el-carousel-item v-for="banner in bannerList" :key="banner.id">
          <div class="banner-item">
            <img :src="getImageUrl(banner.url)" :alt="banner.name || banner.title" class="banner-image" />
            <div class="banner-overlay">
              <div class="banner-content">
                <h2 class="banner-title">{{ banner.name || banner.title }}</h2>
                <p class="banner-subtitle">{{ banner.description || '探索美好世界，开启精彩旅程' }}</p>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <div v-else class="banner-placeholder">
        <div class="placeholder-content">
          <div class="placeholder-icon">🌍</div>
          <h2>欢迎来到旅游攻略平台</h2>
          <p>发现美好世界，开启精彩旅程</p>
        </div>
      </div>
    </section>

    <!-- 景点推荐区域 -->
    <section class="attraction-section">
      <div class="section-header">
        <div class="section-title-wrapper">
          <div class="title-left">
            <div class="title-icon">🏔️</div>
            <h2 class="section-title">热门景点</h2>
          </div>
          <div class="title-right">
            <el-button 
              type="primary" 
              link 
              @click="viewMoreAttractions"
              class="more-btn"
            >
              查看更多 →
            </el-button>
          </div>
        </div>
        <p class="section-subtitle">探索最受欢迎的旅游目的地</p>
      </div>
      <div class="attraction-grid">
        <AttractionCard 
          v-for="attraction in attractionList" 
          :key="attraction.id"
          :attraction="attraction"
        />
      </div>
    </section>

    <!-- 攻略推荐区域 -->
    <section class="strategy-section">
      <div class="section-header">
        <div class="section-title-wrapper">
          <div class="title-left">
            <div class="title-icon">📖</div>
            <h2 class="section-title">精选攻略</h2>
          </div>
          <div class="title-right">
            <el-button 
              type="primary" 
              link 
              @click="viewMoreStrategies"
              class="more-btn"
            >
              查看更多 →
            </el-button>
          </div>
        </div>
        <p class="section-subtitle">资深旅行者的经验分享</p>
      </div>
      <div class="strategy-grid">
        <StrategyCard 
          v-for="strategy in strategyList" 
          :key="strategy.id"
          :strategy="strategy"
        />
      </div>
    </section>

    <!-- 门票展示区域 -->
    <section class="ticket-section">
      <div class="section-header">
        <div class="section-title-wrapper">
          <div class="title-left">
            <div class="title-icon">🎫</div>
            <h2 class="section-title">精选门票</h2>
          </div>
          <div class="title-right">
            <el-button 
              type="primary" 
              link 
              @click="viewMoreTickets"
              class="more-btn"
            >
              查看更多 →
            </el-button>
          </div>
        </div>
        <p class="section-subtitle">优惠门票，畅游无忧</p>
      </div>
      <div class="ticket-grid">
        <TicketCard 
          v-for="ticket in ticketList" 
          :key="ticket.id"
          :ticket="ticket"
        />
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 50%, var(--bg-tertiary) 100%);
}

// 轮播图样式
.banner-section {
  margin: 0;
  position: relative;

  .banner-item {
    position: relative;
    height: 100vh;
    overflow: hidden;

    .banner-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    .banner-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(
        transparent 0%,
        rgba(0, 0, 0, 0.3) 30%,
        rgba(0, 0, 0, 0.7) 70%,
        rgba(0, 0, 0, 0.9) 100%
      );
      padding: 60px 40px 40px;
      color: white;

      .banner-content {
        max-width: 1200px;
        margin: 0 auto;

        .banner-title {
          font-size: 3.5rem;
          font-weight: bold;
          margin: 0 0 16px 0;
          text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
          background: linear-gradient(135deg, #ffffff, #d1fae5);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .banner-subtitle {
          font-size: 1.4rem;
          margin: 0;
          opacity: 0.95;
          text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.5);
        }
      }
    }

    &:hover .banner-image {
      transform: scale(1.02);
    }
  }

  .banner-placeholder {
    height: 100vh;
    background: var(--gradient-ocean);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.15"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
      opacity: 0.3;
    }

    .placeholder-content {
      text-align: center;
      position: relative;
      z-index: 1;

      .placeholder-icon {
        font-size: 4rem;
        margin-bottom: 20px;
        animation: float 3s ease-in-out infinite;
      }

      h2 {
        font-size: 3.5rem;
        margin-bottom: 1rem;
        font-weight: bold;
        text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
      }

      p {
        font-size: 1.4rem;
        opacity: 0.95;
        text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

// 通用区域样式
.attraction-section,
.strategy-section,
.ticket-section {
  padding: 80px 40px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: -40px;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 4px;
    background: var(--gradient-primary);
    border-radius: 2px;
  }
}

.section-header {
  margin-bottom: 50px;

  .section-title-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .title-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .title-icon {
        font-size: 2.5rem;
        background: var(--gradient-primary);
        border-radius: 12px;
        padding: 12px;
        box-shadow: var(--shadow-medium);
        animation: pulse 2s ease-in-out infinite;
      }

      .section-title {
        font-size: 2.8rem;
        color: var(--text-primary);
        margin: 0;
        font-weight: bold;
        background: linear-gradient(135deg, var(--primary-600), var(--accent-600));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .title-right {
      .more-btn {
        font-size: 1.1rem;
        font-weight: 600;
        padding: 12px 24px;
        border-radius: 25px;
        background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
        color: white;
        border: none;
        box-shadow: var(--shadow-light);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-medium);
          background: linear-gradient(135deg, var(--primary-600), var(--primary-700));
        }
      }
    }
  }

  .section-subtitle {
    font-size: 1.2rem;
    color: var(--text-secondary);
    margin: 0;
    text-align: center;
    font-style: italic;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

// 网格布局
.attraction-grid,
.ticket-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.strategy-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 30px;
}

// 响应式设计
@media (max-width: 768px) {
  .attraction-section,
  .strategy-section,
  .ticket-section {
    padding: 0 20px 60px;
  }

  .section-header {
    .section-title-wrapper {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;

      .title-left {
        .title-icon {
          font-size: 2rem;
          padding: 8px;
        }

        .section-title {
          font-size: 2.2rem;
        }
      }

      .title-right {
        align-self: stretch;

        .more-btn {
          width: 100%;
          justify-content: center;
        }
      }
    }

    .section-subtitle {
      text-align: left;
    }
  }

  .attraction-grid,
  .strategy-grid,
  .ticket-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .banner-section {
    margin: 0;

    .banner-item {
      .banner-overlay {
        padding: 40px 20px 30px;

        .banner-content {
          .banner-title {
            font-size: 2.5rem;
          }

          .banner-subtitle {
            font-size: 1.1rem;
          }
        }
      }
    }

    .banner-placeholder {
      .placeholder-content {
        .placeholder-icon {
          font-size: 3rem;
        }

        h2 {
          font-size: 2.5rem;
        }

        p {
          font-size: 1.1rem;
        }
      }
    }
  }
}

// Element Plus 轮播图样式覆盖
:deep(.el-carousel__indicator) {
  .el-carousel__button {
    background-color: rgba(255, 255, 255, 0.4);
    border-radius: 8px;
    width: 12px;
    height: 4px;
  }

  &.is-active .el-carousel__button {
    background: var(--gradient-primary);
    width: 24px;
  }
}

:deep(.el-carousel__arrow) {
  background-color: rgba(255, 255, 255, 0.8);
  color: var(--primary-600);
  border-radius: 50%;
  width: 48px;
  height: 48px;

  &:hover {
    background-color: white;
    color: var(--primary-700);
  }
}
</style>