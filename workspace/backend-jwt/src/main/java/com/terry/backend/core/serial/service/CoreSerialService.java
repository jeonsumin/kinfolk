package com.terry.backend.core.serial.service;


import com.terry.backend.core.serial.config.CoreSerialConfig;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.dto.SerialDTO;
import com.terry.backend.core.serial.mapper.CoreSerialMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@SuppressWarnings("rawtypes")
public class CoreSerialService {

    @Autowired
    private CoreSerialConfig config;

    @Autowired
    private CoreSerialMapper mapper;

    private static boolean run = false;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Throwable.class, isolation = Isolation.READ_UNCOMMITTED)
    public List<Object> getSerial(String id, Integer value, SerialConfiguration configuration) throws Exception {

        while (CoreSerialService.run) {
            // Wait 100ms
            Thread.sleep(100);
        }

        CoreSerialService.run = true;

        final SerialConfiguration c = configuration == null ? config : configuration;
        Optional<SerialDTO> serial = mapper.findOne(id);
        int start = 1;
        int end = 1;
        SerialDTO entity = null;
        final Date now = new Date();

        if (serial.isPresent()) {
            entity = serial.get();

            if (StringUtils.hasText(c.getCompareDateFormat())) {
                final SimpleDateFormat sdf = new SimpleDateFormat(c.getCompareDateFormat());
                String src = sdf.format(now);
                String tgt = sdf.format(entity.getDatetime());

                if (src.contentEquals(tgt)) {
                    start = entity.getValue();
                    end   = entity.getValue() + value;

                    entity.setValue(entity.getValue() + value);
                } else {
                    entity.setId(id);
                    entity.setValue(value + 1);
                    entity.setDatetime(now);

                    start = 1;
                    end   = entity.getValue();
                }
            } else {
                start = entity.getValue();
                end   = entity.getValue() + value;
                entity.setValue(entity.getValue() + value);
            }

        } else {
            entity = new SerialDTO();
            entity.setId(id);
            entity.setValue(value + 1);
            entity.setDatetime(now);

            start = 1;
            end   = entity.getValue();
        }

        mapper.save(entity);

        List<Object> serials = new ArrayList<>();
        for (int i = start; i < end; i++) {
            serials.add(c.getSerial(i));
        }
        CoreSerialService.run = false;

        return serials;
    }

}
