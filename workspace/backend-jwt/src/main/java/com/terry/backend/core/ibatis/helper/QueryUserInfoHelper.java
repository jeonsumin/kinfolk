package com.terry.backend.core.ibatis.helper;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
    MappedStatement.class,
    Object.class,
    RowBounds.class,
    ResultHandler.class
}) })
public class QueryUserInfoHelper implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        tryInjectSessionUserInfo(invocation.getArgs());
        return invocation.proceed();
    }

    private void tryInjectSessionUserInfo(Object[] args) {
        Object arg = args[1];
        if (isBaseSearchParam(arg)) {
            return;
        }
        if (isMap(arg)) {
        }
    }

    private boolean isBaseSearchParam(Object arg) {
        try {
//            if (arg instanceof BaseSearchParam) {
//                BaseSearchParam param = (BaseSearchParam) arg;
//                if (StringUtils.isEmpty(param.getUserId())) {
//                    param.setUserId(SessionUtils.getUsername());
//                }
//                return true;
//            }
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
                    if (key instanceof String && ("userId".contentEquals(key.toString()))) {
                        if (StringUtils.isEmpty(param.get(key))) {
//                            param.put(key, SessionUtils.getUsername());
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
