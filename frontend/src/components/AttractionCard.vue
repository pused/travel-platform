<script setup>
import { useRouter } from 'vue-router';
import { getImageUrl } from "@/utils/system";

const router = useRouter();

// 定义组件属性
const props = defineProps({
  attraction: {
    type: Object,
    required: true
  }
});

// 查看景点详情
const viewDetail = () => {
  router.push(`/front/attraction/${props.attraction.id}`);
};
</script>

<template>
  <div class="attraction-card" @click="viewDetail">
    <div class="card-image">
      <img :src="getImageUrl(attraction.coverImage)" :alt="attraction.name" />
      <div class="card-badge">
        <span class="badge-text">{{ attraction.categoryName }}</span>
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ attraction.name }}</h3>
      <p class="card-location">📍 {{ attraction.address }}</p>
      <p class="card-description">{{ attraction.description }}</p>
      <div class="card-footer">
        <span class="card-rating">⭐ {{ attraction.rating || '4.5' }}</span>
        <span class="card-price">热门推荐</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.attraction-card {
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
      background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
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

    .card-location {
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
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid var(--border-light);

      .card-rating {
        color: var(--secondary-600);
        font-weight: 600;
        font-size: 1rem;
        display: flex;
        align-items: center;
        gap: 4px;
      }

      .card-price {
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
</style>