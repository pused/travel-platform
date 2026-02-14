<script setup>
import http from "@/utils/http";
import { Search, Refresh, Delete, View, Check, Close } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { hasPermission } from "@/utils/system";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  orderNo: "",
  contactName: "",
  contactPhone: "",
  status: "",
  userId: "",
  ticketId: "",
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
  
  if (params.value.orderNo) queryParams.append("orderNo", params.value.orderNo);
  if (params.value.contactName) queryParams.append("contactName", params.value.contactName);
  if (params.value.contactPhone) queryParams.append("contactPhone", params.value.contactPhone);
  if (params.value.status) queryParams.append("status", params.value.status.toString());
  if (params.value.userId) queryParams.append("userId", params.value.userId.toString());
  if (params.value.ticketId) queryParams.append("ticketId", params.value.ticketId.toString());

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
  <div>
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">订单号：</p>
        <el-input v-model="params.orderNo" placeholder="请输入订单号" />
      </div>
      <div class="flex form-item">
        <p class="label-text">联系人：</p>
        <el-input v-model="params.contactName" placeholder="请输入联系人姓名" />
      </div>
      <div class="flex form-item">
        <p class="label-text">联系电话：</p>
        <el-input v-model="params.contactPhone" placeholder="请输入联系电话" />
      </div>
      <div class="flex form-item">
        <p class="label-text">订单状态：</p>
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
      <el-button type="warning" :icon="Refresh" @click="handleReset"
        >重置</el-button
      >
    </header>
    <main>
      <div class="flex op-box">
        <el-button
          v-if="hasPermission('ticketOrder', '删除')"
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
        <el-table-column label="操作" fixed="right" width="250">
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
              v-if="(scope.row.status === 2 || scope.row.status === 3) && hasPermission('ticketOrder', '删除')"
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
    </main>

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
          <h3>用户信息</h3>
          <div class="detail-row">
            <span class="label">用户名：</span>
            <span class="value">{{ orderDetail.userName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">用户ID：</span>
            <span class="value">{{ orderDetail.userId }}</span>
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
.label-text {
  color: #606266;
  width: 120px;
}

.form-item {
  margin-right: 20px;
}

.op-box {
  margin: 10px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  --el-table-header-bg-color: var(--el-fill-color-light);
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
</style>