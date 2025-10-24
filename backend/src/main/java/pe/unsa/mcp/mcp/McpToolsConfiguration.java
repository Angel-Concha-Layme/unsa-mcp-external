package pe.unsa.mcp.mcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import pe.unsa.mcp.services.McpToolsService;

@Configuration
public class McpToolsConfiguration {

    private final McpToolsService mcpToolsService;

    public McpToolsConfiguration(McpToolsService mcpToolsService) {
        this.mcpToolsService = mcpToolsService;
    }

    @Bean
    public ToolCallbackProvider toolCallbackProvider() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpToolsService)
                .build();
    }
}

