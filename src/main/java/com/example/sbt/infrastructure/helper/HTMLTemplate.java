package com.example.sbt.infrastructure.helper;

public class HTMLTemplate {
    public static String createCenteredHtml(String title, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        {
            builder.append("<head>");
            builder.append("<meta charset=\"utf-8\">");
            builder.append("<title>").append(title).append("</title>");
            builder.append("<style>");
            builder.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
            builder.append("html, body { height: 100%; }");
            builder.append("body {");
            builder.append("font-family: system-ui, sans-serif;");
            builder.append("display: flex;");
            builder.append("flex-direction: column;");
            builder.append("justify-content: center;");
            builder.append("align-items: center;");
            builder.append("}");
            builder.append("</style>");
            builder.append("</head>");
        }
        {
            builder.append("<body>");
            builder.append("<h1>").append(message).append("</h1>");
            builder.append("</body>");
        }
        builder.append("</html>");
        return builder.toString();
    }
}
