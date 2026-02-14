<script setup>
import { ref, onMounted, computed } from 'vue';
import { Search, Refresh, Filter, Document, Edit, Star, Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import http from '@/utils/http';
import StrategyCard from '@/components/StrategyCard.vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 搜索参数
const searchParams = ref({
  pageNum: 1,
  pageSize: 12,
  title: '',
  budgetType: null
});

// 数据状态
const strategyList = ref([]);
const total = ref(0);
const loading = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(12);

// 预算类型选项
const budgetTypes = ref([
  { value: '1', label: '经济型', color: '#67C23A' },
  { value: '2', label: '舒适型', color: '#E6A23C' },
  { value: '3', label: '豪华型', color: '#F56C6C' }
]);

// 计算属性
const hasStrategies = computed(() => strategyList.value.length > 0);
const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

// 获取攻略列表
const getStrategyList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      title: searchParams.value.title || undefined,
      budgetType: searchParams.value.budgetType || undefined
    };
    
    const res = await http.get('/strategy/page', { params });
    if (res.code === 200) {
      strategyList.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error('获取攻略列表失败');
    }
  } catch (error) {
    console.error('获取攻略列表失败:', error);
    ElMessage.error('获取攻略列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1;
  getStrategyList();
};

// 重置搜索
const handleReset = () => {
  searchParams.value.title = '';
  searchParams.value.budgetType = null;
  currentPage.value = 1;
  getStrategyList();
};

// 预算类型筛选
const handleBudgetFilter = (budgetType) => {
  searchParams.value.budgetType = budgetType;
  currentPage.value = 1;
  getStrategyList();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  getStrategyList();
};

// 新增攻略
const handleAdd = () => {
  router.push("/front/publishStrategy");
};

// 页面初始化
onMounted(async () => {
  await getStrategyList();
});
</script>

<template>
  <div class="strategy-list-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Document /></el-icon>
          精选攻略
          <el-icon class="title-icon"><Edit /></el-icon>
        </h1>
        <p class="page-subtitle">
          <el-icon class="subtitle-icon"><Star /></el-icon>
          分享最实用的旅行攻略
          <el-icon class="subtitle-icon"><Star /></el-icon>
        </p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <div class="search-container">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchParams.title"
            placeholder="搜索攻略标题..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button 
            type="primary" 
            size="large" 
            :icon="Search" 
            @click="handleSearch"
            :loading="loading"
          >
            搜索
          </el-button>
          <el-button 
            size="large" 
            :icon="Refresh" 
            @click="handleReset"
          >
            重置
          </el-button>
          <el-button
          :icon="Plus"
          type="primary"
          @click="handleAdd"
          >发布攻略</el-button
        >
        </div>

        <!-- 预算类型筛选 -->
        <div class="budget-filter">
          <div class="filter-label">
            <el-icon><Filter /></el-icon>
            <span>预算类型</span>
          </div>
          <div class="budget-tags">
            <el-tag
              :class="['budget-tag', { 'active': searchParams.budgetType === null }]"
              @click="handleBudgetFilter(null)"
            >
              全部
            </el-tag>
            <el-tag
              v-for="budget in budgetTypes"
              :key="budget.value"
              :class="['budget-tag', { 'active': searchParams.budgetType === budget.value }]"
              :style="{ '--tag-color': budget.color }"
              @click="handleBudgetFilter(budget.value)"
            >
              {{ budget.label }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 攻略列表区域 -->
    <div class="strategies-section">
      <div class="strategies-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
        </div>

        <!-- 攻略网格 -->
        <div v-else-if="hasStrategies" class="strategies-grid">
          <StrategyCard
            v-for="strategy in strategyList"
            :key="strategy.id"
            :strategy="strategy"
            class="strategy-item"
          />
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-icon">📝</div>
          <h3 class="empty-title">暂无攻略数据</h3>
          <p class="empty-description">
            {{ searchParams.title ? '没有找到相关攻略，请尝试其他关键词' : '暂时没有攻略信息，请稍后再试' }}
          </p>
          <el-button type="primary" @click="handleReset">重新搜索</el-button>
        </div>
      </div>

      <!-- 分页组件 -->
      <div v-if="hasStrategies && totalPages > 1" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, jumper, total"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.strategy-list-page {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-accent) 100%);
}

/* 页面头部 */
.page-header {
  position: relative;
  background: var(--gradient-primary);
  color: white;
  padding: 80px 0 60px;
  text-align: center;
  overflow: hidden;

  .header-content {
    position: relative;
    z-index: 2;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-title {
    font-size: 3.5rem;
    font-weight: 700;
    margin-bottom: 16px;
    text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
    letter-spacing: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;

    .title-icon {
      font-size: 3rem;
      animation: float 3s ease-in-out infinite;
      
      &:first-child {
        animation-delay: 0s;
      }
      
      &:last-child {
        animation-delay: 1.5s;
      }
    }
  }

  .page-subtitle {
    font-size: 1.3rem;
    opacity: 0.9;
    font-weight: 300;
    letter-spacing: 1px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;

    .subtitle-icon {
      font-size: 1.2rem;
      animation: twinkle 2s ease-in-out infinite;
      
      &:first-child {
        animation-delay: 0s;
      }
      
      &:last-child {
        animation-delay: 1s;
      }
    }
  }

  .header-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;

    .decoration-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);

      &:nth-child(1) {
        width: 200px;
        height: 200px;
        top: -100px;
        right: -100px;
        animation: float 6s ease-in-out infinite;
      }

      &:nth-child(2) {
        width: 150px;
        height: 150px;
        bottom: -75px;
        left: -75px;
        animation: float 8s ease-in-out infinite reverse;
      }

      &:nth-child(3) {
        width: 100px;
        height: 100px;
        top: 50%;
        left: 10%;
        animation: float 10s ease-in-out infinite;
      }
    }
  }
}

/* 搜索区域 */
.search-section {
  padding: 40px 0;
  background: var(--bg-primary);
  box-shadow: var(--shadow-light);

  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .search-box {
    display: flex;
    gap: 16px;
    margin-bottom: 30px;
    align-items: center;
    justify-content: center;

    .el-input {
      flex: 1;
      max-width: 400px;
    }
  }

  .budget-filter {
    .filter-label {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      margin-bottom: 16px;
      font-weight: 600;
      color: var(--text-primary);
      font-size: 1.1rem;
    }

    .budget-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      justify-content: center;

      .budget-tag {
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        transition: all 0.3s ease;
        font-weight: 500;
        background: var(--bg-primary);
        color: var(--text-primary);
        border: 1px solid var(--border-light);

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-medium);
          border-color: var(--primary-300);
        }

        &.active {
          background: var(--tag-color, var(--primary-color));
          color: white;
          border-color: var(--tag-color, var(--primary-color));
          box-shadow: var(--shadow-medium);
        }
      }
    }
  }
}

/* 攻略列表区域 */
.strategies-section {
  padding: 60px 0;

  .strategies-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .loading-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
  }

  .strategies-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
    margin-bottom: 40px;

    .strategy-item {
      animation: fadeInUp 0.6s ease-out;
    }
  }

  .empty-state {
    text-align: center;
    padding: 80px 20px;
    background: var(--bg-primary);
    border-radius: 20px;
    box-shadow: var(--shadow-light);

    .empty-icon {
      font-size: 4rem;
      margin-bottom: 20px;
    }

    .empty-title {
      font-size: 1.5rem;
      color: var(--text-primary);
      margin-bottom: 12px;
      font-weight: 600;
    }

    .empty-description {
      color: var(--text-secondary);
      margin-bottom: 30px;
      line-height: 1.6;
    }
  }
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;

  :deep(.el-pagination) {
    --el-pagination-bg-color: var(--bg-primary);
    --el-pagination-text-color: var(--text-primary);
    --el-pagination-button-color: var(--primary-color);
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: var(--shadow-light);
  border: 1px solid var(--border-light);
  transition: all 0.3s ease;
  background-color: var(--bg-primary);
  
  &:hover {
    border-color: var(--primary-300);
  }
  
  &.is-focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px var(--primary-100);
  }
}

:deep(.el-button--primary) {
  background: var(--gradient-primary);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-medium);
  }
}

:deep(.el-button--default) {
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  border-radius: 12px;
  color: var(--text-primary);
  transition: all 0.3s ease;
  
  &:hover {
    border-color: var(--primary-color);
    color: var(--primary-color);
    background: var(--primary-50);
  }
}

/* 动画效果 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.2);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 60px 0 40px;

    .page-title {
      font-size: 2.5rem;
    }

    .page-subtitle {
      font-size: 1.1rem;
    }
  }

  .search-section {
    padding: 30px 0;

    .search-box {
      flex-direction: column;
      align-items: stretch;

      .el-input {
        max-width: none;
      }
    }

    .budget-tags {
      justify-content: center;
    }
  }

  .strategies-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .strategies-section {
    padding: 40px 0;
  }
}

@media (max-width: 480px) {
  .page-header {
    .page-title {
      font-size: 2rem;
    }
  }

  .search-container,
  .strategies-container {
    padding: 0 15px;
  }
}
</style>