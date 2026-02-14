<script setup>
import { getImageUrl } from "@/utils/system";
import { View, User, Calendar } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 定义组件属性
const props = defineProps({
  strategy: {
    type: Object,
    required: true
  }
});

// 获取预算类型颜色
const getBudgetColor = (budgetType) => {
  const colorMap = {
    '经济型': '#67C23A',
    '舒适型': '#E6A23C', 
    '豪华型': '#F56C6C',
    '奢华型': '#909399'
  };
  return colorMap[budgetType] || '#409EFF';
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN');
};

// 查看攻略详情
const viewDetail = () => {
  router.push(`/front/strategy/${props.strategy.id}`);
};
</script>

<template>
  <div class="strategy-card" @click="viewDetail">
    <div class="card-image">
      <img :src="getImageUrl(strategy.coverImage)" :alt="strategy.title" />
      <div class="card-badge" :style="{ backgroundColor: getBudgetColor(strategy.budgetType) }">
        <span class="badge-text">{{ strategy.budgetType }}</span>
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ strategy.title }}</h3>
      <p class="card-attraction">🏞️ {{ strategy.attractionName || '多个景点' }}</p>
      <p class="card-description">{{ strategy.intro || strategy.content }}</p>
      <div class="card-meta">
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ strategy.authorName || '匿名用户' }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Calendar /></el-icon>
          <span>{{ formatDate(strategy.createTime) }}</span>
        </div>
      </div>
      <div class="card-footer">
        <div class="card-views">
          <el-icon><View /></el-icon>
          <span>{{ strategy.viewCount || 0 }} 浏览</span>
        </div>
        <span class="card-budget">{{ strategy.budget || '预算待定' }}</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.strategy-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--shadow-light);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  border: 2px solid transparent;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--gradient-primary);
    border-radius: 20px;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
  }

  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: var(--shadow-dark);
    border-color: var(--primary-200);

    &::before {
      opacity: 0.1;
    }
  }

  .card-image {
    position: relative;
    height: 220px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.4s ease;
    }

    &:hover img {
      transform: scale(1.1);
    }

    .card-badge {
      position: absolute;
      top: 16px;
      left: 16px;
      color: white;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 0.85rem;
      font-weight: 600;
      box-shadow: var(--shadow-medium);

      .badge-text {
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
      }
    }
  }

  .card-content {
    padding: 24px;

    .card-title {
      font-size: 1.3rem;
      font-weight: bold;
      color: var(--text-primary);
      margin-bottom: 12px;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-attraction {
      color: var(--primary-600);
      font-size: 1rem;
      margin-bottom: 12px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .card-description {
      color: var(--text-secondary);
      font-size: 0.95rem;
      line-height: 1.6;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-meta {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: var(--text-secondary);
        font-size: 0.9rem;

        .el-icon {
          font-size: 1rem;
          color: var(--primary-500);
        }
      }
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid var(--border-light);

      .card-views {
        color: var(--text-secondary);
        font-weight: 500;
        font-size: 0.95rem;
        display: flex;
        align-items: center;
        gap: 4px;

        .el-icon {
          color: var(--primary-500);
        }
      }

      .card-budget {
        background: linear-gradient(135deg, var(--secondary-400), var(--secondary-500));
        color: white;
        padding: 6px 12px;
        border-radius: 15px;
        font-size: 0.85rem;
        font-weight: 600;
        box-shadow: var(--shadow-light);
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .strategy-card {
    .card-content {
      padding: 20px;

      .card-title {
        font-size: 1.2rem;
      }

      .card-meta {
        flex-direction: column;
        gap: 8px;
      }

      .card-footer {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
      }
    }
  }
}
</style>