<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, MapLocation, Phone, View, Star, Sunny, Location } from '@element-plus/icons-vue';
import http from '@/utils/http';
import { getImageUrl } from '@/utils/system';
import useUserStore from '@/stores/userStore';
import TicketCard from '@/components/TicketCard.vue';
import AMap from '../../components/AMap.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 景点详情数据
const attraction = ref({});
const loading = ref(false);

// 用户交互状态
const isLiked = ref(false);
const isCollected = ref(false);
const likeLoading = ref(false);
const collectLoading = ref(false);

// 门票相关
const tickets = ref([]);
const ticketLoading = ref(false);

// 路线相关
const showRoute = ref(false);
const userLocation = ref(null);
const routeLoading = ref(false);

// 计算属性（前台会话）
const currentUser = computed(() => userStore.frontUserInfo || {});

const isLoggedIn = computed(() => {
  return !!userStore.frontToken && !!currentUser.value?.id;
});

// 获取景点详情
const getAttractionDetail = async () => {
  const id = route.params.id;
  if (!id) {
    ElMessage.error('景点ID不能为空');
    router.push('/front/attractions');
    return;
  }

  loading.value = true;
  try {
    const res = await http.get('/attraction/getById', { 
      params: { id } 
    });
    if (res.code === 200) {
      attraction.value = res.data;
      
      // 如果用户已登录，检查点赞和收藏状态
      if (isLoggedIn.value) {
        await Promise.all([
          checkLikeStatus(),
          checkCollectStatus()
        ]);
      }
      
      // 获取相关门票
      getRelatedTickets(attraction.value.id);
    } else {
      ElMessage.error(res.message || '获取景点详情失败');
    }
  } catch (error) {
    console.error('获取景点详情失败:', error);
    ElMessage.error('获取景点详情失败');
  } finally {
    loading.value = false;
  }
};

// 检查点赞状态
const checkLikeStatus = async () => {
  if (!isLoggedIn.value) return;
  
  try {
    const res = await http.get('/like/isLiked', {
      params: {
        userId: currentUser.value.id,
        targetType: 1, // 1-景点
        targetId: attraction.value.id
      }
    });
    if (res.code === 200) {
      isLiked.value = res.data;
    }
  } catch (error) {
    console.error('检查点赞状态失败:', error);
  }
};

// 检查收藏状态
const checkCollectStatus = async () => {
  if (!isLoggedIn.value) return;
  
  try {
    const res = await http.get('/collect/isCollected', {
      params: {
        userId: currentUser.value.id,
        targetType: 1, // 1-景点
        targetId: attraction.value.id
      }
    });
    if (res.code === 200) {
      isCollected.value = res.data;
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error);
  }
};

// 点赞/取消点赞
const toggleLike = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  likeLoading.value = true;
  try {
    const endpoint = isLiked.value ? '/like/cancelLike' : '/like/addLike';
    const res = await http.post(endpoint, null, {
      params: {
        userId: currentUser.value.id,
        targetType: 1, // 1-景点
        targetId: attraction.value.id
      }
    });
    
    if (res.code === 200) {
      isLiked.value = !isLiked.value;
      // 更新点赞数
      if (isLiked.value) {
        attraction.value.likeCount = (attraction.value.likeCount || 0) + 1;
        ElMessage.success('点赞成功');
      } else {
        attraction.value.likeCount = Math.max((attraction.value.likeCount || 0) - 1, 0);
        ElMessage.success('取消点赞成功');
      }
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    console.error('点赞操作失败:', error);
    ElMessage.error('操作失败');
  } finally {
    likeLoading.value = false;
  }
};

// 收藏/取消收藏
const toggleCollect = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  collectLoading.value = true;
  try {
    const endpoint = isCollected.value ? '/collect/cancelCollect' : '/collect/addCollect';
    const res = await http.post(endpoint, null, {
      params: {
        userId: currentUser.value.id,
        targetType: 1, // 1-景点
        targetId: attraction.value.id
      }
    });
    
    if (res.code === 200) {
      isCollected.value = !isCollected.value;
      // 更新收藏数
      if (isCollected.value) {
        attraction.value.collectCount = (attraction.value.collectCount || 0) + 1;
        ElMessage.success('收藏成功');
      } else {
        attraction.value.collectCount = Math.max((attraction.value.collectCount || 0) - 1, 0);
        ElMessage.success('取消收藏成功');
      }
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    ElMessage.error('操作失败');
  } finally {
    collectLoading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 格式化评分
const formatRating = (rating) => {
  return rating ? Number(rating).toFixed(1) : '4.5';
};

// 获取相关门票
const getRelatedTickets = async (attractionId) => {
  ticketLoading.value = true;
  try {
    const res = await http.get('/ticket/page', {
      params: {
        attractionId: attractionId,
        pageNum: 1,
        pageSize: 6,
        status: 1 // 只获取上架的门票
      }
    });
    
    if (res.code === 200) {
      tickets.value = res.data.records || [];
    } else {
      console.error('获取门票失败:', res.message);
    }
  } catch (error) {
    console.error('获取门票失败:', error);
  } finally {
    ticketLoading.value = false;
  }
};

// 处理门票预订
const handleTicketBook = (ticket) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  // 跳转到门票详情页
  router.push(`/front/tickets/${ticket.id}`);
};

// 查看更多门票
const viewMoreTickets = () => {
  router.push({
    path: '/front/tickets',
    query: {
      attractionId: attraction.value.id
    }
  });
};

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0';
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w';
  }
  return num.toString();
};

// 查看门票详情
const viewTicketDetail = (ticketId) => {
  router.push(`/front/ticket/${ticketId}`);
};

// 获取用户位置并显示路线
const goToAttraction = () => {
  if (!navigator.geolocation) {
    ElMessage.error('您的浏览器不支持地理位置服务');
    return;
  }

  routeLoading.value = true;
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      userLocation.value = [position.coords.longitude, position.coords.latitude];
      showRoute.value = true;
      routeLoading.value = false;
      ElMessage.success('正在为您规划路线...');
    },
    (error) => {
      routeLoading.value = false;
      let errorMessage = '获取位置失败';
      switch (error.code) {
        case error.PERMISSION_DENIED:
          errorMessage = '用户拒绝了地理位置请求';
          break;
        case error.POSITION_UNAVAILABLE:
          errorMessage = '位置信息不可用';
          break;
        case error.TIMEOUT:
          errorMessage = '获取位置超时';
          break;
      }
      ElMessage.error(errorMessage);
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000
    }
  );
};

// 路线计算完成回调
const onRouteCalculated = (routeInfo) => {
  ElMessage.success(`路线规划完成，距离约${routeInfo.distance}`);
};

onMounted(() => {
  getAttractionDetail();
});
</script>

<template>
  <div class="attraction-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 景点详情内容 -->
    <div v-else-if="attraction.id" class="detail-content">
      <!-- 顶部封面图区域 -->
      <div class="cover-section">
        <div class="cover-image">
          <img :src="getImageUrl(attraction.coverImage)" :alt="attraction.name" />
          <div class="cover-overlay">
            <div class="cover-content">
              <!-- 返回按钮 -->
              <el-button 
                class="back-button" 
                :icon="ArrowLeft" 
                @click="goBack"
                circle
                size="large"
              />
              
              <!-- 景点信息 -->
              <div class="attraction-info">
                <h1 class="attraction-name">{{ attraction.name }}</h1>
                <div class="attraction-meta">
                  <div class="meta-item location">
                    <el-icon><MapLocation /></el-icon>
                    <span>{{ attraction.address }}</span>
                  </div>
                  <div class="meta-item rating">
                    <el-rate 
                      v-model="attraction.rating" 
                      disabled 
                      show-score 
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                    <span class="rating-text">{{ formatRating(attraction.rating) }}分</span>
                  </div>
                </div>
                <p class="attraction-summary">{{ attraction.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 - 左右布局 -->
      <div class="main-content">
        <div class="content-container">
          <!-- 左侧：景点详细内容 -->
          <div class="left-content">
            <div class="content-section">
              <h2 class="section-title">景点介绍</h2>
              <div class="section-content">
                <p class="description-text">{{ attraction.description }}</p>
                
                <!-- 景点特色 -->
                <div class="features-grid">
                  <div class="feature-item">
                    <div class="feature-icon">🏞️</div>
                    <div class="feature-text">
                      <h4>自然风光</h4>
                      <p>欣赏大自然的鬼斧神工</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">📸</div>
                    <div class="feature-text">
                      <h4>拍照圣地</h4>
                      <p>记录美好的旅行时光</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">🎯</div>
                    <div class="feature-text">
                      <h4>热门推荐</h4>
                      <p>游客必去的经典景点</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">⭐</div>
                    <div class="feature-text">
                      <h4>高评分</h4>
                      <p>游客一致好评推荐</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 位置信息 -->
            <div class="content-section">
              <h2 class="section-title">位置信息</h2>
              <div class="section-content">
                <div class="location-info">
                  <div class="location-item">
                    <el-icon class="location-icon"><MapLocation /></el-icon>
                    <div class="location-text">
                      <h4>详细地址</h4>
                      <p>{{ attraction.address }}</p>
                    </div>
                  </div>
                  <div class="location-coordinates" v-if="attraction.latitude && attraction.longitude">
                    <div class="coordinate-item">
                      <span class="coordinate-label">纬度：</span>
                      <span class="coordinate-value">{{ attraction.latitude }}</span>
                    </div>
                    <div class="coordinate-item">
                      <span class="coordinate-label">经度：</span>
                      <span class="coordinate-value">{{ attraction.longitude }}</span>
                    </div>
                    <el-button 
                      type="primary" 
                      :icon="Location" 
                      @click="goToAttraction"
                      :loading="routeLoading"
                      class="go-button"
                    >
                      去这里
                    </el-button>
                  </div>
                  <div class="map-container">
                    <AMap 
                      :locationParams="{
                        lat: attraction.latitude,
                        lon: attraction.longitude,
                        name: attraction.name,
                      }"
                      :showRoute="showRoute"
                      :startLocation="userLocation"
                      @routeCalculated="onRouteCalculated"
                    /> 
                  </div>
                </div>
              </div>
            </div>

            <!-- 相关门票 -->
            <div class="content-section" v-if="tickets.length > 0">
              <h2 class="section-title">
                🎫 相关门票推荐
              </h2>
              <div class="section-content">
                <div v-if="ticketLoading" class="ticket-loading">
                  <el-skeleton :rows="2" animated />
                </div>
                <div v-else>
                  <div class="tickets-grid">
                    <TicketCard 
                      v-for="ticket in tickets.slice(0, 4)" 
                      :key="ticket.id" 
                      :ticket="ticket"
                      @book="handleTicketBook"
                    />
                  </div>
                  <div class="view-more-section" v-if="tickets.length > 4">
                    <el-button 
                      type="primary" 
                      size="large" 
                      @click="viewMoreTickets"
                      class="view-more-btn"
                    >
                      查看更多门票 ({{ tickets.length }}张)
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：基本信息 -->
          <div class="right-content">
            <div class="info-card">
              <h3 class="card-title">基本信息</h3>
              
              <!-- 评分信息 -->
              <div class="info-item rating-info">
                <div class="info-label">综合评分</div>
                <div class="rating-display">
                  <span class="rating-score">{{ formatRating(attraction.rating) }}</span>
                  <el-rate 
                    v-model="attraction.rating" 
                    disabled 
                    size="large"
                  />
                </div>
              </div>

              <!-- 统计信息 -->
              <div class="stats-grid">
                <div class="stat-item">
                  <el-icon class="stat-icon"><View /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(attraction.viewCount) }}</div>
                    <div class="stat-label">浏览量</div>
                  </div>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon" :class="{ 'liked': isLiked }"><Sunny /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(attraction.likeCount) }}</div>
                    <div class="stat-label">点赞数</div>
                  </div>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon" :class="{ 'collected': isCollected }"><Star /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(attraction.collectCount) }}</div>
                    <div class="stat-label">收藏数</div>
                  </div>
                </div>
              </div>

              <!-- 联系信息 -->
              <div class="contact-info" v-if="attraction.contactPhone">
                <div class="info-item">
                  <div class="info-label">联系电话</div>
                  <div class="phone-display">
                    <el-icon><Phone /></el-icon>
                    <span>{{ attraction.contactPhone }}</span>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="action-buttons">
                <el-button 
                  :type="isCollected ? 'warning' : 'primary'" 
                  size="large" 
                  class="action-btn"
                  :loading="collectLoading"
                  @click="toggleCollect"
                >
                  <el-icon><Star /></el-icon>
                  {{ isCollected ? '已收藏' : '收藏' }}
                </el-button>
                <el-button 
                  :type="isLiked ? 'danger' : 'default'" 
                  size="large" 
                  class="action-btn"
                  :loading="likeLoading"
                  @click="toggleLike"
                >
                  <el-icon><Sunny /></el-icon>
                  {{ isLiked ? '已点赞' : '点赞' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="error-content">
        <div class="error-icon">😕</div>
        <h3 class="error-title">景点不存在</h3>
        <p class="error-description">抱歉，您访问的景点信息不存在或已被删除</p>
        <el-button type="primary" @click="router.push('/front/attractions')">
          返回景点列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.attraction-detail-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 加载状态 */
.loading-container {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 顶部封面图区域 */
.cover-section {
  position: relative;
  height: 60vh;
  min-height: 500px;
  overflow: hidden;

  .cover-image {
    position: relative;
    width: 100%;
    height: 100%;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(
        to bottom,
        rgba(0, 0, 0, 0.3) 0%,
        rgba(0, 0, 0, 0.5) 100%
      );
      display: flex;
      align-items: flex-end;
      padding: 40px;

      .cover-content {
        width: 100%;
        max-width: 1200px;
        margin: 0 auto;
        position: relative;

        .back-button {
          position: absolute;
          top: -60px;
          left: 0;
          background: rgba(255, 255, 255, 0.9);
          color: var(--text-primary);
          border: none;
          backdrop-filter: blur(10px);
          
          &:hover {
            background: rgba(255, 255, 255, 1);
            transform: translateY(-2px);
          }
        }

        .attraction-info {
          color: white;

          .attraction-name {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
            line-height: 1.2;
          }

          .attraction-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 30px;
            margin-bottom: 20px;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 1.1rem;

              &.location {
                .el-icon {
                  color: var(--secondary-color);
                }
              }

              &.rating {
                .rating-text {
                  margin-left: 10px;
                  font-weight: 600;
                  color: var(--secondary-color);
                }
              }
            }
          }

          .attraction-summary {
            font-size: 1.2rem;
            line-height: 1.6;
            opacity: 0.9;
            max-width: 800px;
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
          }
        }
      }
    }
  }
}

/* 主要内容区域 */
.main-content {
  padding: 60px 0;

  .content-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 40px;
  }
}

/* 左侧内容 */
.left-content {
  .content-section {
    background: white;
    border-radius: 20px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-light);

    .section-title {
      font-size: 1.5rem;
      font-weight: bold;
      color: var(--text-primary);
      margin-bottom: 20px;
      padding-bottom: 15px;
      border-bottom: 2px solid var(--primary-100);
      position: relative;
      display: flex;
      align-items: center;
      gap: 10px;

      &::after {
        content: '';
        position: absolute;
        bottom: -2px;
        left: 0;
        width: 60px;
        height: 2px;
        background: var(--primary-color);
      }
    }

    .section-content {
      .description-text {
        font-size: 1.1rem;
        line-height: 1.8;
        color: var(--text-secondary);
        margin-bottom: 30px;
      }

      .features-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 20px;

        .feature-item {
          display: flex;
          align-items: center;
          gap: 15px;
          padding: 20px;
          background: var(--bg-tertiary);
          border-radius: 15px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-medium);
          }

          .feature-icon {
            font-size: 2rem;
            width: 60px;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--primary-50);
            border-radius: 50%;
          }

          .feature-text {
            h4 {
              font-size: 1.1rem;
              font-weight: 600;
              color: var(--text-primary);
              margin-bottom: 5px;
            }

            p {
              font-size: 0.9rem;
              color: var(--text-secondary);
              line-height: 1.4;
            }
          }
        }
      }

      .location-info {
        .location-item {
          display: flex;
          align-items: center;
          gap: 15px;
          margin-bottom: 20px;

          .location-icon {
            color: white;
            font-size: 1.5rem;
            padding: 10px;
            margin-top: 2px;
            background-color: var(--primary-color);
            border-radius: 40%;
          }

          .location-text {
            h4 {
              font-size: 1.1rem;
              font-weight: 600;
              color: var(--text-primary);
              margin-bottom: 5px;
            }

            p {
              font-size: 1rem;
              color: var(--text-secondary);
              line-height: 1.5;
            }
          }
        }

        .location-coordinates {
          display: flex;
          align-items: center;
          gap: 30px;
          padding: 15px;
          background: var(--bg-tertiary);
          border-radius: 10px;

          .coordinate-item {
            .coordinate-label {
              font-weight: 600;
              color: var(--text-primary);
            }

            .coordinate-value {
              color: var(--primary-color);
              font-family: monospace;
            }
          }
        }
      }
    }

    /* 评论相关样式 */
    .comment-form {
      margin-bottom: 30px;

      .comment-input-area {
        .comment-actions {
          display: flex;
          justify-content: flex-end;
          margin-top: 15px;
        }
      }
    }

    .login-prompt {
      text-align: center;
      padding: 30px;
      background: var(--bg-tertiary);
      border-radius: 15px;
      margin-bottom: 30px;

      p {
        color: var(--text-secondary);
        font-size: 1rem;
      }
    }

    .comments-list {
      .comment-loading {
        padding: 20px;
      }

      .no-comments {
        text-align: center;
        padding: 40px 20px;
        color: var(--text-secondary);

        .no-comments-icon {
          font-size: 3rem;
          margin-bottom: 15px;
        }

        p {
          font-size: 1.1rem;
        }
      }

      .comment-item {
        display: flex;
        gap: 15px;
        padding: 20px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .comment-avatar {
          flex-shrink: 0;

          img {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid var(--border-light);
          }
        }

        .comment-content {
          flex: 1;

          .comment-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 8px;

            .comment-author {
              font-weight: 600;
              color: var(--text-primary);
              font-size: 0.95rem;
            }

            .comment-time {
              color: var(--text-secondary);
              font-size: 0.85rem;
            }
          }

          .comment-text {
            color: var(--text-secondary);
            line-height: 1.6;
            font-size: 0.95rem;
          }
        }
      }

      .load-more {
        text-align: center;
        padding: 20px 0;
        border-top: 1px solid var(--border-light);
        margin-top: 20px;
      }
    }
  }
}

/* 右侧信息卡片 */
.right-content {
  .info-card {
    background: white;
    border-radius: 20px;
    padding: 30px;
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-light);
    position: sticky;
    top: 20px;

    .card-title {
      font-size: 1.3rem;
      font-weight: bold;
      color: var(--text-primary);
      margin-bottom: 25px;
      text-align: center;
    }

    .info-item {
      margin-bottom: 25px;

      .info-label {
        font-size: 0.9rem;
        color: var(--text-secondary);
        margin-bottom: 8px;
        font-weight: 500;
      }

      &.rating-info {
        text-align: center;
        padding: 20px;
        background: var(--primary-50);
        border-radius: 15px;

        .rating-display {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 10px;

          .rating-score {
            font-size: 2.5rem;
            font-weight: bold;
            color: var(--primary-color);
          }
        }
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 15px;
      margin-bottom: 25px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 15px;
        padding: 15px;
        background: var(--bg-tertiary);
        border-radius: 12px;

        .stat-icon {
          color: var(--primary-color);
          font-size: 1.5rem;
          transition: all 0.3s ease;

          &.liked {
            color: var(--danger-color);
          }

          &.collected {
            color: var(--warning-color);
          }
        }

        .stat-content {
          .stat-number {
            font-size: 1.3rem;
            font-weight: bold;
            color: var(--text-primary);
          }

          .stat-label {
            font-size: 0.9rem;
            color: var(--text-secondary);
          }
        }
      }
    }

    .contact-info {
      .phone-display {
        display: flex;
        align-items: center;
        gap: 8px;
        color: var(--primary-color);
        font-weight: 500;
        font-size: 1.1rem;
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-top: 30px;

      .action-btn {
        width: 100%;
        height: 45px;
        font-size: 1rem;
        font-weight: 600;
        border-radius: 12px;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-medium);
        }
      }
      :deep(.el-button) {
        margin: 0;
      }
    }
  }
}

/* 错误状态 */
.error-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px;

  .error-content {
    text-align: center;
    max-width: 400px;

    .error-icon {
      font-size: 4rem;
      margin-bottom: 20px;
    }

    .error-title {
      font-size: 1.5rem;
      font-weight: bold;
      color: var(--text-primary);
      margin-bottom: 10px;
    }

    .error-description {
      color: var(--text-secondary);
      line-height: 1.6;
      margin-bottom: 30px;
    }
  }
}

/* 门票区域样式 */
.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.ticket-loading {
  padding: 20px 0;
}

.view-more-section {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid var(--border-light);
  margin-top: 20px;

  .view-more-btn {
    background: var(--gradient-primary);
    border: none;
    border-radius: 25px;
    padding: 12px 30px;
    font-weight: 600;
    box-shadow: var(--shadow-medium);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: var(--shadow-dark);
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cover-section {
    height: 50vh;
    min-height: 400px;

    .cover-overlay .cover-content {
      padding: 20px;

      .attraction-info {
        .attraction-name {
          font-size: 2rem;
        }

        .attraction-meta {
          flex-direction: column;
          gap: 15px;
        }

        .attraction-summary {
          font-size: 1rem;
        }
      }
    }
  }

  .main-content .content-container {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .left-content .content-section {
    padding: 20px;

    .section-content .features-grid {
      grid-template-columns: 1fr;
    }

    .comments-list .comment-item {
      .comment-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 5px;
      }
    }
  }

  /* 门票区域移动端样式 */
  .tickets-grid {
    grid-template-columns: 1fr !important;
  }

  .right-content .info-card {
    position: static;
  }
}
</style>