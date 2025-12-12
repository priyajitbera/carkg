package com.github.priyajitbera.carkg.service.mcp.agent.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Chat {
    List<ChatItem> chatItems;
}
