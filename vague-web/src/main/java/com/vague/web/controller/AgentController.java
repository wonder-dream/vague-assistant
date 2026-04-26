package com.vague.web.controller;

import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.common.result.Result;
import com.vague.core.chat.entity.Agent;
import com.vague.core.chat.entity.dto.AgentCreateRequest;
import com.vague.core.chat.entity.dto.SwitchAgentDTO;
import com.vague.core.chat.entity.vo.AgentVO;
import com.vague.core.chat.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public Result<String> create(@RequestBody AgentCreateRequest req) {
        Agent agent = new Agent();
        BeanUtils.copyProperties(req, agent);
        agentService.save(agent);
        return Result.ok(agent.getId());
    }

    @GetMapping("/list")
    public Result<List<AgentVO>> list() {
        List<AgentVO> list = agentService.list().stream().map(agent -> {
            AgentVO vo = new AgentVO();
            BeanUtils.copyProperties(agent, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<AgentVO> getById(@PathVariable String id) {
        Agent agent = agentService.getById(id);
        if (agent == null) {
            // 统一走 BusinessException，由 GlobalExceptionHandler 转成 Result.error
            throw new BusinessException(404, "Agent 不存在");
        }
        AgentVO vo = new AgentVO();
        BeanUtils.copyProperties(agent, vo);
        return Result.ok(vo);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody AgentCreateRequest req) {
        Agent agent = agentService.getById(id);
        if (agent == null) {
            throw new BusinessException(404, "Agent 不存在");
        }
        BeanUtils.copyProperties(req, agent);
        agent.setId(id);  // copyProperties 会把 id 也覆盖成 null，这里改回来
        agentService.updateById(agent);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        agentService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 切换会话绑定的 Agent
     */
    @PostMapping("/switch")
    public Result<Void> switchAgent(@RequestBody SwitchAgentDTO switchAgentDTO) {
        String sessionId = switchAgentDTO.getSessionId();
        String agentId = switchAgentDTO.getAgentId();
        agentService.switchSessionAgent(sessionId, agentId);
        return Result.ok(null);
    }
}