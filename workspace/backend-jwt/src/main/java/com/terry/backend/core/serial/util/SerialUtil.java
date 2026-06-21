package com.terry.backend.core.serial.util;

import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.service.CoreSerialService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@SuppressWarnings("unchecked")
public class SerialUtil {


    @Autowired
    private CoreSerialService coreSerialService;

    private static SerialUtil instance = null;

    public static <T> List<T> getList(String id, Integer value, SerialConfiguration<T> configuration) throws Exception {
        Assert.notNull(instance, "instance cannot be null");
        return (List<T>) instance.getService().getSerial(id, value, configuration);
    }

    public static <T> List<T> getList(String id, Integer value) throws Exception {
        Assert.notNull(instance, "instance cannot be null");
        return (List<T>) instance.getService().getSerial(id, value, null);
    }

    public static <T> T get(String id, SerialConfiguration<T> configuration) throws Exception {
        List<T> serials = getList(id, 1, configuration);
        return serials.get(0);
    }

    public static <T> T get(String id) throws Exception {
        List<T> serials = getList(id, 1);
        return serials.get(0);
    }

    protected CoreSerialService getService() {
        Assert.notNull(coreSerialService, "coreSerialService cannot be null");
        return coreSerialService;
    }

    @PostConstruct
    public void postConstruct() {
        if (SerialUtil.instance == null) {
            SerialUtil.instance = this;
        }
    }

}
