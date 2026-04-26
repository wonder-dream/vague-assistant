<script setup>
import { ref, onMounted } from 'vue'
import { kbApi, docApi } from '../api/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const kbs = ref([])
const docs = ref([])
const currentKb = ref(null)
const kbDialogVisible = ref(false)
const kbForm = ref({ name: '', description: '' })
const editingKbId = ref(null)
const uploading = ref(false)
const uploadRef = ref(null)

onMounted(loadKbs)

async function loadKbs() {
  const res = await kbApi.list()
  kbs.value = res.data || []
}

async function selectKb(kb) {
  currentKb.value = kb
  const res = await docApi.list(kb.id)
  docs.value = res.data || []
}

function openCreateKb() {
  editingKbId.value = null
  kbForm.value = { name: '', description: '' }
  kbDialogVisible.value = true
}

function openEditKb(kb) {
  editingKbId.value = kb.id
  kbForm.value = { name: kb.name, description: kb.description }
  kbDialogVisible.value = true
}

async function saveKb() {
  if (!kbForm.value.name) return ElMessage.warning('名称不能为空')
  if (editingKbId.value) {
    await kbApi.update(editingKbId.value, kbForm.value)
  } else {
    await kbApi.create(kbForm.value)
  }
  kbDialogVisible.value = false
  loadKbs()
}

async function deleteKb(kb) {
  await ElMessageBox.confirm(`确认删除知识库「${kb.name}」及其所有文档？`, '警告', { type: 'warning' })
  await kbApi.remove(kb.id)
  if (currentKb.value?.id === kb.id) { currentKb.value = null; docs.value = [] }
  loadKbs()
}

async function uploadFile(file) {
  uploading.value = true
  try {
    const res = await docApi.upload(file.raw, currentKb.value.id)
    ElMessage.success(`上传成功，处理中... docId: ${res.data}`)
    setTimeout(() => selectKb(currentKb.value), 2000)
  } catch {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
  return false
}

async function deleteDoc(doc) {
  await ElMessageBox.confirm(`确认删除文档「${doc.filename}」？`, '警告', { type: 'warning' })
  await docApi.remove(doc.id)
  selectKb(currentKb.value)
}
</script>

<template>
  <el-container style="height:100vh">
    <el-aside width="260px" style="border-right:1px solid #eee; display:flex; flex-direction:column">
      <div style="padding:12px; display:flex; gap:8px">
        <span style="font-weight:bold; flex:1">知识库</span>
        <el-button size="small" type="primary" @click="openCreateKb">新建</el-button>
      </div>
      <el-scrollbar style="flex:1">
        <div
          v-for="kb in kbs" :key="kb.id"
          class="kb-item"
          :class="{ active: currentKb?.id === kb.id }"
          @click="selectKb(kb)"
        >
          <span style="flex:1">{{ kb.name }}</span>
          <el-icon @click.stop="openEditKb(kb)"><Edit /></el-icon>
          <el-icon @click.stop="deleteKb(kb)" style="margin-left:6px; color:#f56c6c"><Delete /></el-icon>
        </div>
      </el-scrollbar>
    </el-aside>

    <el-main>
      <div v-if="!currentKb" style="text-align:center; color:#999; margin-top:80px">选择一个知识库</div>
      <template v-else>
        <div style="display:flex; align-items:center; margin-bottom:16px">
          <span style="font-size:16px; font-weight:bold">{{ currentKb.name }}</span>
          <el-upload
            style="margin-left:auto"
            :show-file-list="false"
            :before-upload="() => false"
            :on-change="uploadFile"
            accept=".txt,.md"
          >
            <el-button type="primary" :loading="uploading">上传文档</el-button>
          </el-upload>
        </div>
        <el-table :data="docs" border>
          <el-table-column prop="filename" label="文件名" />
          <el-table-column prop="filetype" label="类型" width="80" />
          <el-table-column prop="size" label="大小" width="100">
            <template #default="{ row }">{{ (row.size / 1024).toFixed(1) }} KB</template>
          </el-table-column>
          <el-table-column prop="metadata" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="JSON.parse(row.metadata || '{}').status === 'done' ? 'success' : JSON.parse(row.metadata || '{}').status === 'failed' ? 'danger' : 'warning'">
                {{ JSON.parse(row.metadata || '{}').status || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button size="small" type="danger" text @click="deleteDoc(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-main>
  </el-container>

  <el-dialog v-model="kbDialogVisible" :title="editingKbId ? '编辑知识库' : '新建知识库'" width="400px">
    <el-form :model="kbForm" label-width="60px">
      <el-form-item label="名称"><el-input v-model="kbForm.name" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="kbForm.description" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="kbDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveKb">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.kb-item {
  display: flex; align-items: center;
  padding: 10px 16px; cursor: pointer;
  border-radius: 6px; margin: 2px 8px; font-size: 14px;
}
.kb-item:hover, .kb-item.active { background: #f0f2f5; }
</style>
