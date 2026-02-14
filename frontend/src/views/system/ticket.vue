<script setup>
import http from "@/utils/http";
import { Plus, Search, Refresh, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import useUserStore from "@/stores/userStore";
import { getImageUrl, hasPermission } from "@/utils/system";
import Config from "@/config/index";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  name: "",
  attractionId: null,
  type: null,
};
// 搜索条件
const params = ref({ ...initParams });
// 初始参数
const initForm = {
  attractionId: null,
  coverImage: "",
  name: "",
  type: 1,
  price: 0,
  originalPrice: 0,
  description: "",
  validityPeriod: "",
  purchaseNotes: "",
  inventory: 0,
};
// 表单数据
const form = ref({ ...initForm });
// 表格数据
const tableData = ref([]);
// 表格数据总数
const total = ref(0);
// 控制弹窗显示
const visible = ref(false);
// 景点列表
const attractionList = ref([]);
// 文件上传地址
const uploadUrl = ref(`${Config.baseUrl}/file/upload`);
const userStore = useUserStore();
// 文件上传携带的数据（后台会话）
const uploadData = computed(() => ({
  authorization: userStore.adminToken,
}));
// 图片上传后的回调地址
const imageUrl = ref(
  form.value?.coverImage ? `${Config.baseUrl}${form.value?.coverImage}` : ""
);


// 批量删除相关
const multipleSelection = ref([]);
const selectedIds = computed(() => {
  return multipleSelection.value.map((item) => item.id);
});
const hasSelected = computed(() => {
  return multipleSelection.value.length > 0;
});

const isEdit = computed(() => {
  return form.value.id > 0;
});
const dialogTitle = computed(() => {
  return isEdit.value ? "编辑" : "新增";
});

// 票种类型字典
const typeDict = {
  1: "成人票",
  2: "儿童票", 
  3: "学生票",
  4: "老人票"
};

// 获取景点列表
const getAttractionList = async () => {
  const res = await http.get("/attraction/list");
  if (res.code === 200) {
    attractionList.value = res.data;
  }
};

// 获取表格数据
const getTableData = async () => {
  let url = `/ticket/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}`;
  if (params.value.name) {
    url += `&name=${params.value.name}`;
  }
  if (params.value.attractionId) {
    url += `&attractionId=${params.value.attractionId}`;
  }
  if (params.value.type) {
    url += `&type=${params.value.type}`;
  }
  
  const res = await http.get(url);
  if (res.code === 200) {
    tableData.value = res.data.records;
    total.value = res.data.total;
  }
};

// 搜索
const handleSearch = async () => {
  await getTableData();
};

// 重置
const handleReset = () => {
  params.value = { ...initParams };
  getTableData();
};

// 新增
const handleAdd = () => {
  form.value = { ...initForm };
  visible.value = true;
};

// 编辑
const handleEdit = (row) => {
  imageUrl.value = row.coverImage ? getImageUrl(row.coverImage) : "";
  form.value = { ...row };
  visible.value = true;
};

// 删除
const delRow = async (id) => {
  const res = await http.get(`/ticket/del?id=${id}`);
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
    ElMessage.warning("请先选择要删除的门票");
    return;
  }
  const res = await http.post("/ticket/batchDel", selectedIds.value);
  if (res.code === 200) {
    ElMessage.success("批量删除成功");
    multipleSelection.value = [];
    await getTableData();
  } else {
    ElMessage.error("批量删除失败");
  }
};

// 多选
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
};

// 确认按钮
const submit = async () => {
  if (isEdit.value) {
    const res = await http.post(`/ticket/edit`, form.value);
    if (res.code === 200) {
      ElMessage.success("编辑成功");
      visible.value = false;
      await getTableData();
    } else {
      ElMessage.error("编辑失败");
    }
  } else {
    const res = await http.post(`/ticket/add`, form.value);
    if (res.code === 200) {
      ElMessage.success("新增成功");
      visible.value = false;
      await getTableData();
    } else {
      ElMessage.error("新增失败");
    }
  }
};

/** 文件上传前的钩子函数 */
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

/** 文件 上传成功回调 */
const handleAvatarSuccess = (response) => {
  form.value.coverImage = response.data;
  imageUrl.value = `${Config.baseUrl}${response.data}`;
};

// 处理关闭弹窗
const handleClose = () => {
  visible.value = false;
};

const handleSizeChange = (val) => {
  params.value.pageSize = val;
  getTableData();
};
const handleCurrentChange = (val) => {
  params.value.pageNum = val;
  getTableData();
};

onMounted(async () => {
  await getAttractionList();
  await getTableData();
});
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">门票名称：</p>
        <el-input v-model="params.name" placeholder="请输入门票名称" />
      </div>
      <div class="flex form-item">
        <p class="label-text">关联景点：</p>
        <el-select v-model="params.attractionId" placeholder="请选择景点" clearable style="width: 200px;">
          <el-option
            v-for="item in attractionList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>
      <div class="flex form-item">
        <p class="label-text">票种类型：</p>
        <el-select v-model="params.type" placeholder="请选择票种类型" clearable style="width: 200px;">
          <el-option
            v-for="(label, value) in typeDict"
            :key="value"
            :label="label"
            :value="parseInt(value)"
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
          v-if="hasPermission('ticket', '新增')"
          :icon="Plus"
          type="primary"
          @click="handleAdd"
          >新增</el-button
        >
        <el-button
          v-if="hasPermission('ticket', '删除')"
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
        <el-table-column prop="name" label="门票名称" width="200" show-overflow-tooltip />
                <el-table-column prop="coverImage" label="封面图片" width="120">
          <template #default="scope">
            <el-image
              :src="getImageUrl(scope.row.coverImage)"
              :preview-src-list="[getImageUrl(scope.row.coverImage)]"
              :preview-teleported="true"
              :hide-on-click-modal="true"
              :lazy="true"
              :initial-index="0"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon>
                    <icon-picture />
                  </el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="attractionName" label="关联景点" width="150" />
        <el-table-column prop="typeName" label="票种类型" width="100" />
        <el-table-column prop="price" label="现价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="scope">
            ¥{{ scope.row.originalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="门票描述" show-overflow-tooltip />
        <el-table-column prop="validityPeriod" label="有效期" width="120" />
        <el-table-column prop="inventory" label="库存" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              v-if="hasPermission('ticket', '编辑')"
              type="primary"
              :icon="Edit"
              @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-popconfirm
              v-if="hasPermission('ticket', '删除')"
              title="您确定要删除吗?"
              @confirm="delRow(scope.row.id)"
            >
              <template #reference>
                <el-button type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
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
    <el-dialog
      v-model="visible"
      :title="dialogTitle"
      width="800"
      :before-close="handleClose"
    >
      <el-form :model="form" label-width="120">
        <el-form-item label="门票名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入门票名称" />
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
        
        <el-form-item label="票种类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">成人票</el-radio>
            <el-radio :value="2">儿童票</el-radio>
            <el-radio :value="3">学生票</el-radio>
            <el-radio :value="4">老人票</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="现价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        
        <el-form-item label="门票描述" prop="description">
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            v-model="form.description"
            placeholder="请输入门票描述"
          />
        </el-form-item>
        
        <el-form-item label="有效期" prop="validityPeriod">
          <el-input v-model="form.validityPeriod" placeholder="请输入有效期，如：当日有效、3天内有效等" />
        </el-form-item>
        
        <el-form-item label="购买须知" prop="purchaseNotes">
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            v-model="form.purchaseNotes"
            placeholder="请输入购买须知"
          />
        </el-form-item>
        
        <el-form-item label="库存数量" prop="inventory">
          <el-input-number v-model="form.inventory" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit()"> 确认 </el-button>
          <el-button @click="visible = false">取消</el-button>
        </div>
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

.avatar-uploader {
  width: 178px;
  height: 178px;
  overflow: hidden;
  border: 1px dashed var(--el-border-color);

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
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  --el-table-header-bg-color: var(--el-fill-color-light);
}
</style>