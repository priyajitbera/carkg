package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Chat {
    List<ChatItem> chatItems;
}
