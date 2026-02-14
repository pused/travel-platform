<script setup>
import { getImageUrl } from "@/utils/system";
import {useRouter} from "vue-router";

const router = useRouter();

// 定义组件属性
const props = defineProps({
  ticket: {
    type: Object,
    required: true
  }
});

// 查看门票详情
const viewDetail = () => {
  router.push(`/front/ticket/${props.ticket.id}`);
};
</script>

<template>
  <div class="ticket-card" @click="viewDetail">
    <div class="card-image">
      <img :src="getImageUrl(ticket.coverImage)" :alt="ticket.name" />
      <div class="price-badge">
        <span class="price-symbol">¥</span>
        <span class="price">{{ ticket.price }}</span>
      </div>
      <div class="discount-badge">
        <span>💰 特惠</span>
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ ticket.name }}</h3>
      <p class="card-attraction">🏛️ {{ ticket.attractionName }}</p>
      <p class="card-description">{{ ticket.description }}</p>
      <div class="card-footer">
        <span class="card-type">{{ ticket.typeName }}</span>
        <span class="card-stock">📦 库存：{{ ticket.inventory }}</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.ticket-card {
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

    .price-badge {
      position: absolute;
      top: 16px;
      right: 16px;
      background: linear-gradient(135deg, var(--error-color), #dc2626);
      color: white;
      padding: 8px 16px;
      border-radius: 25px;
      font-weight: bold;
      box-shadow: var(--shadow-medium);
      display: flex;
      align-items: center;
      gap: 2px;

      .price-symbol {
        font-size: 1rem;
      }

      .price {
        font-size: 1.2rem;
      }
    }

    .discount-badge {
      position: absolute;
      top: 16px;
      left: 16px;
      background: linear-gradient(135deg, var(--success-color), #059669);
      color: white;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 0.85rem;
      font-weight: 600;
      box-shadow: var(--shadow-medium);

      span {
        display: flex;
        align-items: center;
        gap: 4px;
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
      gap: 12px;

      .card-type {
        background: linear-gradient(135deg, var(--success-100), var(--success-200));
        color: var(--success-700);
        padding: 6px 12px;
        border-radius: 15px;
        font-size: 0.85rem;
        font-weight: 500;
        box-shadow: var(--shadow-light);
      }

      .card-stock {
        color: var(--text-tertiary);
        font-size: 0.9rem;
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>