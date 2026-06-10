package com.learnforge.common.utils;

import lombok.Data;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SPELUtils {


    /**
     * Replace expressions in the template with values from the args parameters
     *
     * @param formatter Template
     * @param paraNameArr Parameter names corresponding to the method
     * @param args Parameter values used to match the corresponding expressions
     * @return String after template replacement
     *
     * Example format: tj:#{user.id}
     *      paraNameAddr [user]
     *      args [{"user":{"id":1}}]
     *
     *      Converted result -> tj:1
     */
    public static String parse(String formatter, String[] paraNameArr, Object[] args) {
        if (StringUtils.isNotBlank(formatter) && formatter.indexOf("#") > -1) {
            //Regular expression #{user.id},
            Pattern pattern = Pattern.compile("(\\#\\{([^\\}]*)\\})");
            Matcher matcher = pattern.matcher(formatter);
            //Extract the value of #{} in the regular expression and place it in keys
            List<String> keys = new ArrayList<>();
            while (matcher.find()) {
                keys.add(matcher.group());
            }
            if (!CollUtils.isEmpty(keys)) {
                //SPEL expression object
                ExpressionParser parser = new SpelExpressionParser();
                StandardEvaluationContext context = new StandardEvaluationContext();
                //Map name and value one-to-one
                for (int i = 0; i < paraNameArr.length; i++) {
                    context.setVariable(paraNameArr[i], args[i]);
                }

                for (String tmp : keys) {
                    formatter = formatter.replace(tmp,
                            //Get the corresponding value through SPEL expression and replace the original value
                            parser.parseExpression("#" + tmp.substring(2, tmp.length() - 1)).getValue(context, String.class));
                }
                return formatter;
            }
        }
        return null;
    }

    @Data
    public static class User {
        private Long id = 1L;
    }
    public static void main(String[] args) {

        Object [] users = new Object[1];
        users[0] = new User();

        System.out.println(parse("tj:#{user.id}", new String[]{"user"}, users));
    }
}
