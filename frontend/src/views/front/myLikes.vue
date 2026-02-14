<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Sunny, MapLocation, Document, Calendar, View, Delete } from '@element-plus/icons-vue';
import http from '@/utils/http';
import { getImageUrl } from '@/utils/system';
import useUserStore from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();

// 点赞列表数据
const likes = ref([]);
const loading = ref(false);
const total = ref(0);

// 分页参数
const pageParams = ref({
  pageNum: 1,
  pageSize: 15,
  targetType: null // 筛选类型：1-景点，2-攻略
});

// 筛选选项
const filterOptions = [
  { label: '全部', value: null },
  { label: '景点', value: 1 },
  { label: '攻略', value: 2 }
];

// 当前用户信息（前台会话）
const currentUser = computed(() => userStore.frontUserInfo || {});

const isLoggedIn = computed(() => {
  return !!userStore.frontToken && !!currentUser.value?.id;
});

// 获取点赞列表
const getLikes = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  loading.value = true;
  try {
    const res = await http.get('/like/page', {
      params: {
        ...pageParams.value,
        userId: currentUser.value.id
      }
    });
    
    if (res.code === 200) {
      likes.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取点赞列表失败');
    }
  } catch (error) {
    console.error('获取点赞列表失败:', error);
    ElMessage.error('获取点赞列表失败');
  } finally {
    loading.value = false;
  }
};

// 取消点赞
const cancelLike = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消点赞"${item.targetName}"吗？`,
      '确认取消点赞',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    const res = await http.post('/like/cancelLike', null, {
      params: {
        userId: currentUser.value.id,
        targetType: item.targetType,
        targetId: item.targetId
      }
    });
    
    if (res.code === 200) {
      ElMessage.success('取消点赞成功');
      await getLikes(); // 重新加载列表
    } else {
      ElMessage.error(res.message || '取消点赞失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消点赞失败:', error);
      ElMessage.error('取消点赞失败');
    }
  }
};

// 查看详情
const viewDetail = (item) => {
  if (item.targetType === 1) {
    // 景点详情
    router.push(`/front/attraction/${item.targetId}`);
  } else if (item.targetType === 2) {
    // 攻略详情
    router.push(`/front/strategy/${item.targetId}`);
  }
};

// 筛选类型改变
const handleFilterChange = () => {
  pageParams.value.pageNum = 1;
  getLikes();
};

// 分页改变
const handlePageChange = (page) => {
  pageParams.value.pageNum = page;
  getLikes();
};

// 分页大小改变
const handleSizeChange = (size) => {
  pageParams.value.pageSize = size;
  pageParams.value.pageNum = 1;
  getLikes();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前';
  
  return date.toLocaleDateString();
};

// 获取类型标签
const getTypeTag = (type) => {
  switch (type) {
    case 1:
      return { label: '景点', type: 'success', icon: MapLocation };
    case 2:
      return { label: '攻略', type: 'primary', icon: Document };
    default:
      return { label: '未知', type: 'info', icon: Sunny };
  }
};

onMounted(() => {
  getLikes();
});
</script>

<template>
  <div class="my-likes-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Sunny /></el-icon>
          我的点赞
          <el-icon class="title-icon"><Sunny /></el-icon>
        </h1>
        <p class="page-subtitle">
          <el-icon class="subtitle-icon"><MapLocation /></el-icon>
          管理您点赞的景点和攻略
          <el-icon class="subtitle-icon"><MapLocation /></el-icon>
        </p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-container">
        <div class="filter-box">
          <div class="filter-label">
            <el-icon><Sunny /></el-icon>
            <span>类型筛选</span>
          </div>
          <el-select
            v-model="pageParams.targetType"
            placeholder="选择类型"
            @change="handleFilterChange"
            style="width: 150px"
            size="large"
          >
            <el-option
              v-for="option in filterOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="page-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="likes.length === 0" class="empty-state">
        <div class="empty-icon">👍</div>
        <h3 class="empty-title">暂无点赞</h3>
        <p class="empty-description">
          您还没有点赞任何内容，快去
          <span class="empty-link" @click="router.push('/front/attractions')">浏览景点</span>
          或
          <span class="empty-link" @click="router.push('/front/strategies')">查看攻略</span>
          吧！
        </p>
      </div>

      <!-- 点赞列表 -->
      <div v-else class="likes-container">
        <div class="likes-grid">
          <div
            v-for="item in likes"
            :key="item.id"
            class="like-card"
          >
            <!-- 卡片头部 -->
            <div class="card-header">
              <el-tag
                :type="getTypeTag(item.targetType).type"
                size="small"
                class="type-tag"
              >
                <el-icon class="tag-icon">
                  <component :is="getTypeTag(item.targetType).icon" />
                </el-icon>
                {{ getTypeTag(item.targetType).label }}
              </el-tag>
              
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                circle
                @click="cancelLike(item)"
                class="delete-btn"
                title="取消点赞"
              />
            </div>

            <!-- 卡片内容 -->
            <div class="card-content" @click="viewDetail(item)">
              <h3 class="item-title">{{ item.targetName }}</h3>
              <div class="item-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>点赞于 {{ formatTime(item.createTime) }}</span>
                </div>
              </div>
            </div>

            <!-- 卡片底部 -->
            <div class="card-footer">
              <el-button
                type="primary"
                size="small"
                @click="viewDetail(item)"
                class="view-btn"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pageParams.pageNum"
            v-model:page-size="pageParams.pageSize"
            :page-sizes="[15, 30, 45, 60]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.my-likes-page {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-accent) 100%);

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

  /* 筛选区域 */
  .filter-section {
    padding: 40px 0;
    background: var(--bg-primary);
    box-shadow: var(--shadow-light);

    .filter-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
    }

    .filter-box {
      display: flex;
      gap: 16px;
      align-items: center;
      justify-content: center;

      .filter-label {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        color: var(--text-primary);
        font-size: 1.1rem;
      }
    }
  }

  .page-content {
    .loading-container {
      background: #ffffff;
      border-radius: 12px;
      padding: 24px;
      box-shadow: var(--shadow-light);
    }

    .empty-state {
      background: #ffffff;
      border-radius: 12px;
      padding: 60px 24px;
      text-align: center;
      box-shadow: var(--shadow-light);

      .empty-icon {
        font-size: 64px;
        margin-bottom: 16px;
        opacity: 0.6;
      }

      .empty-title {
        margin: 0 0 12px 0;
        font-size: 20px;
        color: var(--text-primary);
      }

      .empty-description {
        margin: 0;
        color: var(--text-secondary);
        line-height: 1.6;
      }

      .empty-link {
        color: var(--el-color-primary);
        cursor: pointer;
        padding: 0 2px;
        display: inline;
        font-size: inherit;
        font-family: inherit;
        font-weight: inherit;
        background-image: linear-gradient(currentColor, currentColor);
        background-size: 0% 1px;
        background-repeat: no-repeat;
        background-position: 0 100%;
        transition: color 0.35s ease, background-size 0.35s ease;

        &:hover {
          color: var(--el-color-primary-dark-2);
          background-size: 100% 1px;
        }
      }
    }

    .likes-container {
      padding: 40px 20px;

      .likes-grid {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 24px;
        margin-bottom: 32px;

        .like-card {
          background: #ffffff;
          border-radius: 12px;
          padding: 20px;
          box-shadow: var(--shadow-light);
          transition: all 0.3s ease;
          border: 1px solid var(--border-light);

          &:hover {
            box-shadow: var(--shadow-medium);
            transform: translateY(-2px);
          }

          .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;

            .type-tag {
              display: flex;
              align-items: center;
              gap: 4px;

              .tag-icon {
                font-size: 12px;
              }
            }

            .delete-btn {
              opacity: 0.6;
              transition: opacity 0.3s ease;

              &:hover {
                opacity: 1;
              }
            }
          }

          .card-content {
            cursor: pointer;
            margin-bottom: 16px;

            .item-title {
              margin: 0 0 12px 0;
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            .item-meta {
              .meta-item {
                display: flex;
                align-items: center;
                gap: 6px;
                color: var(--text-tertiary);
                font-size: 13px;

                .el-icon {
                  font-size: 14px;
                }
              }
            }
          }

          .card-footer {
            .view-btn {
              width: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              gap: 6px;
            }
          }
        }
      }

      .pagination-container {
        display: flex;
        justify-content: center;
        padding: 24px;
        background: #ffffff;
        border-radius: 12px;
        box-shadow: var(--shadow-light);
      }
    }
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

// 响应式设计
@media (max-width: 768px) {
  .my-likes-page {
    .page-header {
      padding: 60px 0 40px;

      .page-title {
        font-size: 2.5rem;
      }

      .page-subtitle {
        font-size: 1.1rem;
      }
    }

    .filter-section {
      padding: 30px 0;

      .filter-box {
        flex-direction: column;
        gap: 12px;
      }
    }

    .likes-grid {
      grid-template-columns: 1fr !important;
    }
  }
}

@media (max-width: 480px) {
  .my-likes-page {
    .page-header {
      .page-title {
        font-size: 2rem;
      }
    }

    .filter-container,
    .likes-container {
      padding: 0 15px;
    }
  }
}
</style>