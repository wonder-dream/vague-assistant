<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { sessionApi, agentApi, chatApi } from '../api/index.js'
import { ElMessage } from 'element-plus'

const sessions = ref([])
const agents = ref([])
const currentSession = ref(null)
const messages = ref([])
const input = ref('')
const selectedAgent = ref('')
const loading = ref(false)
const messagesRef = ref(null)

onMounted(async () => {
  const [s, a] = await Promise.all([sessionApi.list(), agentApi.list()])
  sessions.value = s.data || []
  agents.value = a.data || []
})

async function selectSession(session) {
  currentSession.value = session
  const res = await sessionApi.messages(session.id)
  messages.value = res.data || []
  scrollBottom()
}

async function send() {
  const content = input.value.trim()
  if (!content) return
  input.value = ''
  loading.value = true

  // 乐观更新
  messages.value.push({ role: 'user', content })

  try {
    const res = await chatApi.talk({
      sessionId: currentSession.value?.id || null,
      agentId: selectedAgent.value || null,
      content,
    })
    messages.value.push({ role: 'assistant', content: res.data })

    // 如果是新会话，刷新会话列表
    if (!currentSession.value) {
      const s = await sessionApi.list()
      sessions.value = s.data || []
      currentSession.value = sessions.value[0] || null
    }
  } catch {
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
    scrollBottom()
  }
}

async function newSession() {
  currentSession.value = null
  messages.value = []
}

async function deleteSession(session) {
  await sessionApi.remove(session.id)
  sessions.value = sessions.value.filter(s => s.id !== session.id)
  if (currentSession.value?.id === session.id) {
    currentSession.value = null
    messages.value = []
  }
}

function scrollBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}
</script>

<template>
  <el-container style="height:100vh">
    <!-- 会话列表 -->
    <el-aside width="240px" style="border-right:1px solid #eee; display:flex; flex-direction:column">
      <div style="padding:12px">
        <el-button type="primary" style="width:100%" @click="newSession">
          <el-icon><Plus /></el-icon> 新建会话
        </el-button>
      </div>
      <div style="padding:8px 12px">
        <el-select v-model="selectedAgent" placeholder="选择 Agent（可选）" clearable style="width:100%">
          <el-option v-for="a in agents" :key="a.id" :label="a.name" :value="a.id" />
        </el-select>
      </div>
      <el-scrollbar style="flex:1">
        <div
          v-for="s in sessions" :key="s.id"
          class="session-item"
          :class="{ active: currentSession?.id === s.id }"
          @click="selectSession(s)"
        >
          <span style="flex:1; overflow:hidden; text-overflow:ellipsis; white-space:nowrap">{{ s.title || '新会话' }}</span>
          <el-icon class="del-btn" @click.stop="deleteSession(s)"><Delete /></el-icon>
        </div>
      </el-scrollbar>
    </el-aside>

    <!-- 聊天区 -->
    <el-container style="flex-direction:column">
      <el-scrollbar ref="messagesRef" style="flex:1; padding:16px">
        <div v-if="messages.length === 0" style="text-align:center; color:#999; margin-top:80px">
          选择或新建一个会话开始对话
        </div>
        <div v-for="(m, i) in messages" :key="i" class="msg-row" :class="m.role">
          <div class="bubble">{{ m.content }}</div>
        </div>
      </el-scrollbar>

      <div style="padding:12px; border-top:1px solid #eee; display:flex; gap:8px">
        <el-input
          v-model="input"
          placeholder="输入消息，Enter 发送"
          :disabled="loading"
          @keyup.enter="send"
          style="flex:1"
        />
        <el-button type="primary" :loading="loading" @click="send">发送</el-button>
      </div>
    </el-container>
  </el-container>
</template>

<style scoped>
.session-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  cursor: pointer;
  border-radius: 6px;
  margin: 2px 8px;
  font-size: 14px;
}
.session-item:hover, .session-item.active {
  background: #f0f2f5;
}
.del-btn { opacity: 0; color: #999; }
.session-item:hover .del-btn { opacity: 1; }

.msg-row { display: flex; margin-bottom: 12px; }
.msg-row.user { justify-content: flex-end; }
.msg-row.assistant { justify-content: flex-start; }
.bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}
.msg-row.user .bubble { background: #409eff; color: #fff; }
.msg-row.assistant .bubble { background: #f0f2f5; color: #333; }
</style>
