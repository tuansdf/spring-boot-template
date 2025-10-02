package com.example.sbt.infrastructure.web.config;


import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.dto.RequestContextHolder;
import org.springframework.core.task.TaskDecorator;

public class RequestContextCopyingTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        RequestContext parentCtx = RequestContextHolder.get();

        return () -> {
            try {
                if (parentCtx != null) {
                    RequestContextHolder.set(parentCtx);
                }
                runnable.run();
            } finally {
                RequestContextHolder.clear();
            }
        };
    }
}
