<script setup>
import { ref, onMounted, nextTick, computed, watch } from 'vue';
import { Promotion, PartlyCloudy, User } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import http from '@/utils/http';

const STORAGE_KEY = 'ai-chat-messages';

// 聊天数据
const messages = ref([]);
const inputMessage = ref('');
const loading = ref(false);
const chatContainer = ref(null);

// 计算属性
const hasMessages = computed(() => messages.value.length > 0);

// 自动滚动到底部
const scrollToBottom = async () => {
  await nextTick();
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容');
    return;
  }

  const userMessage = {
    id: Date.now(),
    type: 'user',
    content: inputMessage.value.trim(),
    timestamp: new Date()
  };

  // 添加用户消息
  messages.value.push(userMessage);
  const query = inputMessage.value.trim();
  inputMessage.value = '';
  
  // 滚动到底部
  await scrollToBottom();

  // 添加AI思考状态
  const thinkingMessage = {
    id: Date.now() + 1,
    type: 'ai',
    content: '正在思考中...',
    timestamp: new Date(),
    isThinking: true
  };
  messages.value.push(thinkingMessage);
  await scrollToBottom();

  try {
    loading.value = true;
    
    // 调用AI接口
    const formData = new FormData();
    formData.append('query', query);
    
    const response = await http.post('/ai/send', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.code === 200) {
      // 移除思考状态消息
      messages.value = messages.value.filter(msg => !msg.isThinking);
      
      // 添加AI回复
      const aiMessage = {
        id: Date.now() + 2,
        type: 'ai',
        content: response.data,
        timestamp: new Date()
      };
      messages.value.push(aiMessage);
      await scrollToBottom();
    } else {
      ElMessage.error('AI助手暂时无法回复，请稍后再试');
      // 移除思考状态消息
      messages.value = messages.value.filter(msg => !msg.isThinking);
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    ElMessage.error('发送失败，请检查网络连接');
    // 移除思考状态消息
    messages.value = messages.value.filter(msg => !msg.isThinking);
  } finally {
    loading.value = false;
  }
};

// 处理回车发送
const handleKeyPress = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
};

// 清空聊天记录
const clearChat = () => {
  messages.value = [];
  localStorage.removeItem(STORAGE_KEY);
  ElMessage.success('聊天记录已清空');
};

// 格式化时间
const formatTime = (date) => {
  const parsedDate = typeof date === 'string' ? new Date(date) : date;
  return new Intl.DateTimeFormat('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(parsedDate);
};

// 从本地存储恢复聊天记录，保持一次会话内的数据
const loadMessages = () => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      const parsed = JSON.parse(stored).map((msg) => ({
        ...msg,
        timestamp: msg.timestamp ? new Date(msg.timestamp) : new Date()
      }));
      messages.value = parsed;
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error);
  }
};

// 消息变化时写回本地存储，避免路由切换时丢失
const saveMessages = () => {
  try {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(
        messages.value.map((msg) => ({
          ...msg,
          timestamp: msg.timestamp instanceof Date ? msg.timestamp : new Date(msg.timestamp)
        }))
      )
    );
  } catch (error) {
    console.error('保存聊天记录失败:', error);
  }
};

watch(
  messages,
  () => {
    saveMessages();
  },
  { deep: true }
);

// 页面初始化
onMounted(() => {
  loadMessages();
  if (messages.value.length === 0) {
    const welcomeMessage = {
      id: 1,
      type: 'ai',
      content: '您好！我是您的AI旅游助手，可以为您提供旅游攻略、景点推荐、行程规划等服务。有什么可以帮助您的吗？',
      timestamp: new Date()
    };
    messages.value.push(welcomeMessage);
  }
  scrollToBottom();
});
</script>

<template>
  <div class="ai-chat-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><PartlyCloudy /></el-icon>
          AI 旅游助手
          <el-icon class="title-icon"><PartlyCloudy /></el-icon>
        </h1>
        <p class="page-subtitle">
          智能旅游规划，让您的旅行更精彩
        </p>
      </div>
      <div class="header-actions">
        <el-button 
          type="danger" 
          plain 
          size="small" 
          @click="clearChat"
          v-if="hasMessages"
        >
          清空聊天
        </el-button>
      </div>
    </div>

    <!-- 聊天容器 -->
    <div class="chat-container">
      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="chatContainer">
        <div class="messages-wrapper">
          <!-- 空状态 -->
          <div v-if="!hasMessages" class="empty-state">
            <div class="empty-icon">🤖</div>
            <h3 class="empty-title">开始与AI助手对话</h3>
            <p class="empty-description">
              我可以为您提供旅游建议、景点推荐、行程规划等服务
            </p>
          </div>

          <!-- 消息列表 -->
          <div v-else class="message-list">
            <div
              v-for="message in messages"
              :key="message.id"
              :class="['message-item', message.type]"
            >
              <div class="message-avatar">
                <el-icon v-if="message.type === 'ai'" class="ai-avatar">
                  <PartlyCloudy />
                </el-icon>
                <el-icon v-else class="user-avatar">
                  <User />
                </el-icon>
              </div>
              
              <div class="message-content">
                <div class="message-header">
                  <span class="message-sender">
                    {{ message.type === 'ai' ? 'AI助手' : '我' }}
                  </span>
                  <span class="message-time">
                    {{ formatTime(message.timestamp) }}
                  </span>
                </div>
                
                <div class="message-body">
                  <!-- AI消息使用v-html渲染 -->
                  <div 
                    v-if="message.type === 'ai'"
                    class="ai-message"
                    v-html="message.content"
                  ></div>
                  <!-- 用户消息普通文本 -->
                  <div v-else class="user-message">
                    {{ message.content }}
                  </div>
                  
                  <!-- 思考状态 -->
                  <div v-if="message.isThinking" class="thinking-indicator">
                    <div class="thinking-dots">
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            resize="none"
            maxlength="1000"
            show-word-limit
            @keypress="handleKeyPress"
            :disabled="loading"
          />
          <el-button
            type="primary"
            :icon="Promotion"
            class="send-button"
            @click="sendMessage"
            :loading="loading"
            :disabled="!inputMessage.trim()"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.ai-chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-accent) 100%);
  overflow: hidden; /* 防止整个页面滚动 */
}

/* 页面头部 */
.page-header {
  background: var(--gradient-primary);
  color: white;
  padding: 60px 0 40px;
  text-align: center;
  position: relative;
  flex-shrink: 0;
  box-shadow: var(--shadow-medium);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-title {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;

    .title-icon {
      font-size: 2.2rem;
      animation: pulse 2s ease-in-out infinite;
      
      &:first-child {
        animation-delay: 0s;
      }
      
      &:last-child {
        animation-delay: 1s;
      }
    }
  }

  .page-subtitle {
    font-size: 1.1rem;
    opacity: 0.9;
    font-weight: 300;
    margin: 0;
  }

  .header-actions {
    position: absolute;
    top: 50%;
    right: 20px;
    transform: translateY(-50%);
  }
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  min-height: 0; /* 允许flex子元素收缩 */
  overflow: hidden; /* 防止容器溢出 */
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  margin-bottom: 20px;
  min-height: 0; /* 允许flex子元素收缩 */
  height: 0; /* 强制使用flex分配的高度 */
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  -ms-overflow-style: none; /* IE/Edge 隐藏滚动条 */

  &::-webkit-scrollbar {
    display: none; /* WebKit 隐藏滚动条 */
  }

  .messages-wrapper {
    min-height: 100%;
    display: flex;
    flex-direction: column;
  }

  .empty-state {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
    padding: 60px 20px;

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
      line-height: 1.6;
      max-width: 400px;
    }
  }

  .message-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  animation: fadeInUp 0.3s ease-out;

  &.user {
    flex-direction: row-reverse;
    
    .message-content {
      background: var(--primary-color);
      color: white;
      border-radius: 18px 18px 4px 18px;
    }
    
    .message-header {
      text-align: right;
    }
  }

  &.ai {
    .message-content {
      background: var(--bg-primary);
      color: var(--text-primary);
      border-radius: 18px 18px 18px 4px;
      border: 1px solid var(--border-light);
    }
  }

  .message-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    
    .ai-avatar {
      background: var(--accent-color);
      color: white;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.2rem;
    }
    
    .user-avatar {
      background: var(--primary-color);
      color: white;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.2rem;
    }
  }

  .message-content {
    max-width: 70%;
    padding: 12px 16px;
    box-shadow: var(--shadow-light);
  }

  .message-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
    font-size: 0.85rem;
    opacity: 0.8;
  }

  .message-sender {
    font-weight: 600;
  }

  .message-time {
    font-size: 0.8rem;
  }

  .message-body {
    line-height: 1.6;
    word-wrap: break-word;
  }

  .ai-message {
    :deep(p) {
      margin: 0 0 8px 0;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    :deep(ul), :deep(ol) {
      margin: 8px 0;
      padding-left: 20px;
    }
    
    :deep(li) {
      margin: 4px 0;
    }
    
    :deep(strong) {
      font-weight: 600;
      color: var(--primary-color);
    }
    
    :deep(code) {
      background: var(--bg-secondary);
      padding: 2px 6px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 0.9em;
    }
  }

  .user-message {
    white-space: pre-wrap;
  }
}

/* 思考指示器 */
.thinking-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;

  .thinking-dots {
    display: flex;
    gap: 4px;

    span {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--primary-color);
      animation: thinking 1.4s ease-in-out infinite;

      &:nth-child(1) { animation-delay: 0s; }
      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }
}

/* 输入区域 */
.chat-input-area {
  flex-shrink: 0;
  background: var(--bg-primary);
  border-radius: 20px 20px 0 0;
  padding: 20px;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  border-top: 1px solid var(--border-light);

  .input-container {
    display: flex;
    gap: 12px;
    align-items: flex-end;

    .el-textarea {
      flex: 1;
    }

    .send-button {
      height: 48px;
      padding: 0 24px;
      border-radius: 12px;
      font-weight: 600;
      background: var(--gradient-primary);
      border: none;
      
      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: var(--shadow-medium);
      }
      
      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid var(--border-light);
  background: var(--bg-primary);
  resize: none;
  font-family: inherit;
  line-height: 1.5;
  
  &:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px var(--primary-100);
  }
}

:deep(.el-button--danger.is-plain) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
  
  &:hover {
    background: rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.5);
  }
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes thinking {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px 0;
    
    .page-title {
      font-size: 2rem;
    }
    
    .header-actions {
      position: static;
      transform: none;
      margin-top: 15px;
      text-align: center;
    }
  }

  .chat-container {
    padding: 0 15px;
  }

  .message-item {
    .message-content {
      max-width: 85%;
    }
  }

  .chat-input-area {
    padding: 15px;
    
    .input-container {
      flex-direction: column;
      align-items: stretch;
      
      .send-button {
        height: 44px;
        margin-top: 8px;
      }
    }
  }
}

@media (max-width: 480px) {
  .page-header {
    .page-title {
      font-size: 1.8rem;
      flex-direction: column;
      gap: 8px;
    }
  }

  .message-item {
    .message-content {
      max-width: 90%;
    }
  }
}
</style>