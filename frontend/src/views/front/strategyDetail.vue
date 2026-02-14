<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, MapLocation, User, Calendar, View, Star, Sunny, ChatDotRound, Money, Clock } from '@element-plus/icons-vue';
import http from '@/utils/http';
import { getImageUrl } from '@/utils/system';
import useUserStore from '@/stores/userStore';
import MarkdownIt from "markdown-it";
import TicketCard from '@/components/TicketCard.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 攻略详情数据
const strategy = ref({});
const loading = ref(false);
const markdown = new MarkdownIt();
const markdownText = ref(); // 传入文本

// 用户交互状态
const isLiked = ref(false);
const isCollected = ref(false);
const likeLoading = ref(false);
const collectLoading = ref(false);

// 评论相关
const comments = ref([]);
const commentLoading = ref(false);
const commentForm = ref({
  content: ''
});
const commentParams = ref({
  pageNum: 1,
  pageSize: 10,
  strategyId: null
});
const commentTotal = ref(0);

// 门票相关
const tickets = ref([]);
const ticketLoading = ref(false);

// 计算属性（前台会话）
const currentUser = computed(() => userStore.frontUserInfo || {});

const isLoggedIn = computed(() => {
  return !!userStore.frontToken && !!currentUser.value?.id;
});

// 预算类型映射
const budgetTypeMap = {
  1: { label: '经济', color: '#67C23A', icon: '💰' },
  2: { label: '舒适', color: '#E6A23C', icon: '💎' },
  3: { label: '豪华', color: '#F56C6C', icon: '👑' }
};

// 获取攻略详情
const getStrategyDetail = async () => {
  const id = route.params.id;
  if (!id) {
    ElMessage.error('攻略ID不能为空');
    router.push('/front/strategies');
    return;
  }

  loading.value = true;
  try {
    const res = await http.get('/strategy/getById', { 
      params: { id } 
    });
    if (res.code === 200) {
      strategy.value = res.data;
      commentParams.value.strategyId = id;
      markdownText.value = markdown.render(res.data.content || "");
      
      // 如果用户已登录，检查点赞和收藏状态
      if (isLoggedIn.value) {
        await Promise.all([
          checkLikeStatus(),
          checkCollectStatus()
        ]);
      }
      
      // 增加浏览量
      incrementViewCount();
      
      // 获取相关门票
      if (res.data.attractionId) {
        getRelatedTickets(res.data.attractionId);
      }
    } else {
      ElMessage.error(res.message || '获取攻略详情失败');
    }
  } catch (error) {
    console.error('获取攻略详情失败:', error);
    ElMessage.error('获取攻略详情失败');
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
        targetType: 2, // 2-攻略
        targetId: strategy.value.id
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
        targetType: 2, // 2-攻略
        targetId: strategy.value.id
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
        targetType: 2, // 2-攻略
        targetId: strategy.value.id
      }
    });
    
    if (res.code === 200) {
      isLiked.value = !isLiked.value;
      // 更新点赞数
      if (isLiked.value) {
        strategy.value.likeCount = (strategy.value.likeCount || 0) + 1;
        ElMessage.success('点赞成功');
      } else {
        strategy.value.likeCount = Math.max((strategy.value.likeCount || 0) - 1, 0);
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
        targetType: 2, // 2-攻略
        targetId: strategy.value.id
      }
    });
    
    if (res.code === 200) {
      isCollected.value = !isCollected.value;
      // 更新收藏数
      if (isCollected.value) {
        strategy.value.collectCount = (strategy.value.collectCount || 0) + 1;
        ElMessage.success('收藏成功');
      } else {
        strategy.value.collectCount = Math.max((strategy.value.collectCount || 0) - 1, 0);
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

// 增加浏览量
const incrementViewCount = async () => {
  try {
    // 这里可以调用增加浏览量的接口
    // await http.post('/strategy/incrementView', { id: strategy.value.id });
  } catch (error) {
    console.error('增加浏览量失败:', error);
  }
};

// 获取评论列表
const getComments = async () => {
  if (!commentParams.value.strategyId) return;
  
  commentLoading.value = true;
  try {
    const res = await http.get('/comment/page', {
      params: {
        ...commentParams.value,
        strategyId: commentParams.value.strategyId
      }
    });
    
    if (res.code === 200) {
      if (commentParams.value.pageNum === 1) {
        comments.value = res.data.records || [];
      } else {
        comments.value.push(...(res.data.records || []));
      }
      commentTotal.value = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取评论失败');
    }
  } catch (error) {
    console.error('获取评论失败:', error);
    ElMessage.error('获取评论失败');
  } finally {
    commentLoading.value = false;
  }
};

// 提交评论
const submitComment = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }
  
  try {
    const res = await http.post('/comment/add', {
      content: commentForm.value.content,
      strategyId: strategy.value.id,
      userId: currentUser.value.id
    });
    
    if (res.code === 200) {
      ElMessage.success('评论发表成功');
      commentForm.value.content = '';
      // 重新加载评论
      commentParams.value.pageNum = 1;
      await getComments();
      // 更新评论数
      strategy.value.commentCount = (strategy.value.commentCount || 0) + 1;
    } else {
      ElMessage.error(res.message || '评论发表失败');
    }
  } catch (error) {
    console.error('评论发表失败:', error);
    ElMessage.error('评论发表失败');
  }
};

// 加载更多评论
const loadMoreComments = async () => {
  if (commentLoading.value) return;
  commentParams.value.pageNum++;
  await getComments();
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0';
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w';
  }
  return num.toString();
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

// 格式化预算
const formatBudget = (budget) => {
  if (!budget) return '预算待定';
  return `¥${budget}`;
};

// 获取预算类型信息
const getBudgetTypeInfo = (type) => {
  return budgetTypeMap[type] || { label: '未知', color: '#909399', icon: '💰' };
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
      attractionId: strategy.value.attractionId
    }
  });
};

onMounted(async () => {
  await Promise.all([await getStrategyDetail(),
  await getComments()])
});
</script>

<template>
  <div class="strategy-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 攻略详情内容 -->
    <div v-else-if="strategy.id" class="detail-content">
      <!-- 顶部封面图区域 -->
      <div class="cover-section">
        <div class="cover-image">
          <img :src="getImageUrl(strategy.coverImage)" :alt="strategy.title" />
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
              
              <!-- 攻略信息 -->
              <div class="strategy-info">
                <h1 class="strategy-title">{{ strategy.title }}</h1>
                <div class="strategy-meta">
                  <div class="meta-item author" v-if="strategy.authorName">
                    <el-icon><User /></el-icon>
                    <span>{{ strategy.authorName }}</span>
                  </div>
                  <div class="meta-item attraction" v-if="strategy.attractionName">
                    <el-icon><MapLocation /></el-icon>
                    <span>{{ strategy.attractionName }}</span>
                  </div>
                  <div class="meta-item date">
                    <el-icon><Calendar /></el-icon>
                    <span>{{ formatTime(strategy.createTime) }}</span>
                  </div>
                </div>
                <p class="strategy-summary">{{ strategy.intro }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 - 左右布局 -->
      <div class="main-content">
        <div class="content-container">
          <!-- 左侧：攻略详细内容 -->
          <div class="left-content">
            <div class="content-section">
              <h2 class="section-title">攻略详情</h2>
              <div class="section-content">
                <div class="strategy-content" v-html="markdownText"></div>
              </div>
            </div>

            <!-- 行程信息 -->
            <div class="content-section">
              <h2 class="section-title">行程信息</h2>
              <div class="section-content">
                <div class="trip-info-grid">
                  <div class="trip-info-item">
                    <div class="info-icon">📅</div>
                    <div class="info-text">
                      <h4>行程天数</h4>
                      <p>{{ strategy.days }}天</p>
                    </div>
                  </div>
                  <div class="trip-info-item">
                    <div class="info-icon">{{ getBudgetTypeInfo(strategy.budgetType).icon }}</div>
                    <div class="info-text">
                      <h4>预算类型</h4>
                      <p :style="{ color: getBudgetTypeInfo(strategy.budgetType).color }">
                        {{ getBudgetTypeInfo(strategy.budgetType).label }}
                      </p>
                    </div>
                  </div>
                  <div class="trip-info-item" v-if="strategy.estimatedBudget">
                    <div class="info-icon">💰</div>
                    <div class="info-text">
                      <h4>预估预算</h4>
                      <p>{{ formatBudget(strategy.estimatedBudget) }}</p>
                    </div>
                  </div>
                  <div class="trip-info-item" v-if="strategy.season">
                    <div class="info-icon">🌸</div>
                    <div class="info-text">
                      <h4>适合季节</h4>
                      <p>{{ strategy.season }}</p>
                    </div>
                  </div>
                  <div class="trip-info-item">
                    <div class="info-icon">{{ getBudgetTypeInfo(strategy.budgetType).icon }}</div>
                    <div class="info-text">
                      <h4>预算类型</h4>
                      <p :style="{ color: getBudgetTypeInfo(strategy.budgetType).color }">
                        {{ getBudgetTypeInfo(strategy.budgetType).label }}
                      </p>
                    </div>
                  </div>
                  <div class="trip-info-item" v-if="strategy.estimatedBudget">
                    <div class="info-icon">💰</div>
                    <div class="info-text">
                      <h4>预估预算</h4>
                      <p>{{ formatBudget(strategy.estimatedBudget) }}</p>
                    </div>
                  </div>
                  <div class="trip-info-item" v-if="strategy.season">
                    <div class="info-icon">🌸</div>
                    <div class="info-text">
                      <h4>适合季节</h4>
                      <p>{{ strategy.season }}</p>
                    </div>
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

            <!-- 评论区域 -->
            <div class="content-section">
              <h2 class="section-title">
                <el-icon><ChatDotRound /></el-icon>
                评论区 ({{ formatNumber(strategy.commentCount) }})
              </h2>
              <div class="section-content">
                <!-- 评论表单 -->
                <div class="comment-form" v-if="isLoggedIn">
                  <div class="comment-input-area">
                    <el-input
                      v-model="commentForm.content"
                      type="textarea"
                      :rows="4"
                      placeholder="写下你的评论..."
                      maxlength="500"
                      show-word-limit
                    />
                    <div class="comment-actions">
                      <el-button type="primary" @click="submitComment">
                        发表评论
                      </el-button>
                    </div>
                  </div>
                </div>

                <!-- 未登录提示 -->
                <div class="login-prompt" v-else>
                  <p>请先登录后再发表评论</p>
                  <el-button type="primary" @click="router.push('/login')">
                    立即登录
                  </el-button>
                </div>

                <!-- 评论列表 -->
                <div class="comments-list">
                  <div v-if="commentLoading && commentParams.pageNum === 1" class="comment-loading">
                    <el-skeleton :rows="3" animated />
                  </div>
                  
                  <div v-else-if="comments.length === 0" class="no-comments">
                    <div class="no-comments-icon">💬</div>
                    <p>暂无评论，快来发表第一条评论吧！</p>
                  </div>
                  
                  <div v-else>
                    <div v-for="comment in comments" :key="comment.id" class="comment-item">
                      <div class="comment-avatar">
                        <img :src="getImageUrl(comment.userAvatar)" :alt="comment.userName" />
                      </div>
                      <div class="comment-content">
                        <div class="comment-header">
                          <span class="comment-author">{{ comment.userName }}</span>
                          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                        </div>
                        <div class="comment-text">{{ comment.content }}</div>
                      </div>
                    </div>
                    
                    <!-- 加载更多 -->
                    <div v-if="comments.length < commentTotal" class="load-more">
                      <el-button 
                        @click="loadMoreComments" 
                        :loading="commentLoading"
                        type="primary" 
                        plain
                      >
                        加载更多评论
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：基本信息 -->
          <div class="right-content">
            <div class="info-card">
              <h3 class="card-title">攻略信息</h3>
              
              <!-- 推荐标识 -->
              <div class="recommend-badge" v-if="strategy.isRecommend === 1">
                <el-icon><Star /></el-icon>
                <span>推荐攻略</span>
              </div>

              <!-- 统计信息 -->
              <div class="stats-grid">
                <div class="stat-item">
                  <el-icon class="stat-icon"><View /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(strategy.viewCount) }}</div>
                    <div class="stat-label">浏览量</div>
                  </div>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon" :class="{ 'liked': isLiked }"><Sunny /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(strategy.likeCount) }}</div>
                    <div class="stat-label">点赞数</div>
                  </div>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon" :class="{ 'collected': isCollected }"><Star /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(strategy.collectCount) }}</div>
                    <div class="stat-label">收藏数</div>
                  </div>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon"><ChatDotRound /></el-icon>
                  <div class="stat-content">
                    <div class="stat-number">{{ formatNumber(strategy.commentCount) }}</div>
                    <div class="stat-label">评论数</div>
                  </div>
                </div>
              </div>

              <!-- 攻略详细信息 -->
              <div class="strategy-details">
                <div class="detail-item" v-if="strategy.days">
                  <div class="detail-label">
                    <el-icon><Clock /></el-icon>
                    行程天数
                  </div>
                  <div class="detail-value">{{ strategy.days }}天</div>
                </div>
                
                <div class="detail-item" v-if="strategy.budgetType">
                  <div class="detail-label">
                    <el-icon><Money /></el-icon>
                    预算类型
                  </div>
                  <div class="detail-value" :style="{ color: getBudgetTypeInfo(strategy.budgetType).color }">
                    {{ getBudgetTypeInfo(strategy.budgetType).label }}
                  </div>
                </div>
                
                <div class="detail-item" v-if="strategy.estimatedBudget">
                  <div class="detail-label">预估预算</div>
                  <div class="detail-value budget-amount">{{ formatBudget(strategy.estimatedBudget) }}</div>
                </div>
                
                <div class="detail-item" v-if="strategy.season">
                  <div class="detail-label">适合季节</div>
                  <div class="detail-value">{{ strategy.season }}</div>
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
                  {{ isCollected ? '已收藏' : '收藏攻略' }}
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
        <h3 class="error-title">攻略不存在</h3>
        <p class="error-description">抱歉，您访问的攻略信息不存在或已被删除</p>
        <el-button type="primary" @click="router.push('/front/strategies')">
          返回攻略列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.strategy-detail-page {
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

        .strategy-info {
          color: white;
          
          .strategy-title {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
            line-height: 1.2;
          }

          .strategy-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 24px;
            margin-bottom: 20px;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 1.1rem;
              
              .el-icon {
                font-size: 1.2rem;
              }
            }
          }

          .strategy-summary {
            font-size: 1.2rem;
            line-height: 1.6;
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
    grid-template-columns: 1fr 350px;
    gap: 40px;
  }
}

/* 左侧内容 */
.left-content {
  .content-section {
    background: var(--bg-primary);
    border-radius: 16px;
    padding: 32px;
    margin-bottom: 32px;
    box-shadow: var(--shadow-light);

    .section-title {
      font-size: 1.5rem;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      gap: 8px;
      
      .el-icon {
        color: var(--primary-color);
      }
    }

    .section-content {
      .strategy-content {
        color: var(--text-primary);
        line-height: 1.8;
        font-size: 1rem;
        
        :deep(p) {
          margin-bottom: 16px;
        }
        
        :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
          color: var(--text-primary);
          margin: 24px 0 16px 0;
          font-weight: 600;
        }
        
        :deep(ul), :deep(ol) {
          margin: 16px 0;
          padding-left: 24px;
        }
        
        :deep(li) {
          margin-bottom: 8px;
        }
      }

      .trip-info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 24px;

        .trip-info-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 20px;
          background: var(--bg-secondary);
          border-radius: 12px;
          border: 1px solid var(--border-light);

          .info-icon {
            font-size: 2rem;
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--primary-100);
            border-radius: 50%;
          }

          .info-text {
            h4 {
              font-size: 0.9rem;
              color: var(--text-secondary);
              margin-bottom: 4px;
              font-weight: 500;
            }

            p {
              font-size: 1.1rem;
              color: var(--text-primary);
              font-weight: 600;
              margin: 0;
            }
          }
        }
      }
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

/* 评论区域样式 */
.comment-form {
  margin-bottom: 32px;

  .comment-input-area {
    .comment-actions {
      margin-top: 12px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

.login-prompt {
  text-align: center;
  padding: 24px;
  background: var(--bg-secondary);
  border-radius: 12px;
  margin-bottom: 32px;
  
  p {
    color: var(--text-secondary);
    margin: 0;
  }
}

.comments-list {
  .comment-loading {
    padding: 20px 0;
  }

  .no-comments {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);

    .no-comments-icon {
      font-size: 3rem;
      margin-bottom: 16px;
    }

    p {
      margin: 0;
    }
  }

  .comment-item {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid var(--border-light);

    &:last-child {
      border-bottom: none;
    }

    .comment-avatar {
      flex-shrink: 0;

      img {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
      }
    }

    .comment-content {
      flex: 1;

      .comment-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;

        .comment-author {
          font-weight: 600;
          color: var(--text-primary);
        }

        .comment-time {
          font-size: 0.85rem;
          color: var(--text-secondary);
        }
      }

      .comment-text {
        color: var(--text-primary);
        line-height: 1.6;
      }
    }
  }

  .load-more {
    text-align: center;
    padding: 20px 0;
  }
}

/* 右侧信息卡片 */
.right-content {
  .info-card {
    background: var(--bg-primary);
    border-radius: 16px;
    padding: 32px;
    box-shadow: var(--shadow-light);
    position: sticky;
    top: 20px;

    .card-title {
      font-size: 1.3rem;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 24px;
      text-align: center;
    }

    .recommend-badge {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: var(--gradient-primary);
      color: white;
      padding: 12px 20px;
      border-radius: 12px;
      margin-bottom: 24px;
      font-weight: 600;

      .el-icon {
        font-size: 1.2rem;
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      margin-bottom: 32px;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: 12px;
        border: 1px solid var(--border-light);

        .stat-icon {
          font-size: 1.5rem;
          color: var(--primary-color);
          margin-bottom: 8px;

          &.liked {
            color: var(--danger-color);
          }

          &.collected {
            color: var(--warning-color);
          }
        }

        .stat-content {
          text-align: center;

          .stat-number {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 0.85rem;
            color: var(--text-secondary);
          }
        }
      }
    }

    .strategy-details {
      margin-bottom: 32px;

      .detail-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .detail-label {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--text-secondary);
          font-size: 0.9rem;

          .el-icon {
            color: var(--primary-color);
          }
        }

        .detail-value {
          font-weight: 600;
          color: var(--text-primary);

          &.budget-amount {
            color: var(--success-color);
            font-size: 1.1rem;
          }
        }
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .action-btn {
        width: 100%;
        height: 48px;
        border-radius: 12px;
        font-weight: 600;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
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
      margin-bottom: 24px;
    }

    .error-title {
      font-size: 1.5rem;
      color: var(--text-primary);
      margin-bottom: 12px;
      font-weight: 600;
    }

    .error-description {
      color: var(--text-secondary);
      margin-bottom: 32px;
      line-height: 1.6;
    }
  }
}

/* 评论区域样式 */
.comment-form {
  margin-bottom: 32px;
  
  .comment-input-area {
    background: var(--bg-primary);
    border-radius: 12px;
    padding: 20px;
    border: 1px solid var(--border-color);
    
    .comment-actions {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

.login-prompt {
  text-align: center;
  padding: 40px 20px;
  background: var(--bg-primary);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  margin-bottom: 32px;
  
  p {
    color: var(--text-secondary);
    margin-bottom: 16px;
    font-size: 1rem;
  }
}

.comments-list {
  .comment-loading {
    padding: 20px;
  }
  
  .no-comments {
    text-align: center;
    padding: 60px 20px;
    color: var(--text-secondary);
    
    .no-comments-icon {
      font-size: 3rem;
      margin-bottom: 16px;
    }
    
    p {
      font-size: 1rem;
    }
  }
  
  .comment-item {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid var(--border-color);
    
    &:last-child {
      border-bottom: none;
    }
    
    .comment-avatar {
      flex-shrink: 0;
      
      img {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid var(--border-color);
      }
    }
    
    .comment-content {
      flex: 1;
      
      .comment-header {
        display: flex;
        align-items: center;
        gap: 12px;
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
        color: var(--text-primary);
        line-height: 1.6;
        font-size: 0.95rem;
        word-wrap: break-word;
      }
    }
  }
  
  .load-more {
    text-align: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid var(--border-color);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cover-section {
    height: 50vh;
    min-height: 400px;

    .cover-overlay .cover-content {
      padding: 20px;

      .strategy-info .strategy-title {
        font-size: 2rem;
      }

      .strategy-meta {
        flex-direction: column;
        gap: 12px;
      }
    }
  }

  .main-content .content-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 0 16px;
  }

  .left-content .content-section {
    padding: 20px;
    margin-bottom: 20px;
  }

  .right-content .info-card {
    padding: 20px;
  }

  .trip-info-grid {
    grid-template-columns: 1fr !important;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }

  /* 门票区域移动端样式 */
  .tickets-grid {
    grid-template-columns: 1fr !important;
  }

  /* 评论区域移动端样式 */
  .comment-form .comment-input-area {
    padding: 16px;
  }
  
  .comment-item {
    gap: 12px;
    padding: 16px 0;
    
    .comment-avatar img {
      width: 36px;
      height: 36px;
    }
    
    .comment-content {
      .comment-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
      }
    }
  }
  
  .login-prompt {
    padding: 30px 16px;
  }
}
</style>