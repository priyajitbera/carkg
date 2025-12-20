package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Chat {
  List<ChatItem> chatItems;
}
