package com.example.hrmapplication.aspect;

import com.example.hrmapplication.service.AuditLogService;
import com.example.hrmapplication.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @AfterReturning("@annotation(com.example.hrmapplication.annotation.Audit)")
    public void audit(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String userId = SecurityUtil.getCurrentUserId();
            String username = SecurityUtil.getCurrentUsername();
            String action = joinPoint.getSignature().getName();
            String entityType = joinPoint.getTarget().getClass().getSimpleName();
            
            auditLogService.log(userId, username, action, entityType, null, 
                    "Thực hiện: " + action + " trên " + entityType, request);
        } catch (Exception e) {
            // Log silently to avoid breaking the main flow
        }
    }
}

