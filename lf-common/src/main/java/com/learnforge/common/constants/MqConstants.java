package com.learnforge.common.constants;

public interface MqConstants {
    interface Exchange{
        /*Course related exchange*/
        String COURSE_EXCHANGE = "course.topic";

        /*Order related exchange*/
        String ORDER_EXCHANGE = "order.topic";

        /*Switch related to learning*/
        String LEARNING_EXCHANGE = "learning.topic";

        /*Switch related to SMS in information center*/
        String SMS_EXCHANGE = "sms.direct";

        /*Switch for abnormal information*/
        String ERROR_EXCHANGE = "error.topic";

        /*Switch related to payment*/
        String PAY_EXCHANGE = "pay.topic";
        /*Switch for transaction service delay task*/
        String TRADE_DELAY_EXCHANGE = "trade.delay.topic";

         /*Switch related to like records*/
        String LIKE_RECORD_EXCHANGE = "like.record.topic";

        String PROMOTION_EXCHANGE = "promotion.topic";
    }
    interface Queue {
        String ERROR_QUEUE_TEMPLATE = "error.{}.queue";
    }
    interface Key{
        /*RoutingKey related to course*/
        String COURSE_NEW_KEY = "course.new";
        String COURSE_UP_KEY = "course.up";
        String COURSE_DOWN_KEY = "course.down";
        String COURSE_EXPIRE_KEY = "course.expire";
        String COURSE_DELETE_KEY = "course.delete";

        /*RoutingKey related to order*/
        String ORDER_PAY_KEY = "order.pay";
        String ORDER_REFUND_KEY = "order.refund";

        /*RoutingKey related to points*/
        /* Write answer */
        String WRITE_REPLY = "reply.new";
        /* Check-in */
        String SIGN_IN = "sign.in";
        /* Learning video */
        String LEARN_SECTION = "section.learned";
        /* Write notes */
        String WRITE_NOTE = "note.new";
        /* Notes collected */
        String NOTE_GATHERED = "note.gathered";

        /*RoutingKey for likes*/
        String LIKED_TIMES_KEY_TEMPLATE = "{}.times.changed";
        /*Q&A*/
        String QA_LIKED_TIMES_KEY = "QA.times.changed";
        /*Notes*/
        String NOTE_LIKED_TIMES_KEY = "NOTE.times.changed";

        /*SMS system sends SMS*/
        String SMS_MESSAGE = "sms.message";

        /*Prefix of RoutingKey for abnormal*/
        String ERROR_KEY_PREFIX = "error.";
        String DEFAULT_ERROR_KEY = "error.#";

        /*Key related to payment*/
        String PAY_SUCCESS = "pay.success";
        String REFUND_CHANGE = "refund.status.change";

        String ORDER_DELAY_KEY = "delay.order.query";


        /*COUPON KEY */
        String COUPON_RECEIVE =  "coupon.receive";
    }
}
