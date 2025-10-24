package pe.unsa.mcp.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcp")
public class McpToolsController {

  private final ApplicationContext applicationContext;

  public McpToolsController(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @GetMapping("/tools")
  public ResponseEntity<ToolsResponse> listTools() {
    Map<String, Object> allBeans = applicationContext.getBeansOfType(Object.class);

    List<ToolInfo> tools = new ArrayList<>();

    for (Map.Entry<String, Object> entry : allBeans.entrySet()) {
      Object bean = entry.getValue();
      Class<?> targetClass = AopUtils.getTargetClass(bean);
      if (targetClass == null) {
        targetClass = bean.getClass();
      }

      // Limit scan to our code packages
      Package pkg = targetClass.getPackage();
      if (pkg == null || !pkg.getName().startsWith("pe.unsa.")) {
        continue;
      }

      for (Method method : targetClass.getDeclaredMethods()) {
        Tool toolAnn = method.getAnnotation(Tool.class);
        if (toolAnn != null) {
          String name = toolAnn.name().isEmpty() ? method.getName() : toolAnn.name();
          String description = toolAnn.description();
          tools.add(new ToolInfo(name, description == null || description.isBlank() ? "No description available" : description));
        }
      }
    }

    // Ensure stable ordering
    List<ToolInfo> sorted = tools.stream()
        .distinct()
        .sorted((a, b) -> a.name.compareToIgnoreCase(b.name))
        .collect(Collectors.toList());

    return ResponseEntity.ok(new ToolsResponse(sorted, sorted.size()));
  }

  public record ToolInfo(String name, String description) {}

  public record ToolsResponse(List<ToolInfo> tools, int count) {}
}
