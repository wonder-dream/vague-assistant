import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 60000,
})

http.interceptors.response.use(
  res => res.data,
  err => Promise.reject(err)
)

// KB
export const kbApi = {
  list: () => http.get('/kb/list'),
  create: (data) => http.post('/kb', data),
  update: (id, data) => http.put(`/kb/${id}`, data),
  remove: (id) => http.delete(`/kb/${id}`),
}

// Document
export const docApi = {
  list: (kbId) => http.get('/document/list', { params: { kbId } }),
  upload: (file, kbId) => {
    const form = new FormData()
    form.append('file', file)
    form.append('kbId', kbId)
    return http.post('/document/upload', form)
  },
  status: (id) => http.get(`/document/${id}/status`),
  remove: (id) => http.delete(`/document/${id}`),
}

// Agent
export const agentApi = {
  list: () => http.get('/agent/list'),
  create: (data) => http.post('/agent', data),
  update: (id, data) => http.put(`/agent/${id}`, data),
  remove: (id) => http.delete(`/agent/${id}`),
}

// Session
export const sessionApi = {
  list: () => http.get('/session/list'),
  messages: (id) => http.get(`/session/${id}/messages`),
  rename: (id, title) => http.put(`/session/${id}`, { title }),
  remove: (id) => http.delete(`/session/${id}`),
}

// Chat
export const chatApi = {
  talk: (data) => http.post('/chat/talk', data),
}
