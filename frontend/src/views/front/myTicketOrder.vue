<script setup>
import http from "@/utils/http";
import { Search, Refresh, Delete, View, Check, Close } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "@/utils/system";
import useUserStore from "../../stores/userStore";

const userStore = useUserStore();
const currentUser = computed(() => userStore.frontUserInfo || {});

const initParams = {
  pageNum: 1,
  pageSize: 10,
  orderNo: "",
  status: "",
  userId: "", 
};

// 搜索条件
const params = ref({ ...initParams });

// 表格数据
const tableData = ref([]);

// 表格数据总数
const total = ref(0);

// 订单详情弹窗相关
const detailDialogVisible = ref(false);
const orderDetail = ref({});

// 批量删除相关
const multipleSelection = ref([]);
const selectedIds = computed(() => {
  return multipleSelection.value.map((item) => item.id);
});
const hasSelected = computed(() => {
  return multipleSelection.value.length > 0;
});
// 可删除的选中项（状态不为1的订单）
const deletableSelection = computed(() => {
  return multipleSelection.value.filter((item) => item.status !== 1);
});
const hasDeletableSelected = computed(() => {
  return deletableSelection.value.length > 0;
});

// 订单状态字典
const statusOptions = [
  { label: "已付款", value: 1 },
  { label: "已完成", value: 2 },
  { label: "已取消", value: 3 },
];

// 获取表格数据
const getTableData = async () => {
  const queryParams = new URLSearchParams();
  queryParams.append("pageNum", params.value.pageNum.toString());
  queryParams.append("pageSize", params.value.pageSize.toString());
  // 添加用户ID（前台会话）
  if (currentUser.value?.id) {
    queryParams.append("userId", currentUser.value.id.toString());
  }
  
  if (params.value.orderNo) queryParams.append("orderNo", params.value.orderNo);
  if (params.value.status) queryParams.append("status", params.value.status.toString());
  if (params.value.userId) queryParams.append("userId", params.value.userId.toString());

  const res = await http.get(`/ticketOrder/page?${queryParams.toString()}`);
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
const handleView = async (row) => {
   orderDetail.value = {...row};
  detailDialogVisible.value = true;
};

// 删除
const delRow = async (id) => {
  const res = await http.get(`/ticketOrder/del?id=${id}`);
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
  
  // 检查是否有状态为1的订单
  const paidOrders = multipleSelection.value.filter((item) => item.status === 1);
  if (paidOrders.length > 0) {
    ElMessage.warning("已付款的订单不能删除，请先取消或完成订单");
    return;
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${deletableSelection.value.length} 个订单吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    const deletableIds = deletableSelection.value.map((item) => item.id);
    const res = await http.post("/ticketOrder/batchDel", deletableIds);
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

// 更新订单状态
const updateStatus = async (row, status) => {
  const res = await http.post(`/ticketOrder/edit?id=${row.id}`, {
    ...row,
    status
  });
  if (res.code === 200) {
    ElMessage.success("状态更新成功");
    await getTableData();
  } else {
    ElMessage.error("状态更新失败");
  }
};

// 确认完成订单
const confirmComplete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要将订单 ${row.orderNo} 标记为已完成吗？`,
      '确认完成',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
      }
    );
    await updateStatus(row, 2);
  } catch {
    // 用户取消操作
  }
};

// 确认取消订单
const confirmCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 ${row.orderNo} 吗？取消后不可恢复。`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    await updateStatus(row, 3);
  } catch {
    // 用户取消操作
  }
};

// 确认删除订单
const confirmDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除订单 ${row.orderNo} 吗？删除后不可恢复。`,
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

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 1:
      return "success";
    case 2:
      return "info";
    case 3:
      return "danger";
    default:
      return "";
  }
};

onMounted(async () => {
  await getTableData();
});
</script>

<template>
  <div class="my-ticket-order-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="title-icon el-icon-tickets"></i>
          我的订单
          <i class="title-icon el-icon-document"></i>
        </h1>
        <p class="page-subtitle">
          <i class="subtitle-icon el-icon-star-filled"></i>
          管理您的门票订单，查看订单状态和详情
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
              订单号：
            </span>
            <el-input v-model="params.orderNo" placeholder="请输入订单号" />
          </div>
          <div class="flex form-item">
            <span class="filter-label">
              <i class="el-icon-collection-tag"></i>
              订单状态：
            </span>
            <el-select v-model="params.status" placeholder="请选择订单状态" clearable style="width: 200px;">
              <el-option
                v-for="item in statusOptions"
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
          :icon="Delete"
          type="danger"
          :disabled="!hasDeletableSelected"
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
        <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="ticketName" label="门票名称" width="150" show-overflow-tooltip />
        <el-table-column prop="attractionName" label="景点名称" width="150" show-overflow-tooltip />
        <el-table-column prop="typeName" label="票种类型" width="100" />
        <el-table-column prop="payNum" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="scope">
            {{ formatAmount(scope.row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="cardNo" label="身份证号" width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="scope">
            <el-button
              type="primary"
              :icon="View"
              size="small"
              @click="handleView(scope.row)"
              >查看</el-button
            >
            <!-- 已付款状态(1)：显示完成和取消按钮 -->
            <template v-if="scope.row.status === 1">
              <el-button
                type="success"
                :icon="Check"
                size="small"
                @click="confirmComplete(scope.row)"
                >完成</el-button
              >
              <el-button
                type="warning"
                :icon="Close"
                size="small"
                @click="confirmCancel(scope.row)"
                >取消</el-button
              >
            </template>
            <!-- 已完成(2)或已取消(3)状态：显示删除按钮 -->
            <el-button
              v-if="(scope.row.status === 2 || scope.row.status === 3)"
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

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="order-detail">
        <div class="detail-section">
          <h3>订单信息</h3>
          <div class="detail-row">
            <span class="label">订单号：</span>
            <span class="value">{{ orderDetail.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusTagType(orderDetail.status)">
              {{ orderDetail.statusName }}
            </el-tag>
          </div>
          <div class="detail-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ orderDetail.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="label">总金额：</span>
            <span class="value amount">{{ formatAmount(orderDetail.totalAmount) }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h3>门票信息</h3>
          <div class="detail-row">
            <span class="label">门票名称：</span>
            <span class="value">{{ orderDetail.ticketName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">景点名称：</span>
            <span class="value">{{ orderDetail.attractionName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">票种类型：</span>
            <span class="value">{{ orderDetail.typeName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">购买数量：</span>
            <span class="value">{{ orderDetail.payNum }}</span>
          </div>
        </div>


        <div class="detail-section">
          <h3>联系信息</h3>
          <div class="detail-row">
            <span class="label">联系人：</span>
            <span class="value">{{ orderDetail.contactName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">联系电话：</span>
            <span class="value">{{ orderDetail.contactPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="label">身份证号：</span>
            <span class="value">{{ orderDetail.cardNo }}</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.my-ticket-order-page {
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
        width: 120px;
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

/* 订单详情弹窗样式 */
.order-detail {
  .detail-section {
    margin-bottom: 24px;
    
    h3 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      border-bottom: 2px solid #409eff;
      padding-bottom: 8px;
    }
    
    .detail-row {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
      
      .label {
        width: 100px;
        color: #606266;
        font-weight: 500;
        flex-shrink: 0;
      }
      
      .value {
        color: #303133;
        flex: 1;
        
        &.amount {
          font-weight: 600;
          color: #e6a23c;
          font-size: 16px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .my-ticket-order-page {
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
}

@media (max-width: 480px) {
  .my-ticket-order-page {
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
}
</style>