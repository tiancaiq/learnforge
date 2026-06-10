package com.learnforge.learning.mq;


import com.learnforge.api.dto.trade.OrderBasicDTO;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class LessonChangeListener {
    
    private final ILearningLessonService lessonService;

    @RabbitListener(bindings =  @QueueBinding(
            value = @Queue(value = "learning.lesson.pay.queue", durable = "true"),
            exchange = @Exchange(name =MqConstants.Exchange.ORDER_EXCHANGE,type = ExchangeTypes.TOPIC),
            key =  MqConstants.Key.ORDER_PAY_KEY
    ))
    public void listenLessonPay(OrderBasicDTO order){
        if (order ==null || order.getUserId() ==null || CollUtils.isEmpty(order.getCourseIds())){
            
            log.error("order is empty, mq");
            return;
            
        }

        // add lesson

        log.debug("lisend user {} order {} need to get in lesson");
        lessonService.addUserLessons(order.getUserId(), order.getCourseIds());
    }
}
