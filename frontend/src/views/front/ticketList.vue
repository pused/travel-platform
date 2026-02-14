<script setup>
import { ref, onMounted, computed } from 'vue';
import { Search, Refresh, Filter, Ticket, Star, MapLocation } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import http from '@/utils/http';
import TicketCard from '@/components/TicketCard.vue';

// 搜索参数
const searchParams = ref({
  pageNum: 1,
  pageSize: 12,
  name: '',
  attractionId: null,
  type: null
});

// 数据状态
const ticketList = ref([]);
const attractionList = ref([]);
const total = ref(0);
const loading = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(12);

// 门票类型选项
const ticketTypes = ref([
  { value: 1, label: '成人票', color: '#409EFF' },
  { value: 2, label: '儿童票', color: '#67C23A' },
  { value: 3, label: '学生票', color: '#E6A23C' },
  { value: 4, label: '老人票', color: '#F56C6C' },
  { value: 5, label: '团体票', color: '#909399' }
]);

// 计算属性
const hasTickets = computed(() => ticketList.value.length > 0);
const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

// 获取景点列表
const getAttractionList = async () => {
  try {
    const res = await http.get('/attraction/list');
    if (res.code === 200) {
      attractionList.value = res.data;
    }
  } catch (error) {
    console.error('获取景点列表失败:', error);
  }
};

// 获取门票列表
const getTicketList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      name: searchParams.value.name || undefined,
      attractionId: searchParams.value.attractionId || undefined,
      type: searchParams.value.type || undefined
    };
    
    const res = await http.get('/ticket/page', { params });
    if (res.code === 200) {
      ticketList.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error('获取门票列表失败');
    }
  } catch (error) {
    console.error('获取门票列表失败:', error);
    ElMessage.error('获取门票列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1;
  getTicketList();
};

// 重置搜索
const handleReset = () => {
  searchParams.value.name = '';
  searchParams.value.attractionId = null;
  searchParams.value.type = null;
  currentPage.value = 1;
  getTicketList();
};

// 景点筛选
const handleAttractionFilter = () => {
  currentPage.value = 1;
  getTicketList();
};

// 门票类型筛选
const handleTypeFilter = (type) => {
  searchParams.value.type = type;
  currentPage.value = 1;
  getTicketList();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  getTicketList();
};

// 获取门票类型颜色
const getTypeColor = (type) => {
  const typeItem = ticketTypes.value.find(item => item.value === type);
  return typeItem ? typeItem.color : '#409EFF';
};

// 获取门票类型名称
const getTypeName = (type) => {
  const typeItem = ticketTypes.value.find(item => item.value === type);
  return typeItem ? typeItem.label : '未知类型';
};

// 页面初始化
onMounted(async () => {
  await getAttractionList();
  await getTicketList();
});
</script>

<template>
  <div class="ticket-list-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Ticket /></el-icon>
          精选门票
          <el-icon class="title-icon"><Star /></el-icon>
        </h1>
        <p class="page-subtitle">
          <el-icon class="subtitle-icon"><MapLocation /></el-icon>
          优惠门票，畅游景点
          <el-icon class="subtitle-icon"><MapLocation /></el-icon>
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
            v-model="searchParams.name"
            placeholder="搜索门票名称..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-model="searchParams.attractionId"
            placeholder="选择景点"
            size="large"
            clearable
            @change="handleAttractionFilter"
            style="width: 200px;"
          >
            <el-option
              v-for="attraction in attractionList"
              :key="attraction.id"
              :label="attraction.name"
              :value="attraction.id"
            />
          </el-select>
          
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
        </div>

        <!-- 门票类型筛选 -->
        <div class="filter-section">
          <div class="filter-label">
            <el-icon><Ticket /></el-icon>
            <span>门票类型</span>
          </div>
          <div class="filter-tags">
            <el-tag
              :class="['filter-tag', { 'active': searchParams.type === null }]"
              @click="handleTypeFilter(null)"
            >
              全部类型
            </el-tag>
            <el-tag
              v-for="typeItem in ticketTypes"
              :key="typeItem.value"
              :class="['filter-tag', { 'active': searchParams.type === typeItem.value }]"
              :style="{ '--tag-color': typeItem.color }"
              @click="handleTypeFilter(typeItem.value)"
            >
              {{ typeItem.label }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 门票列表区域 -->
    <div class="tickets-section">
      <div class="tickets-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
        </div>

        <!-- 门票网格 -->
        <div v-else-if="hasTickets" class="tickets-grid">
          <TicketCard
            v-for="ticket in ticketList"
            :key="ticket.id"
            :ticket="ticket"
            class="ticket-item"
          />
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-icon">🎫</div>
          <h3 class="empty-title">暂无门票数据</h3>
          <p class="empty-description">
            {{ searchParams.name ? '没有找到相关门票，请尝试其他关键词' : '暂时没有门票信息，请稍后再试' }}
          </p>
          <el-button type="primary" @click="handleReset">重新搜索</el-button>
        </div>
      </div>

      <!-- 分页组件 -->
      <div v-if="hasTickets && totalPages > 1" class="pagination-container">
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
.ticket-list-page {
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
    flex-wrap: wrap;

    .el-input {
      flex: 1;
      max-width: 400px;
      min-width: 250px;
    }
  }

  .filter-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

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

    .filter-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      justify-content: center;

      .filter-tag {
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

/* 门票列表区域 */
.tickets-section {
  padding: 60px 0;

  .tickets-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .loading-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
  }

  .tickets-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
    animation: fadeInUp 0.6s ease-out;

    .ticket-item {
      animation: fadeInUp 0.6s ease-out;
      animation-fill-mode: both;

      &:nth-child(1) { animation-delay: 0.1s; }
      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.3s; }
      &:nth-child(4) { animation-delay: 0.4s; }
      &:nth-child(5) { animation-delay: 0.5s; }
      &:nth-child(6) { animation-delay: 0.6s; }
    }
  }

  .empty-state {
    text-align: center;
    padding: 80px 20px;
    color: var(--text-secondary);

    .empty-icon {
      font-size: 4rem;
      margin-bottom: 24px;
      opacity: 0.6;
    }

    .empty-title {
      font-size: 1.5rem;
      font-weight: 600;
      margin-bottom: 12px;
      color: var(--text-primary);
    }

    .empty-description {
      font-size: 1rem;
      margin-bottom: 32px;
      line-height: 1.6;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 60px;
  }
}

/* 按钮样式优化 */
:deep(.el-button--primary) {
  background: var(--primary-color);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
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
      gap: 12px;

      .el-input {
        max-width: none;
        min-width: auto;
      }

      .el-select {
        width: 100% !important;
      }
    }

    .filter-tags {
      justify-content: center;
    }
  }

  .tickets-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .tickets-section {
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
  .tickets-container {
    padding: 0 15px;
  }
}
</style>