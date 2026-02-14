<script setup>
import http from "@/utils/http";
import { Search, Refresh, Delete, View, Edit, Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import useUserStore from "../../stores/userStore";
import { getImageUrl } from "@/utils/system";
import Config from "@/config/index";
import { Picture as IconPicture } from "@element-plus/icons-vue";

const userStore = useUserStore();
const router = useRouter();

const initParams = {
  pageNum: 1,
  pageSize: 10,
  title: "",
  budgetType: "",
};

// 搜索条件
const params = ref({ ...initParams });

// 表格数据
const tableData = ref([]);

// 表格数据总数
const total = ref(0);

// 编辑弹窗相关
const editDialogVisible = ref(false);
const initForm = {
  title: "",
  attractionId: null,
  coverImage: "",
  intro: "",
  content: "",
  days: 1,
  budgetType: 1,
  estimatedBudget: 0,
  season: "",
};
const form = ref({ ...initForm });
const attractionList = ref([]);

// 文件上传地址
const uploadUrl = ref(`${Config.baseUrl}/file/upload`);
// 文件上传携带的数据
const uploadData = {
  authorization: `${localStorage.getItem("token")}`,
};
// 图片上传后的回调地址
const imageUrl = ref("");

const isEdit = computed(() => {
  return form.value.id > 0;
});
const dialogTitle = computed(() => {
  return isEdit.value ? "编辑攻略" : "新增攻略";
});

// 批量删除相关
const multipleSelection = ref([]);
const selectedIds = computed(() => {
  return multipleSelection.value.map((item) => item.id);
});
const hasSelected = computed(() => {
  return multipleSelection.value.length > 0;
});

// 预算类型字典
const budgetTypeOptions = [
  { label: "经济", value: 1 },
  { label: "舒适", value: 2 },
  { label: "豪华", value: 3 },
];

// 获取预算类型名称
const getBudgetTypeName = (type) => {
  const option = budgetTypeOptions.find(item => item.value === type);
  return option ? option.label : "未知";
};

// 获取预算类型标签类型
const getBudgetTypeTagType = (type) => {
  switch (type) {
    case 1:
      return "success";
    case 2:
      return "warning";
    case 3:
      return "danger";
    default:
      return "";
  }
};

// 获取景点列表
const getAttractionList = async () => {
  const res = await http.get("/attraction/list");
  if (res.code === 200) {
    attractionList.value = res.data;
  }
};

// 获取表格数据
const currentUser = computed(() => userStore.frontUserInfo || {});
const isLoggedIn = computed(() => !!userStore.frontToken && !!currentUser.value?.id);

const getTableData = async () => {
  const queryParams = new URLSearchParams();
  queryParams.append("pageNum", params.value.pageNum.toString());
  queryParams.append("pageSize", params.value.pageSize.toString());
  // 添加用户ID，只查询当前用户的攻略
  if (currentUser.value?.id) {
    queryParams.append("userId", currentUser.value.id.toString());
  }
  
  if (params.value.title) queryParams.append("title", params.value.title);
  if (params.value.budgetType) queryParams.append("budgetType", params.value.budgetType.toString());

  const res = await http.get(`/strategy/page?${queryParams.toString()}`);
  if (res.code === 200) {
    tableData.value = res.data.records;
    total.value = res.data.total;
  }
};

// 搜索
const handleSearch = async () => {
  params.value.pageNum = 1;
  await getTableData();
};

// 重置
const handleReset = () => {
  params.value = { ...initParams };
  getTableData();
};

// 查看详情
const handleView = (row) => {
  router.push(`/front/strategy/${row.id}`);
};

// 编辑攻略
const handleEdit = (row) => {
  imageUrl.value = row.coverImage ? getImageUrl(row.coverImage) : "";
  form.value = { ...row };
  editDialogVisible.value = true;
};

// 删除
const delRow = async (id) => {
  const res = await http.get(`/strategy/del?id=${id}`);
  if (res.code === 200) {
    ElMessage.success("删除成功");
    await getTableData();
  } else {
    ElMessage.error("删除失败");
  }
};

// 批量删除
const batchDel = async () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning("请先选择要删除的数据");
    return;
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${multipleSelection.value.length} 个攻略吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    const res = await http.post("/strategy/batchDel", selectedIds.value);
    if (res.code === 200) {
      ElMessage.success("批量删除成功");
      multipleSelection.value = [];
      await getTableData();
    } else {
      ElMessage.error("批量删除失败");
    }
  } catch {
    // 用户取消删除
  }
};

// 多选
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
};

// 确认删除攻略
const confirmDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除攻略 "${row.title}" 吗？删除后不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
    );
    await delRow(row.id);
  } catch {
    // 用户取消操作
  }
};

const handleSizeChange = (val) => {
  params.value.pageSize = val;
  getTableData();
};

const handleCurrentChange = (val) => {
  params.value.pageNum = val;
  getTableData();
};

// 格式化金额显示
const formatAmount = (amount) => {
  return amount ? `¥${amount.toFixed(2)}` : "¥0.00";
};

// 新增攻略
const handleAdd = () => {
  router.push("/front/publishStrategy");
};

// 文件上传前的钩子函数
const beforeAvatarUpload = (rawFile) => {
  if (!rawFile.type.includes("image")) {
    ElMessage.error("文件类型错误，只允许上传图片文件");
    return false;
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error("图片超过2MB限制");
    return false;
  }
  return true;
};

// 文件上传成功回调
const handleAvatarSuccess = (response) => {
  form.value.coverImage = response.data;
  imageUrl.value = `${Config.baseUrl}${response.data}`;
};

// 确认按钮
const submit = async () => {
  if (isEdit.value) {
    const res = await http.post(`/strategy/edit`, form.value);
    if (res.code === 200) {
      ElMessage.success("编辑成功");
      editDialogVisible.value = false;
      await getTableData();
    } else {
      ElMessage.error("编辑失败");
    }
  }
};

// 处理关闭弹窗
const handleClose = () => {
  editDialogVisible.value = false;
};

// 跳转到景点详情
const goToAttractionDetail = (attractionId) => {
  if (attractionId) {
    router.push(`/front/attraction/${attractionId}`);
  }
};

onMounted(async () => {
  await getAttractionList();
  await getTableData();
});
</script>

<template>
  <div class="my-strategy-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="title-icon el-icon-notebook-1"></i>
          我的攻略
          <i class="title-icon el-icon-edit-outline"></i>
        </h1>
        <p class="page-subtitle">
          <i class="subtitle-icon el-icon-star-filled"></i>
          管理您的旅游攻略，分享精彩的旅行经验
          <i class="subtitle-icon el-icon-star-filled"></i>
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
          <div class="flex form-item">
            <span class="filter-label">
              <i class="el-icon-search"></i>
              攻略标题：
            </span>
            <el-input v-model="params.title" placeholder="请输入攻略标题" />
          </div>
          <div class="flex form-item">
            <span class="filter-label">
              <i class="el-icon-money"></i>
              预算类型：
            </span>
            <el-select v-model="params.budgetType" placeholder="请选择预算类型" clearable style="width: 200px;">
              <el-option
                v-for="item in budgetTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          <el-button type="primary" :icon="Search" @click="handleSearch"
            >搜索</el-button
          >
          <el-button type="default" :icon="Refresh" @click="handleReset"
            >重置</el-button
          >
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="container">
      <div class="flex op-box">
        <el-button
          :icon="Plus"
          type="primary"
          @click="handleAdd"
          >新增攻略</el-button
        >
        <el-button
          :icon="Delete"
          type="danger"
          :disabled="!hasSelected"
          @click="batchDel"
          >批量删除</el-button
        >
      </div>
      
      <!-- 表格 -->
      <el-table
        :data="tableData"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="攻略标题" width="200" show-overflow-tooltip />
        <el-table-column prop="attractionName" label="关联景点" width="150" show-overflow-tooltip>
          <template #default="scope">
            <span 
              v-if="scope.row.attractionId"
              class="attraction-link"
              @click="goToAttractionDetail(scope.row.attractionId)"
            >
              {{ scope.row.attractionName }}
            </span>
            <span v-else>{{ scope.row.attractionName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="intro" label="攻略摘要" width="250" show-overflow-tooltip />
        <el-table-column prop="days" label="行程天数" width="100">
          <template #default="scope">
            {{ scope.row.days }}天
          </template>
        </el-table-column>
        <el-table-column prop="budgetType" label="预算类型" width="100">
          <template #default="scope">
            <el-tag :type="getBudgetTypeTagType(scope.row.budgetType)">
              {{ getBudgetTypeName(scope.row.budgetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="estimatedBudget" label="预估预算" width="120">
          <template #default="scope">
            {{ formatAmount(scope.row.estimatedBudget) }}
          </template>
        </el-table-column>
        <el-table-column prop="season" label="适合季节" width="100" />
        <el-table-column prop="viewCount" label="浏览数" width="80" />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="collectCount" label="收藏数" width="80" />
        <el-table-column prop="commentCount" label="评论数" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="scope">
            <el-button
              type="primary"
              :icon="View"
              size="small"
              @click="handleView(scope.row)"
              >查看</el-button
            >
            <el-button
              type="warning"
              :icon="Edit"
              size="small"
              @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button
              type="danger"
              :icon="Delete"
              size="small"
              @click="confirmDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="params.pageNum"
          v-model:page-size="params.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 编辑攻略弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="dialogTitle"
      width="800px"
      :before-close="handleClose"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入攻略标题" />
        </el-form-item>
        
        <el-form-item label="关联景点" prop="attractionId">
          <el-select v-model="form.attractionId" placeholder="请选择关联景点" style="width: 100%">
            <el-option
              v-for="item in attractionList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadData"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <el-image
              v-if="imageUrl"
              :src="imageUrl"
              style="width: 100%; height: 100%"
            />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="攻略摘要" prop="intro">
          <el-input
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
            v-model="form.intro"
            placeholder="请输入攻略摘要"
          />
        </el-form-item>
        
        <el-form-item label="攻略内容" prop="content">
          <el-input
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 8 }"
            v-model="form.content"
            placeholder="请输入攻略详细内容"
          />
        </el-form-item>
        
        <el-form-item label="行程天数" prop="days">
          <el-input-number v-model="form.days" :min="1" :max="30" />
        </el-form-item>
        
        <el-form-item label="预算类型" prop="budgetType">
          <el-radio-group v-model="form.budgetType">
            <el-radio :value="1">经济</el-radio>
            <el-radio :value="2">舒适</el-radio>
            <el-radio :value="3">豪华</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="预估预算" prop="estimatedBudget">
          <el-input-number v-model="form.estimatedBudget" :min="0" :precision="2" />
        </el-form-item>
        
        <el-form-item label="适合季节" prop="season">
          <el-input v-model="form.season" placeholder="请输入适合季节，如：春季、夏季等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit()">确认</el-button>
          <el-button @click="editDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.my-strategy-page {
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
        width: 140px;
      }

      .form-item {
        margin-right: 20px;
      }
    }
  }

  /* 主要内容区域 */
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
    background: var(--bg-primary);
    border-radius: 16px;
    box-shadow: var(--shadow-light);
    margin-top: -20px;
    position: relative;
    z-index: 2;

    .op-box {
      margin-bottom: 20px;
      display: flex;
      justify-content: flex-start;
      gap: 12px;
    }

    .pagination-container {
      margin-top: 30px;
      display: flex;
      justify-content: center;
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

/* 景点链接样式 */
.attraction-link {
  color: var(--primary-color);
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;
  
  &:hover {
    color: var(--primary-600);
    text-decoration: underline;
  }
}

/* 文件上传样式 */
.avatar-uploader {
  width: 178px;
  height: 178px;
  overflow: hidden;
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;

  &:hover {
    border-color: var(--el-color-primary);
  }
}

.avatar {
  width: 100%;
  height: 100%;
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 弹窗样式优化 */
:deep(.el-dialog) {
  border-radius: 16px;
  
  .el-dialog__header {
    background: var(--gradient-primary);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    
    .el-dialog__title {
      font-weight: 600;
      font-size: 18px;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid var(--border-light);
  }
}
@media (max-width: 768px) {
  .my-strategy-page {
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

    .container {
      padding: 20px 15px;
      margin: 0 15px;
      margin-top: -20px;
    }
  }

  .strategy-detail {
    .stats-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 480px) {
  .my-strategy-page {
    .page-header {
      .page-title {
        font-size: 2rem;
      }
    }

    .filter-container {
      padding: 0 15px;
    }

    .container {
      padding: 15px 10px;
      margin: 0 10px;
    }
  }

  .strategy-detail {
    .stats-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>