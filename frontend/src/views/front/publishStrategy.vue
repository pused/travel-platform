<script setup>
import http from "@/utils/http";
import { Plus, Upload, Edit, Check, Close } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import useUserStore from "../../stores/userStore";
import Config from "@/config/index";

const router = useRouter();
const userStore = useUserStore();

// 表单数据
const form = ref({
  title: "",
  attractionId: "",
  coverImage: "",
  intro: "",
  content: "",
  days: 1,
  budgetType: 1,
  estimatedBudget: "",
  season: "",
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: "请输入攻略标题", trigger: "blur" }
  ],
  attractionId: [
    { required: true, message: "请选择关联景点", trigger: "change" }
  ],
  intro: [
    { required: true, message: "请输入攻略摘要", trigger: "blur" }
  ],
  content: [
    { required: true, message: "请输入攻略内容", trigger: "blur" }
  ],
  days: [
    { required: true, message: "请输入行程天数", trigger: "blur" }
  ],
  estimatedBudget: [
    { required: true, message: "请输入预估预算", trigger: "blur" }
  ],
  season: [
    { required: true, message: "请选择适合季节", trigger: "change" }
  ]
};

// 景点列表
const attractions = ref([]);
const attractionLoading = ref(false);

// 预算类型选项
const budgetTypeOptions = [
  { label: "经济", value: 1, color: "#67c23a", icon: "💰" },
  { label: "舒适", value: 2, color: "#e6a23c", icon: "🏨" },
  { label: "豪华", value: 3, color: "#f56c6c", icon: "💎" },
];

// 季节选项
const seasonOptions = [
  { label: "春季", value: "春季", color: "#67c23a", icon: "🌸" },
  { label: "夏季", value: "夏季", color: "#409eff", icon: "☀️" },
  { label: "秋季", value: "秋季", color: "#e6a23c", icon: "🍂" },
  { label: "冬季", value: "冬季", color: "#909399", icon: "❄️" },
  { label: "四季皆宜", value: "四季皆宜", color: "#f56c6c", icon: "🌈" },
];

// 表单引用
const formRef = ref();

// 提交状态
const submitting = ref(false);

// 文件上传相关
const uploadRef = ref();
const imageUrl = ref("");
// 文件上传地址
const uploadUrl = ref(`${Config.baseUrl}/file/upload`);
// 文件上传携带的数据
const uploadData = {
  authorization: `${localStorage.getItem("token")}`,
};

// 获取景点列表
const getAttractions = async () => {
  attractionLoading.value = true;
  try {
    const res = await http.get("/attraction/list");
    if (res.code === 200) {
      attractions.value = res.data;
    }
  } catch (error) {
    ElMessage.error("获取景点列表失败");
  } finally {
    attractionLoading.value = false;
  }
};

// 文件上传前的校验
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
};

// 文件上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.coverImage = response.data;
    imageUrl.value = `${Config.baseUrl}${response.data}`;
    ElMessage.success("图片上传成功");
  } else {
    ElMessage.error("图片上传失败");
  }
};

// 文件上传失败
const handleUploadError = () => {
  ElMessage.error("图片上传失败");
};

// 移除图片
const handleRemove = () => {
  form.value.coverImage = "";
  imageUrl.value = "";
};

// 获取预算类型信息
const getBudgetTypeInfo = (type) => {
  return budgetTypeOptions.find(item => item.value === type) || budgetTypeOptions[0];
};

// 获取季节信息
const getSeasonInfo = (season) => {
  return seasonOptions.find(item => item.value === season) || seasonOptions[0];
};

// 计算属性（前台会话）
const currentUser = computed(() => userStore.frontUserInfo || {});
const frontToken = computed(() => userStore.frontToken || "");

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    submitting.value = true;
    
    const submitData = {
      ...form.value,
      userId: currentUser.value?.id,
      estimatedBudget: parseFloat(form.value.estimatedBudget),
    };
    
    const res = await http.post("/strategy/add", submitData);
    
    if (res.code === 200) {
      ElMessage.success("攻略发布成功！");
      // 跳转到我的攻略页面
      router.push("/front/myStrategy");
    } else {
      ElMessage.error(res.message || "发布失败");
    }
  } catch (error) {
    console.error("表单验证失败:", error);
  } finally {
    submitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  imageUrl.value = "";
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 保存草稿
const saveDraft = () => {
  ElMessage.info("保存草稿功能暂未实现");
};

onMounted(() => {
  getAttractions();
});
</script>

<template>
  <div class="publish-strategy-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <el-button 
            :icon="Close" 
            @click="goBack"
            class="back-button"
            circle
          />
          <div class="title-section">
            <h1 class="page-title">
              <i class="title-icon">✍️</i>
              发布攻略
              <i class="title-icon">📝</i>
            </h1>
            <p class="page-subtitle">
              分享您的旅行经验，帮助更多人发现美好
            </p>
          </div>
        </div>
        <div class="header-actions">
          <el-button 
            type="primary" 
            :icon="Check"
            @click="submitForm"
            :loading="submitting"
            class="publish-button"
          >
            {{ submitting ? "发布中..." : "发布攻略" }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="strategy-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="section-icon">📋</i>
              基本信息
            </h2>
          </div>
          
          <div class="form-grid">
            <el-form-item label="攻略标题" prop="title" class="full-width">
              <el-input
                v-model="form.title"
                placeholder="请输入攻略标题，如：桂林山水三日游完美攻略"
                maxlength="100"
                show-word-limit
                size="large"
              />
            </el-form-item>

            <el-form-item label="关联景点" prop="attractionId">
              <el-select
                v-model="form.attractionId"
                placeholder="请选择关联景点"
                :loading="attractionLoading"
                filterable
                size="large"
                style="width: 100%"
              >
                <el-option
                  v-for="attraction in attractions"
                  :key="attraction.id"
                  :label="attraction.name"
                  :value="attraction.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="行程天数" prop="days">
              <el-input-number
                v-model="form.days"
                :min="1"
                :max="30"
                size="large"
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <el-form-item label="攻略摘要" prop="intro" class="full-width">
            <el-input
              v-model="form.intro"
              type="textarea"
              :rows="3"
              placeholder="请输入攻略摘要，简要描述您的旅行亮点和特色..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </div>

        <!-- 预算信息 -->
        <div class="form-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="section-icon">💰</i>
              预算信息
            </h2>
          </div>
          
          <!-- 预算类型 - 单独一行 -->
          <el-form-item label="预算类型" prop="budgetType" class="full-width">
            <el-radio-group v-model="form.budgetType" class="budget-radio-group">
              <el-radio
                v-for="option in budgetTypeOptions"
                :key="option.value"
                :label="option.value"
                class="budget-radio"
              >
                <div class="radio-content">
                  <span class="radio-icon">{{ option.icon }}</span>
                  <span class="radio-text">{{ option.label }}</span>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 预算和季节 - 并排显示 -->
          <div class="form-grid">
            <el-form-item label="预估预算" prop="estimatedBudget">
              <el-input
                v-model="form.estimatedBudget"
                type="number"
                placeholder="请输入预估预算"
                size="large"
              >
                <template #prepend>¥</template>
                <template #append>元</template>
              </el-input>
            </el-form-item>

            <el-form-item label="适合季节" prop="season">
              <el-select
                v-model="form.season"
                placeholder="请选择适合季节"
                size="large"
                style="width: 100%"
              >
                <el-option
                  v-for="option in seasonOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                >
                  <div class="season-option">
                    <span class="season-icon">{{ option.icon }}</span>
                    <span class="season-text">{{ option.label }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>

        <!-- 封面图片 -->
        <div class="form-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="section-icon">🖼️</i>
              封面图片
            </h2>
          </div>
          
          <el-form-item label="封面图片" class="full-width">
            <div class="upload-container">
              <el-upload
                ref="uploadRef"
                class="cover-uploader"
                :action="uploadUrl"
                :headers="uploadData"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
              >
                <div v-if="imageUrl" class="image-preview">
                  <img :src="imageUrl" class="cover-image" />
                  <div class="image-overlay">
                    <el-button
                      type="danger"
                      :icon="Close"
                      circle
                      @click.stop="handleRemove"
                      class="remove-button"
                    />
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <div class="upload-text">点击上传封面图片</div>
                  <div class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</div>
                </div>
              </el-upload>
            </div>
          </el-form-item>
        </div>

        <!-- 攻略内容 -->
        <div class="form-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="section-icon">📝</i>
              攻略内容
            </h2>
          </div>
          
          <el-form-item label="详细内容" prop="content" class="full-width">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="15"
              placeholder="请详细描述您的旅行攻略，包括：&#10;1. 行程安排&#10;2. 景点介绍&#10;3. 交通方式&#10;4. 住宿推荐&#10;5. 美食推荐&#10;6. 注意事项&#10;7. 实用贴士等..."
              show-word-limit
            />
          </el-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button size="large" @click="resetForm">
            重置表单
          </el-button>
          <el-button size="large" @click="saveDraft">
            保存草稿
          </el-button>
          <el-button 
            type="primary" 
            size="large"
            :icon="Check"
            @click="submitForm"
            :loading="submitting"
          >
            {{ submitting ? "发布中..." : "发布攻略" }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.publish-strategy-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 页面头部 */
.page-header {
  background: var(--gradient-primary);
  color: white;
  padding: 120px 0;
  box-shadow: var(--shadow-medium);

  .header-content {
    max-width: 900px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .back-button {
        background: rgba(255, 255, 255, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: white;
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateY(-1px);
        }
      }

      .title-section {
        .page-title {
          font-size: 1.8rem;
          font-weight: 600;
          margin: 0 0 4px 0;
          display: flex;
          align-items: center;
          gap: 8px;

          .title-icon {
            font-size: 1.6rem;
          }
        }

        .page-subtitle {
          font-size: 0.9rem;
          opacity: 0.85;
          margin: 0;
        }
      }
    }

    .header-actions {
      display: flex;
      gap: 12px;

      .draft-button {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: white;
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
      }

      .publish-button {
        background: var(--secondary-color);
        border: none;
        font-weight: 500;
        
        &:hover {
          background: var(--secondary-600);
          transform: translateY(-1px);
        }
      }
    }
  }
}

/* 表单容器 */
.form-container {
  max-width: 800px;
  margin: -12px auto 0;
  padding: 0 20px 32px;
  position: relative;
  z-index: 2;
}

/* 表单样式 */
.strategy-form {
  background: var(--bg-primary);
  border-radius: 12px;
  padding: 32px;
  box-shadow: var(--shadow-light);
  border: 1px solid var(--border-light);

  .form-section {
    margin-bottom: 32px;
    
    &:last-child {
      margin-bottom: 0;
    }

    .section-header {
      margin-bottom: 20px;
      
      .section-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: var(--text-primary);
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;
        padding-bottom: 8px;
        border-bottom: 1px solid var(--primary-color);

        .section-icon {
          font-size: 1.1rem;
          color: var(--primary-color);
        }
      }
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 16px;
      margin-bottom: 16px;
    }

    .full-width {
      grid-column: 1 / -1;
    }
  }
}

/* 预算类型单选组 */
.budget-radio-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  justify-content: flex-start;

  .budget-radio {
    margin-right: 0;
    
    :deep(.el-radio__input) {
      display: none;
    }
    
    :deep(.el-radio__label) {
      padding-left: 0;
    }

    .radio-content {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      border: 1px solid var(--border-medium);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      background: var(--bg-primary);
      font-size: 0.95rem;
      min-width: 120px;
      justify-content: center;

      .radio-icon {
        font-size: 1.1rem;
      }

      .radio-text {
        font-weight: 500;
      }
    }

    &.is-checked .radio-content {
      border-color: var(--primary-color);
      background: var(--primary-50);
      color: var(--primary-color);
      box-shadow: 0 2px 8px var(--primary-200);
    }

    &:hover .radio-content {
      border-color: var(--primary-light);
      background: var(--primary-50);
      transform: translateY(-1px);
    }
  }
}

/* 季节选项 */
.season-option {
  display: flex;
  align-items: center;
  gap: 6px;

  .season-icon {
    font-size: 1rem;
  }
}

/* 文件上传 */
.upload-container {
  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--border-medium);
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.2s ease;
      width: 240px;
      height: 160px;

      &:hover {
        border-color: var(--primary-color);
      }
    }

    .image-preview {
      position: relative;
      width: 100%;
      height: 100%;

      .cover-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
      }

      .image-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.2s ease;
        border-radius: 8px;

        .remove-button {
          background: rgba(255, 255, 255, 0.9);
          border: none;
        }
      }

      &:hover .image-overlay {
        opacity: 1;
      }
    }

    .upload-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: var(--text-tertiary);

      .upload-icon {
        font-size: 2rem;
        margin-bottom: 8px;
        color: var(--primary-color);
      }

      .upload-text {
        font-size: 0.9rem;
        font-weight: 500;
        margin-bottom: 4px;
      }

      .upload-tip {
        font-size: 0.8rem;
        color: var(--text-light);
      }
    }
  }
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border-light);
}

/* Element Plus 组件样式覆盖 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: all 0.2s ease;
  border: 1px solid var(--border-light);
  
  &:hover {
    border-color: var(--primary-light);
  }
  
  &.is-focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px var(--primary-50);
  }
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
  transition: all 0.2s ease;
  border: 1px solid var(--border-light);
  
  &:hover {
    border-color: var(--primary-light);
  }
  
  &:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px var(--primary-50);
  }
}

:deep(.el-select .el-input__wrapper) {
  &:hover {
    border-color: var(--primary-light);
  }
  
  &.is-focus {
    border-color: var(--primary-color);
  }
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
  
  &.el-button--primary {
    background: var(--primary-color);
    border-color: var(--primary-color);
    
    &:hover {
      background: var(--primary-600);
      border-color: var(--primary-600);
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    .header-content {
      flex-direction: column;
      gap: 16px;
      text-align: center;

      .header-left {
        flex-direction: column;
        gap: 12px;

        .title-section .page-title {
          font-size: 1.6rem;
        }
      }

      .header-actions {
        width: 100%;
        justify-content: center;
      }
    }
  }

  .form-container {
    padding: 0 16px 24px;
  }

  .strategy-form {
    padding: 24px 20px;

    .form-section .form-grid {
      grid-template-columns: 1fr;
    }

    .budget-radio-group {
      flex-direction: column;
    }
  }

  .form-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}

@media (max-width: 480px) {
  .page-header {
    .header-content .header-left .title-section .page-title {
      font-size: 1.4rem;
    }
  }

  .strategy-form {
    padding: 20px 16px;
  }

  .upload-container .cover-uploader :deep(.el-upload) {
    width: 100%;
    height: 140px;
  }
}
</style>