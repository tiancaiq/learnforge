package com.learnforge.learning.mq;

import com.learnforge.api.dto.remark.LikeTimesDTO;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.learning.domain.po.InteractionReply;
import com.learnforge.learning.service.IInteractionReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.learnforge.common.constants.MqConstants.Exchange.LIKE_RECORD_EXCHANGE;
import static com.learnforge.common.constants.MqConstants.Key.QA_LIKED_TIMES_KEY;
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeTimesChangeListener {

    private final IInteractionReplyService replyService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "qa.liked.times.queue", durable = "true"),
            exchange = @Exchange(name = LIKE_RECORD_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = QA_LIKED_TIMES_KEY
    ))
    public void listenReplyLikedTimesChange(List<LikeTimesDTO> likeTimesDTO){
        log.debug("listened like");


        List<InteractionReply> list = new ArrayList<>(likeTimesDTO.size());
        for (LikeTimesDTO dto : likeTimesDTO) {
            InteractionReply r = new InteractionReply();
            r.setId(dto.getBizId());
            r.setLikedTimes(dto.getLikeTimes());
            list.add(r);
        }

        replyService.updateBatchById(list);


    }


}
