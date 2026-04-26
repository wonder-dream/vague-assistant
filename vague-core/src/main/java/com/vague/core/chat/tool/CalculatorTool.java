package com.vague.core.chat.tool;

import com.google.gson.Gson;
import com.vague.core.chat.api.ToolDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculatorTool implements ToolExecutor {

    private final Gson gson = new Gson();

    @Override
    public String name() { return "calculator"; }

    @Override
    public ToolDefinition definition() {
        ToolDefinition def = new ToolDefinition();
        ToolDefinition.FunctionDef fn = new ToolDefinition.FunctionDef();
        fn.setName("calculator");
        fn.setDescription("计算数学表达式，支持加减乘除和括号");
        fn.setParameters(Map.of(
            "type", "object",
            "properties", Map.of(
                "expression", Map.of("type", "string", "description", "数学表达式，如 (3+5)*2")
            ),
            "required", new String[]{"expression"}
        ));
        def.setFunction(fn);
        return def;
    }

    @Override
    public String execute(String argumentsJson) {
        try {
            Map<?, ?> args = gson.fromJson(argumentsJson, Map.class);
            String expr = (String) args.get("expression");
            double result = eval(expr.replaceAll("\\s", ""));
            return String.valueOf(result);
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }

    // 简单递归下降解析器，支持 +、-、*、/、括号
    private double eval(String expr) {
        return new Object() {
            int pos = 0;

            double parse() {
                double result = parseTerm();
                while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) {
                    char op = expr.charAt(pos++);
                    result = op == '+' ? result + parseTerm() : result - parseTerm();
                }
                return result;
            }

            double parseTerm() {
                double result = parseFactor();
                while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')) {
                    char op = expr.charAt(pos++);
                    result = op == '*' ? result * parseFactor() : result / parseFactor();
                }
                return result;
            }

            double parseFactor() {
                if (expr.charAt(pos) == '(') {
                    pos++;
                    double result = parse();
                    pos++; // ')'
                    return result;
                }
                int start = pos;
                if (expr.charAt(pos) == '-') pos++;
                while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) pos++;
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }
}
