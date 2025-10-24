package pe.unsa.mcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.*;

@RestController
public class McpDebugController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/debug/mcp-routes")
    public Map<String, Object> getMcpRoutes() {
        Map<String, Object> info = new HashMap<>();

        // Retrieve the MCP RouterFunction
        try {
            RouterFunction<ServerResponse> routerFunction =
                (RouterFunction<ServerResponse>) context.getBean("mvcMcpRouterFunction");

            info.put("routerFunctionFound", true);
            info.put("routerFunctionClass", routerFunction.getClass().getName());
            info.put("routerFunctionToString", routerFunction.toString());
        } catch (Exception e) {
            info.put("routerFunctionFound", false);
            info.put("error", e.getMessage());
        }

        // List all registered functions
        Map<String, Object> functionBeans = context.getBeansWithAnnotation(
            org.springframework.context.annotation.Bean.class
        );

        List<String> mcpFunctions = new ArrayList<>();
        context.getBeansOfType(java.util.function.Function.class).forEach((name, bean) -> {
            mcpFunctions.add(name);
        });

        info.put("registeredMcpFunctions", mcpFunctions);

        return info;
    }
}