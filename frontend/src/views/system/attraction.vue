<script setup>
import http from "@/utils/http";
import { Plus, Search, Refresh, Edit, Delete, View } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import Config from "@/config/index";
import { Picture as IconPicture } from "@element-plus/icons-vue";
import { computed, onMounted, ref } from "vue";
import { getImageUrl, hasPermission } from "@/utils/system";

const initParams = {
  pageNum: 1,
  pageSize: 10,
  name: "",
};
// 搜索条件
const params = ref({ ...initParams });
// 初始参数
const initForm = {
  name: "",
  categoryId: null,
  coverImage: "",
  description: "",
  address: "",
  latitude: null,
  longitude: null,
  contactPhone: "",
  viewCount: 0,
  likeCount: 0,
  collectCount: 0,
  rating: 0,
  isRecommend: 0,
};
// 表单数据
const form = ref({ ...initForm });
// 表格数据
const tableData = ref([]);
// 表格数据总数
const total = ref(0);
// 控制弹窗显示
const visible = ref(false);
// 分类列表
const categoryList = ref([]);

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
// 文件上传地址
const uploadUrl = ref(`${Config.baseUrl}/file/upload`);
// 文件上传携带的数据
const uploadData = {
  authorization: `${localStorage.getItem("token")}`,
};
// 图片上传后的回调地址
const imageUrl = ref(
  form.value?.coverImage ? `${Config.baseUrl}${form.value?.coverImage}` : ""
);

// 获取分类列表
const getCategoryList = async () => {
  const res = await http.get("/category/list");
  if (res.code === 200) {
    categoryList.value = res.data;
  }
};

// 获取表格数据
const getTableData = async () => {
  const res = await http.get(
    `/attraction/page?pageNum=${params.value.pageNum}&pageSize=${params.value.pageSize}&name=${params.value.name}`
  );
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
  imageUrl.value = "";
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
  const res = await http.get(`/attraction/del?id=${id}`);
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
    ElMessage.warning("请先选择要删除的景点");
    return;
  }
  const res = await http.post("/attraction/batchDel", selectedIds.value);
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
    const res = await http.post(`/attraction/edit`, form.value);
    if (res.code === 200) {
      ElMessage.success("编辑成功");
      visible.value = false;
      await getTableData();
    } else {
      ElMessage.error("编辑失败");
    }
  } else {
    const res = await http.post(`/attraction/add`, form.value);
    if (res.code === 200) {
      ElMessage.success("新增成功");
      visible.value = false;
      await getTableData();
    } else {
      ElMessage.error("新增失败");
    }
  }
};

// 处理关闭弹窗
const handleClose = () => {
  visible.value = false;
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

const handleSizeChange = (val) => {
  params.value.pageSize = val;
  getTableData();
};
const handleCurrentChange = (val) => {
  params.value.pageNum = val;
  getTableData();
};

onMounted(async () => {
  await getCategoryList();
  await getTableData();
});
</script>

<template>
  <div>
    <header class="flex">
      <div class="flex form-item">
        <p class="label-text">景点名称：</p>
        <el-input v-model="params.name" placeholder="请输入景点名称" />
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
          v-if="hasPermission('attraction', '新增')"
          :icon="Plus"
          type="primary"
          @click="handleAdd"
          >新增</el-button
        >
        <el-button
          v-if="hasPermission('attraction', '删除')"
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
        <el-table-column prop="name" label="景点名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
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
        <el-table-column
          prop="description"
          label="景点描述"
          show-overflow-tooltip
          width="200"
        />
        <el-table-column prop="address" label="地址" show-overflow-tooltip width="150" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="viewCount" label="浏览数" width="80">
          <template #default="scope">
            <div class="flex items-center">
              <el-icon class="mr-1"><View /></el-icon>
              {{ scope.row.viewCount }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="collectCount" label="收藏数" width="80" />
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column prop="isRecommend" label="推荐" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isRecommend === 1 ? 'success' : 'info'">
              {{ scope.row.isRecommend === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="scope">
            <el-button
              v-if="hasPermission('attraction', '编辑')"
              type="primary"
              :icon="Edit"
              @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-popconfirm
              v-if="hasPermission('attraction', '删除')"
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
      top="2%"
      width="600"
      :before-close="handleClose"
    >
      <el-form :model="form" label-width="120">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入景点名称" />
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categoryList"
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
        
        <el-form-item label="景点描述" prop="description">
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            v-model="form.description"
            placeholder="请输入景点描述"
          />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        
        <el-form-item label="纬度" prop="latitude">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" style="width: 100%" placeholder="请输入纬度" />
        </el-form-item>
        
        <el-form-item label="经度" prop="longitude">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" style="width: 100%" placeholder="请输入经度" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="浏览数" prop="viewCount">
          <el-input-number v-model="form.viewCount" :min="0" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="点赞数" prop="likeCount">
          <el-input-number v-model="form.likeCount" :min="0" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="收藏数" prop="collectCount">
          <el-input-number v-model="form.collectCount" :min="0" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="评分" prop="rating">
          <el-input-number v-model="form.rating" :min="0" :max="5" :precision="1" :step="0.1" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="是否推荐" prop="isRecommend">
          <el-radio-group v-model="form.isRecommend">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
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

:deep(.el-image) {
  width: 55px;
  height: 55px;
}

:deep(.el-table) {
  --el-table-header-bg-color: var(--el-fill-color-light);
}

.flex {
  display: flex;
  align-items: center;
}

.items-center {
  align-items: center;
}

.mr-1 {
  margin-right: 4px;
}
</style>