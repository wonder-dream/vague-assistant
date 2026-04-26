import { createRouter, createWebHistory } from 'vue-router'
import ChatView from '../views/ChatView.vue'
import KbView from '../views/KbView.vue'
import AgentView from '../views/AgentView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: ChatView },
    { path: '/kb', component: KbView },
    { path: '/agent', component: AgentView },
  ],
})
