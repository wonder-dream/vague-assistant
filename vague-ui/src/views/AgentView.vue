<script setup>
import { ref, onMounted } from 'vue'
import { agentApi, kbApi } from '../api/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const agents = ref([])
const kbs = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ name: '', description: '', systemPrompt: '', allowedKbs: [] })

onMounted(async () => {
  const [a, k] = await Promise.all([agentApi.list(), kbApi.list()])
  agents.value = a.data || []
  kbs.value = k.data || []
})

function openCreate() {
  editingId.value = null
  form.value = { name: '', description: '', systemPrompt: '', allowedKbs: [] }
  dialogVisible.value = true
}

function openEdit(agent) {
  editingId.value = agent.id
  let kbIds = []
  try { kbIds = JSON.parse(agent.allowedKbs || '[]') } catch {}
  form.value = {
    name: agent.name,
    description: agent.description,
    systemPrompt: agent.systemPrompt,
    allowedKbs: kbIds,
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.name) return ElMessage.warning('名称不能为空')
  const payload = {
    ...form.value,
    allowedKbs: JSON.stringify(form.value.allowedKbs),
  }
  if (editingId.value) {
    await agentApi.update(editingId.value, payload)
  } else {
    await agentApi.create(payload)
  }
  dialogVisible.value = false
  const res = await agentApi.list()
  agents.value = res.data || []
}

async function remove(agent) {
  await ElMessageBox.confirm(`确认删除 Agent「${agent.name}」？`, '警告', { type: 'warning' })
  await agentApi.remove(agent.id)
  agents.value = agents.value.filter(a => a.id !== agent.id)
}
</script>

<template>
  <div style="padding:24px">
    <div style="display:flex; align-items:center; margin-bottom:16px">
      <span style="font-size:18px; font-weight:bold">Agent 管理</span>
      <el-button type="primary" style="margin-left:auto" @click="openCreate">新建 Agent</el-button>
    </div>

    <el-table :data="agents" border>
      <el-table-column prop="name" label="名称" width="160" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="systemPrompt" label="系统提示词" show-overflow-tooltip />
      <el-table-column label="知识库" width="200">
        <template #default="{ row }">
          <span v-if="!row.allowedKbs || row.allowedKbs === '[]'" style="color:#999">未关联</span>
          <template v-else>
            <el-tag
              v-for="id in JSON.parse(row.allowedKbs || '[]')" :key="id"
              size="small" style="margin-right:4px"
            >{{ kbs.find(k => k.id === id)?.name || id.slice(0, 8) }}</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" text @click="openEdit(row)">编辑</el-button>
          <el-button size="small" text type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogVisible" :title="editingId ? '编辑 Agent' : '新建 Agent'" width="500px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
      <el-form-item label="系统提示词">
        <el-input v-model="form.systemPrompt" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="关联知识库">
        <el-select v-model="form.allowedKbs" multiple placeholder="选择知识库" style="width:100%">
          <el-option v-for="kb in kbs" :key="kb.id" :label="kb.name" :value="kb.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>
