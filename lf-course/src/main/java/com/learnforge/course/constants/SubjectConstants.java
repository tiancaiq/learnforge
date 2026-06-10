package com.learnforge.course.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wusongsong
 * @since 2022/7/17 13:07
 * @version 1.0.0
 **/
public class SubjectConstants {

    @AllArgsConstructor
    @Getter
    public enum Type {
        SIGNLE_CHOICE(1, "Single choice question"),
        MUtiple_CHOICE(2, "Multiple choice question"),
        NON_DIRECTIONAL_CHOICE(3, "Indefinite choice question"),
        JUDGEMENT_QUESTION(4, "Judgment question");
        private Integer type;
        private String desc;

        public static String desc(Integer subjectType) {
            for (Type type : values()) {
                if (type.type == subjectType) {
                    return type.desc;
                }
            }
            return null;
        }

    }

    @AllArgsConstructor
    @Getter
    public enum Difficult {
        EASY(1, "Simple"), MEDIUM(2, "Medium"), DIFFICULT(3, "Difficult");
        private Integer type;
        private String desc;

        public static String desc(Integer type) {
            for (Difficult difficult : values()) {
                if (difficult.getType() == type) {
                    return difficult.desc;
                }
            }
            return null;
        }
    }
}
