package com.terry.backend.core.ibatis.helper;

import com.terry.backend.core.dto.BaseDTO;
import com.terry.backend.core.security.util.SessionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class SessionUserInfoHelper implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        tryInjectSessionUserInfo(invocation.getArgs());
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    private void tryInjectSessionUserInfo(Object[] args) {
        Object arg = args[1];
        if (isBaseDTO(arg)) {
            return;
        }
        if (isMap(arg)) {
        }
    }

    private boolean isBaseDTO(Object arg) {
        try {
            if (arg instanceof BaseDTO) {
                BaseDTO param = (BaseDTO) arg;
                if (StringUtils.isEmpty(param.getCreateId())) {
                    param.setCreateId(SessionUtils.getUsername());
                }
                if (StringUtils.isEmpty(param.getUpdateId())) {
                    param.setUpdateId(SessionUtils.getUsername());
                }
                return true;
            }
        } catch (Exception e) {
            // Do nothing
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean isMap(Object arg) {
        try {
            if (arg instanceof Map) {
                Map<Object, Object> param = (Map<Object, Object>) arg;
                for (Object key : param.keySet()) {
                    if (key instanceof String &&
                            ("createId".contentEquals(key.toString()) ||
                                    "updateId".contentEquals(key.toString()))) {
                        if (StringUtils.isEmpty(param.get(key))) {
                            param.put(key, SessionUtils.getUsername());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Do nothing
        }
        return false;
    }

}
