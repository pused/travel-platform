<template>
  <div class="ticket-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 门票详情内容 -->
    <div v-else-if="ticket.id" class="detail-content">
      <!-- 顶部封面图区域 -->
      <div class="cover-section">
        <div class="cover-image">
          <img :src="getImageUrl(ticket.coverImage)" :alt="ticket.name" @error="handleImageError" />
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
              
              <!-- 门票信息 -->
              <div class="ticket-info">
                <h1 class="ticket-name">{{ ticket.name }}</h1>
                <div class="ticket-meta">
                  <div class="meta-item type">
                    <el-tag 
                      :type="getTypeTagType(ticket.type)" 
                      size="large"
                      class="type-tag"
                    >
                      {{ getTypeName(ticket.type) }}
                    </el-tag>
                  </div>
                  <div class="meta-item location" v-if="ticket.attractionName">
                    <el-icon><MapLocation /></el-icon>
                    <span>{{ ticket.attractionName }}</span>
                  </div>
                  <div class="meta-item price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ formatPrice(ticket.price) }}</span>
                    <span class="price-unit">/张</span>
                  </div>
                </div>
                <p class="ticket-summary" v-if="ticket.description">{{ ticket.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 - 左右布局 -->
      <div class="main-content">
        <div class="content-container">
          <!-- 左侧：门票详细内容 -->
          <div class="left-content">
            <!-- 门票介绍 -->
            <div class="content-section" v-if="ticket.description">
              <h2 class="section-title">门票介绍</h2>
              <div class="section-content">
                <p class="description-text">{{ ticket.description }}</p>
                
                <!-- 门票特色 -->
                <div class="features-grid">
                  <div class="feature-item">
                    <div class="feature-icon">🎫</div>
                    <div class="feature-text">
                      <h4>正规门票</h4>
                      <p>官方授权，安全可靠</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">⚡</div>
                    <div class="feature-text">
                      <h4>快速入园</h4>
                      <p>电子票券，扫码入园</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">💰</div>
                    <div class="feature-text">
                      <h4>优惠价格</h4>
                      <p>平台特惠，性价比高</p>
                    </div>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">🔄</div>
                    <div class="feature-text">
                      <h4>退改保障</h4>
                      <p>支持退改，无忧购买</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 购票须知 -->
            <div class="content-section" v-if="ticket.purchaseNotes">
              <h2 class="section-title">购票须知</h2>
              <div class="section-content">
                <div class="notes-content" v-html="formatNotes(ticket.purchaseNotes)"></div>
              </div>
            </div>
          </div>

          <!-- 右侧：基本信息和购买 -->
          <div class="right-content">
            <div class="info-card">
              <h3 class="card-title">门票信息</h3>
              
              <!-- 价格信息 -->
              <div class="price-section">
                <div class="current-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ formatPrice(ticket.price) }}</span>
                  <span class="price-unit">/张</span>
                </div>
                <div v-if="ticket.originalPrice && ticket.originalPrice > ticket.price" class="original-price">
                  原价：¥{{ formatPrice(ticket.originalPrice) }}
                </div>
              </div>

              <!-- 门票基本信息 -->
              <div class="ticket-details">
                <div class="detail-item">
                  <div class="detail-label">
                    <el-icon><Clock /></el-icon>
                    有效期
                  </div>
                  <div class="detail-value">{{ ticket.validityPeriod }}天</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">
                    <el-icon><Star /></el-icon>
                    库存
                  </div>
                  <div class="detail-value">{{ ticket.inventory }}张</div>
                </div>
              </div>

              <!-- 购买区域 -->
              <div class="purchase-section">
                <div class="quantity-selector">
                  <span class="quantity-label">购买数量：</span>
                  <el-input-number
                    v-model="purchaseQuantity"
                    :min="1"
                    :max="Math.min(ticket.inventory, 10)"
                    size="large"
                    class="quantity-input"
                  />
                </div>
                <el-button
                  type="primary"
                  size="large"
                  class="purchase-button"
                  :disabled="!isLoggedIn || ticket.inventory <= 0"
                  @click="handlePurchase"
                  :loading="purchaseLoading"
                >
                  <el-icon><Money /></el-icon>
                  {{ !isLoggedIn ? '请先登录' : ticket.inventory <= 0 ? '暂无库存' : '立即购买' }}
                </el-button>
                <div class="total-price">
                  总计：¥{{ formatPrice(ticket.price * purchaseQuantity) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="error-content">
        <div class="error-icon">🎫</div>
        <h3 class="error-title">门票不存在</h3>
        <p class="error-description">抱歉，您访问的门票信息不存在或已下架</p>
        <el-button type="primary" @click="goBack">返回上一页</el-button>
      </div>
    </div>

    <!-- 购买信息填写弹窗 -->
    <el-dialog
      v-model="orderDialogVisible"
      title="填写订单信息"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form
        ref="orderFormRef"
        :model="orderForm"
        :rules="orderFormRules"
        label-width="100px"
        label-position="left"
      >
        <!-- 门票信息展示 -->
        <div class="order-ticket-info">
          <div class="ticket-summary">
            <img :src="getImageUrl(ticket.coverImage)" :alt="ticket.name" class="ticket-thumb" />
            <div class="ticket-details">
              <h4>{{ ticket.name }}</h4>
              <p class="ticket-price">¥{{ formatPrice(ticket.price) }} × {{ purchaseQuantity }}张</p>
              <p class="total-amount">总计：¥{{ formatPrice(ticket.price * purchaseQuantity) }}</p>
            </div>
          </div>
        </div>

        <!-- 联系人信息 -->
        <el-form-item label="联系人姓名" prop="contactName">
          <el-input
            v-model="orderForm.contactName"
            placeholder="请输入联系人姓名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系人手机" prop="contactPhone">
          <el-input
            v-model="orderForm.contactPhone"
            placeholder="请输入联系人手机号"
            maxlength="11"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="身份证号" prop="cardNo">
          <el-input
            v-model="orderForm.cardNo"
            placeholder="请输入身份证号码"
            maxlength="18"
            show-word-limit
          />
        </el-form-item>

        <!-- 购买须知 -->
        <div class="purchase-notice">
          <h4>购买须知</h4>
          <ul>
            <li>请确保联系人信息准确无误，以便顺利入园</li>
            <li>门票一经售出，不支持退换，请谨慎购买</li>
            <li>入园时请携带有效身份证件</li>
            <li>门票有效期以实际购买时间为准</li>
          </ul>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="orderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmOrder" :loading="orderConfirmLoading">
            确认订单
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="paymentDialogVisible"
      title="扫码支付"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="payment-content">
        <!-- 订单信息 -->
        <div class="payment-order-info">
          <h4>{{ ticket.name }}</h4>
          <p class="payment-amount">支付金额：¥{{ formatPrice(ticket.price * purchaseQuantity) }}</p>
        </div>

        <!-- 二维码 -->
        <div class="qr-code-container">
          <vue-qrcode
            :value="paymentQRCode"
            :options="{ width: 200 }"
            class="qr-code"
          />
          <p class="qr-tip">请使用微信或支付宝扫码支付</p>
        </div>

        <!-- 倒计时 -->
        <div class="countdown-container">
          <p class="countdown-text">
            支付剩余时间：<span class="countdown-time">{{ formatCountdown(countdown) }}</span>
          </p>
          <el-progress
            :percentage="countdownPercentage"
            :show-text="false"
            :stroke-width="6"
            color="#409EFF"
          />
        </div>

        <!-- 支付状态 -->
        <div class="payment-status" v-if="paymentStatus">
          <el-icon class="status-icon" :class="paymentStatus === 'success' ? 'success' : 'failed'">
            <Check v-if="paymentStatus === 'success'" />
            <Close v-else />
          </el-icon>
          <p class="status-text">
            {{ paymentStatus === 'success' ? '支付成功！' : '支付失败，请重试' }}
          </p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelPayment">取消支付</el-button>
          <el-button type="primary" @click="confirmPayment" :loading="paymentLoading">
            确认支付
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, MapLocation, Clock, Star, Money, Check, Close } from '@element-plus/icons-vue';
import VueQrcode from 'vue-qrcode'
import http from '@/utils/http';
import { getImageUrl } from '@/utils/system';
import useUserStore from '@/stores/userStore';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const ticket = ref({});
const loading = ref(false);
const purchaseQuantity = ref(1);
const purchaseLoading = ref(false);

// 订单弹窗相关
const orderDialogVisible = ref(false);
const orderFormRef = ref(null);
const orderConfirmLoading = ref(false);
const orderForm = ref({
  contactName: '',
  contactPhone: '',
  cardNo: ''
});

// 支付弹窗相关
const paymentDialogVisible = ref(false);
const paymentLoading = ref(false);
const paymentQRCode = ref('');
const paymentStatus = ref(''); // 'success' | 'failed' | ''
const countdown = ref(300); // 5分钟倒计时
const countdownTimer = ref(null);

// 表单验证规则
const orderFormRules = {
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系人手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  cardNo: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    // { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ]
};

// 计算属性（前台会话）
const currentUser = computed(() => userStore.frontUserInfo || {});

const isLoggedIn = computed(() => {
  return !!userStore.frontToken && !!currentUser.value?.id;
});

// 倒计时百分比
const countdownPercentage = computed(() => {
  return ((300 - countdown.value) / 300) * 100;
});

// 获取门票详情
const getTicketDetail = async () => {
  const id = route.params.id;
  if (!id) {
    ElMessage.error('门票ID不能为空');
    router.push('/front/tickets');
    return;
  }

  loading.value = true;
  try {
    const res = await http.get('/ticket/getById', { 
      params: { id } 
    });
    if (res.code === 200) {
      ticket.value = res.data;
      
      // 如果有景点ID，获取景点名称
      if (ticket.value.attractionId) {
        await getAttractionName(ticket.value.attractionId);
      }
    } else {
      ElMessage.error(res.message || '获取门票详情失败');
    }
  } catch (error) {
    console.error('获取门票详情失败:', error);
    ElMessage.error('获取门票详情失败');
  } finally {
    loading.value = false;
  }
};

// 获取景点名称
const getAttractionName = async (attractionId) => {
  try {
    const res = await http.get('/attraction/getById', { 
      params: { id: attractionId } 
    });
    if (res.code === 200 && res.data) {
      ticket.value.attractionName = res.data.name;
    }
  } catch (error) {
    console.error('获取景点名称失败:', error);
  }
};

// 获取票种类型名称
const getTypeName = (type) => {
  const typeMap = {
    1: '成人票',
    2: '儿童票', 
    3: '学生票',
    4: '老人票'
  };
  return typeMap[type] || '未知类型';
};

// 获取票种标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    1: 'primary',
    2: 'success',
    3: 'warning', 
    4: 'info'
  };
  return typeMap[type] || '';
};

// 格式化价格
const formatPrice = (price) => {
  return Number(price).toFixed(2);
};

// 格式化购票须知
const formatNotes = (notes) => {
  if (!notes) return '';
  return notes.replace(/\n/g, '<br>');
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/src/assets/images/default-ticket.jpg';
};

// 处理购买
const handlePurchase = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    return;
  }

  if (ticket.value.inventory <= 0) {
    ElMessage.warning('门票库存不足');
    return;
  }

  // 打开订单信息填写弹窗
  orderDialogVisible.value = true;
};

// 确认订单信息
const confirmOrder = async () => {
  if (!orderFormRef.value) return;
  
  try {
    await orderFormRef.value.validate();
    orderConfirmLoading.value = true;
    
    // 模拟验证延迟
    setTimeout(() => {
      orderConfirmLoading.value = false;
      orderDialogVisible.value = false;
      
      // 生成支付二维码（模拟）
      paymentQRCode.value = `payment://ticket/${ticket.value.id}/${Date.now()}`;
      
      // 重置支付状态
      paymentStatus.value = '';
      countdown.value = 300;
      
      // 打开支付弹窗
      paymentDialogVisible.value = true;
      
      // 开始倒计时
      startCountdown();
    }, 1000);
    
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

// 开始倒计时
const startCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
  
  countdownTimer.value = setInterval(() => {
    countdown.value--;
    
    if (countdown.value <= 0) {
      clearInterval(countdownTimer.value);
      countdownTimer.value = null;
      
      // 倒计时结束，自动取消支付
      if (paymentDialogVisible.value && !paymentStatus.value) {
        cancelPayment();
        ElMessage.warning('支付超时，请重新下单');
      }
    }
  }, 1000);
};

// 格式化倒计时显示
const formatCountdown = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
};

// 确认支付
const confirmPayment = async () => {
  paymentLoading.value = true;
  
  try {
    // 创建订单
    const orderData = {
      userId: currentUser.value.id,
      ticketId: ticket.value.id,
      orderNo: `TK${Date.now()}${Math.floor(Math.random() * 1000)}`,
      payNum: purchaseQuantity.value,
      totalAmount: ticket.value.price * purchaseQuantity.value,
      status: 1, // 已付款
      contactName: orderForm.value.contactName,
      contactPhone: orderForm.value.contactPhone,
      cardNo: orderForm.value.cardNo
    };
    
    const res = await http.post('/ticketOrder/add', orderData);
    
    if (res.code === 200) {
      // 停止倒计时
      if (countdownTimer.value) {
        clearInterval(countdownTimer.value);
        countdownTimer.value = null;
      }
      
      paymentStatus.value = 'success';
      
      setTimeout(() => {
        paymentLoading.value = false;
        paymentDialogVisible.value = false;
        
        ElMessage.success('支付成功！订单已创建');
        // 刷新数据
        getTicketDetail();
        
        // 重置表单
        resetForms();
      }, 2000);
    } else {
      throw new Error(res.message || '订单创建失败');
    }
    
  } catch (error) {
    console.error('支付失败:', error);
    paymentStatus.value = 'failed';
    paymentLoading.value = false;
    
    setTimeout(() => {
      paymentStatus.value = '';
    }, 3000);
    
    ElMessage.error('支付失败，请重试');
  }
};

// 取消支付
const cancelPayment = () => {
  // 停止倒计时
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
    countdownTimer.value = null;
  }
  
  paymentDialogVisible.value = false;
  paymentStatus.value = '';
  
  // 重置表单
  resetForms();
};

// 重置表单
const resetForms = () => {
  orderForm.value = {
    contactName: '',
    contactPhone: '',
    cardNo: ''
  };
  
  if (orderFormRef.value) {
    orderFormRef.value.resetFields();
  }
  
  purchaseQuantity.value = 1;
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 页面加载时获取门票详情
onMounted(() => {
  getTicketDetail();
});

// 组件卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
    countdownTimer.value = null;
  }
});
</script>

<style lang="scss" scoped>
.ticket-detail-page {
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

        .ticket-info {
          color: white;

          .ticket-name {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
            line-height: 1.2;
          }

          .ticket-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 30px;
            margin-bottom: 20px;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 1.1rem;

              &.type {
                .type-tag {
                  font-weight: 600;
                  background: rgba(255, 255, 255, 0.9);
                  color: var(--primary-700);
                  border: none;
                  border-radius: 20px;
                  padding: 8px 16px;
                  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                }
              }

              &.location {
                .el-icon {
                  color: var(--secondary-color);
                }
              }

              &.price {
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                padding: 8px 16px;
                border-radius: 12px;
                border: 1px solid rgba(255, 255, 255, 0.2);

                .price-symbol {
                  font-size: 1.2rem;
                  color: var(--secondary-color);
                  font-weight: 700;
                }

                .price-value {
                  font-size: 1.8rem;
                  color: var(--secondary-color);
                  font-weight: 800;
                  margin: 0 4px;
                }

                .price-unit {
                  font-size: 1rem;
                  color: var(--secondary-color);
                  font-weight: 500;
                }
              }
            }
          }

          .ticket-summary {
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

      .notes-content {
        font-size: 1rem;
        color: var(--text-secondary);
        line-height: 1.7;

        :deep(br) {
          margin-bottom: 8px;
        }
      }
    }
  }
}

/* 右侧内容 */
.right-content {
  .info-card {
    background: white;
    border-radius: 20px;
    padding: 30px;
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-light);
    position: sticky;
    top: 100px;

    .card-title {
      font-size: 1.3rem;
      font-weight: bold;
      color: var(--text-primary);
      margin-bottom: 25px;
      text-align: center;
      padding-bottom: 15px;
      border-bottom: 2px solid var(--primary-100);
    }

    .price-section {
      margin-bottom: 25px;
      padding: 24px;
      background: linear-gradient(135deg, var(--secondary-50) 0%, var(--secondary-100) 100%);
      border-radius: 16px;
      border: 2px solid var(--secondary-200);
      box-shadow: 0 4px 16px rgba(245, 158, 11, 0.1);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, var(--secondary-400), var(--secondary-600));
      }

      .current-price {
        display: flex;
        align-items: baseline;
        justify-content: center;
        gap: 6px;
        margin-bottom: 8px;

        .price-symbol {
          font-size: 24px;
          color: var(--secondary-600);
          font-weight: 700;
        }

        .price-value {
          font-size: 36px;
          color: var(--secondary-700);
          font-weight: 800;
          text-shadow: 0 2px 4px rgba(245, 158, 11, 0.2);
        }

        .price-unit {
          font-size: 16px;
          color: var(--secondary-600);
          font-weight: 500;
        }
      }

      .original-price {
        text-align: center;
        color: var(--gray-500);
        text-decoration: line-through;
        font-size: 14px;
        background: var(--gray-100);
        padding: 4px 12px;
        border-radius: 8px;
        display: inline-block;
        margin: 0 auto;
      }
    }

    .ticket-details {
      margin-bottom: 25px;
      background: var(--primary-50);
      border-radius: 12px;
      padding: 16px;
      border: 1px solid var(--primary-200);

      .detail-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--primary-200);

        &:last-child {
          border-bottom: none;
        }

        .detail-label {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--primary-600);
          font-weight: 600;

          .el-icon {
            color: var(--primary-500);
            background: var(--primary-100);
            padding: 4px;
            border-radius: 6px;
          }
        }

        .detail-value {
          color: var(--primary-700);
          font-weight: 700;
          background: var(--primary-100);
          padding: 4px 12px;
          border-radius: 8px;
        }
      }
    }

    .purchase-section {
      padding: 24px;
      background: linear-gradient(135deg, var(--primary-50) 0%, var(--accent-50) 100%);
      border-radius: 16px;
      border: 2px solid var(--primary-200);
      box-shadow: 0 4px 16px rgba(30, 144, 255, 0.1);

      .quantity-selector {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20px;

        .quantity-label {
          font-weight: 600;
          color: var(--primary-700);
        }

        .quantity-input {
          width: 120px;

          :deep(.el-input__wrapper) {
            border: 2px solid var(--primary-300);
            border-radius: 10px;
            background: var(--bg-primary);
            transition: all 0.3s ease;

            &:hover {
              border-color: var(--primary-400);
              box-shadow: 0 2px 8px rgba(30, 144, 255, 0.1);
            }

            &.is-focus {
              border-color: var(--primary-500);
              box-shadow: 0 0 0 3px var(--primary-100);
            }
          }
        }
      }

      .purchase-button {
        width: 100%;
        height: 54px;
        font-size: 18px;
        font-weight: 700;
        margin-bottom: 15px;
        background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
        border: none;
        border-radius: 14px;
        box-shadow: 0 6px 20px rgba(30, 144, 255, 0.3);
        transition: all 0.3s ease;
        color: white;

        &:hover {
          background: linear-gradient(135deg, var(--primary-600), var(--primary-700));
          transform: translateY(-3px);
          box-shadow: 0 8px 25px rgba(30, 144, 255, 0.4);
        }

        &:disabled {
          background: var(--gray-300);
          color: var(--gray-500);
          transform: none;
          box-shadow: none;
        }
      }

      .total-price {
        text-align: center;
        font-size: 18px;
        font-weight: 700;
        color: var(--secondary-700);
        background: linear-gradient(135deg, var(--secondary-100), var(--secondary-200));
        padding: 12px;
        border-radius: 12px;
        border: 2px solid var(--secondary-300);
        box-shadow: 0 2px 8px rgba(245, 158, 11, 0.1);
      }
    }
  }
}

/* 错误状态 */
.error-state {
  padding: 60px 20px;

  .error-content {
    text-align: center;
    background: white;
    border-radius: 24px;
    padding: 60px 30px;
    box-shadow: var(--shadow-light);
    max-width: 600px;
    margin: 0 auto;
    border: 1px solid var(--border-light);

    .error-icon {
      font-size: 64px;
      margin-bottom: 20px;
      filter: grayscale(1);
      opacity: 0.6;
    }

    .error-title {
      font-size: 24px;
      color: var(--text-primary);
      margin: 0 0 15px 0;
      font-weight: 700;
    }

    .error-description {
      color: var(--text-secondary);
      margin: 0 0 30px 0;
      line-height: 1.6;
    }

    .el-button {
      background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
      border: none;
      border-radius: 12px;
      box-shadow: 0 4px 16px rgba(30, 144, 255, 0.3);
      color: white;
      font-weight: 600;
    }
  }
}

/* 订单弹窗样式 */
.order-ticket-info {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  
  .ticket-summary {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .ticket-thumb {
      width: 60px;
      height: 60px;
      border-radius: 6px;
      object-fit: cover;
    }
    
    .ticket-details {
      flex: 1;
      
      h4 {
        margin: 0 0 8px 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
      
      .ticket-price {
        margin: 0 0 4px 0;
        font-size: 14px;
        color: #666;
      }
      
      .total-amount {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #e74c3c;
      }
    }
  }
}

.purchase-notice {
  margin-top: 20px;
  padding: 16px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
  
  h4 {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #d46b08;
  }
  
  ul {
    margin: 0;
    padding-left: 16px;
    
    li {
      margin-bottom: 6px;
      font-size: 13px;
      color: #8c6239;
      line-height: 1.4;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

/* 支付弹窗样式 */
.payment-content {
  text-align: center;
  
  .payment-order-info {
    margin-bottom: 24px;
    
    h4 {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .payment-amount {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #e74c3c;
    }
  }
  
  .qr-code-container {
    margin-bottom: 24px;
    
    .qr-code {
      display: block;
      margin: 0 auto 12px auto;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 16px;
      background: white;
    }
    
    .qr-tip {
      margin: 0;
      font-size: 14px;
      color: #666;
    }
  }
  
  .countdown-container {
    margin-bottom: 20px;
    
    .countdown-text {
      margin: 0 0 12px 0;
      font-size: 14px;
      color: #333;
      
      .countdown-time {
        font-weight: 600;
        color: #409EFF;
      }
    }
  }
  
  .payment-status {
    margin-top: 20px;
    
    .status-icon {
      font-size: 48px;
      margin-bottom: 12px;
      
      &.success {
        color: #67c23a;
      }
      
      &.failed {
        color: #f56c6c;
      }
    }
    
    .status-text {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      
      &.success {
        color: #67c23a;
      }
      
      &.failed {
        color: #f56c6c;
      }
    }
  }
}

@media (max-width: 768px) {  .cover-section {
    height: 50vh;
    min-height: 400px;

    .cover-overlay {
      padding: 20px;

      .cover-content {
        .back-button {
          top: -40px;
        }

        .ticket-info {
          .ticket-name {
            font-size: 2rem;
          }

          .ticket-meta {
            flex-direction: column;
            align-items: flex-start;
            gap: 15px;

            .meta-item {
              &.price {
                align-self: flex-start;
              }
            }
          }

          .ticket-summary {
            font-size: 1rem;
          }
        }
      }
    }
  }

  .main-content {
    padding: 40px 0;

    .content-container {
      grid-template-columns: 1fr;
      gap: 30px;
      padding: 0 15px;
    }
  }

  .right-content {
    .info-card {
      position: static;
      padding: 20px;

      .price-section {
        padding: 20px;

        .current-price {
          .price-value {
            font-size: 28px;
          }

          .price-symbol {
            font-size: 20px;
          }
        }
      }

      .purchase-section {
        padding: 20px;

        .quantity-selector {
          flex-direction: column;
          align-items: flex-start;
          gap: 12px;

          .quantity-input {
            width: 100%;
          }
        }

        .purchase-button {
          height: 48px;
          font-size: 16px;
        }

        .total-price {
          font-size: 16px;
          padding: 10px;
        }
      }
    }
  }

  .left-content {
    .content-section {
      padding: 20px;
      margin-bottom: 20px;

      .section-title {
        font-size: 1.3rem;
      }

      .section-content {
        .features-grid {
          grid-template-columns: 1fr;
          gap: 15px;

          .feature-item {
            padding: 15px;

            .feature-icon {
              width: 50px;
              height: 50px;
              font-size: 1.5rem;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .cover-section {
    height: 40vh;
    min-height: 300px;

    .cover-overlay {
      padding: 15px;

      .cover-content {
        .ticket-info {
          .ticket-name {
            font-size: 1.5rem;
          }

          .ticket-meta {
            gap: 10px;

            .meta-item {
              font-size: 1rem;

              &.price {
                .price-value {
                  font-size: 1.4rem;
                }
              }
            }
          }

          .ticket-summary {
            font-size: 0.9rem;
          }
        }
      }
    }
  }

  .main-content {
    padding: 30px 0;

    .content-container {
      padding: 0 10px;
    }
  }

  .left-content {
    .content-section {
      padding: 15px;

      .section-title {
        font-size: 1.2rem;
      }
    }
  }

  .right-content {
    .info-card {
      padding: 15px;

      .price-section {
        padding: 15px;

        .current-price {
          .price-value {
            font-size: 24px;
          }
        }
      }

      .purchase-section {
        padding: 15px;

        .purchase-button {
          height: 44px;
          font-size: 15px;
        }
      }
    }
  }
}
</style>