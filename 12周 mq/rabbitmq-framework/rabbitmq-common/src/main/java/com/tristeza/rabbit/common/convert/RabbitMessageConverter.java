package com.tristeza.rabbit.common.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author chaodong.xi
 * @date 2020/7/6 8:18 下午
 */
public class RabbitMessageConverter implements MessageConverter {
    private GenericMessageConverter converter;

    public RabbitMessageConverter(GenericMessageConverter converter) {
        this.converter = converter;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        com.tristeza.rabbit.api.model.Message message = (com.tristeza.rabbit.api.model.Message) object;
        messageProperties.setExpiration(String.valueOf(message.getDelayMills()));
        return this.converter.toMessage(object, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.tristeza.rabbit.api.model.Message msg = (com.tristeza.rabbit.api.model.Message) this.converter.fromMessage(message);
        return msg;
    }
}
