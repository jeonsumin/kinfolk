package com.terry.backend.core.messages.config;

import com.terry.backend.core.messages.util.MessageSourceUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Configuration
public class MessageConfigure {

    @Autowired
    private ListableBeanFactory beanFactory;

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setAlwaysUseMessageFormat(true);
        messageSource.setUseCodeAsDefaultMessage(true);

        messageSource.addBasenames("classpath:messages/messages");
        Collection<MessageSourceConfig> configurations = beanFactory.getBeansOfType(MessageSourceConfig.class).values();
        if (!configurations.isEmpty()) {
            for (MessageSourceConfig config : configurations) {
                if (config != null && StringUtils.hasText(config.getBasename())) {
                    messageSource.addBasenames(config.getBasename());
                }
            }
        }

        /**
         * Set message source to utility
         */
        MessageSourceUtils.setMessageSource(messageSource);

        return messageSource;
    }
}
