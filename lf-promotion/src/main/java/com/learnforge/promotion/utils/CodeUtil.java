package com.learnforge.promotion.utils;

import com.learnforge.common.constants.RegexConstants;
import com.learnforge.common.exceptions.BadRequestException;

/**


 *   <ul style='padding: 0 15px'>


 *   </ul>

 *     <ol type='a' style='padding: 0 15px'>


 *     </ol>

 * <ol type='a' style='padding: 0 15px'>


 * </ol>
 */
public class CodeUtil {
    /**

     */
    private final static long[] XOR_TABLE = {
            61261925471L, 61261925523L, 58169127203L, 64169927267L,
            64169927199L, 61261925629L, 58169127227L, 64169927363L,
            59169127063L, 64169927359L, 58169127291L, 61261925739L,
            59169127133L, 55139281911L, 56169127077L, 59169127167L
    };
    /**

     */
    private final static int FRESH_BIT_OFFSET = 32;
    /**

     */
    private final static int CHECK_CODE_BIT_OFFSET = 36;
    /**

     */
    private final static int FRESH_MASK = 0xF;
    /**

     */
    private final static int CHECK_CODE_MASK = 0b11111111111111;
    /**

     */
    private final static long PAYLOAD_MASK = 0xFFFFFFFFFL;
    /**

     */
    private final static long SERIAL_NUM_MASK = 0xFFFFFFFFL;
    /**

     */
    private final static int[][] PRIME_TABLE = {
            {23, 59, 241, 61, 607, 67, 977, 1217, 1289, 1601},
            {79, 83, 107, 439, 313, 619, 911, 1049, 1237},
            {173, 211, 499, 673, 823, 941, 1039, 1213, 1429, 1259},
            {31, 293, 311, 349, 431, 577, 757, 883, 1009, 1657},
            {353, 23, 367, 499, 599, 661, 719, 929, 1301, 1511},
            {103, 179, 353, 467, 577, 691, 811, 947, 1153, 1453},
            {213, 439, 257, 313, 571, 619, 743, 829, 983, 1103},
            {31, 151, 241, 349, 607, 677, 769, 823, 967, 1049},
            {61, 83, 109, 137, 151, 521, 701, 827, 1123},
            {23, 61, 199, 223, 479, 647, 739, 811, 947, 1019},
            {31, 109, 311, 467, 613, 743, 821, 881, 1031, 1171},
            {41, 173, 367, 401, 569, 683, 761, 883, 1009, 1181},
            {127, 283, 467, 577, 661, 773, 881, 967, 1097, 1289},
            {59, 137, 257, 347, 439, 547, 641, 839, 977, 1009},
            {61, 199, 313, 421, 613, 739, 827, 941, 1087, 1307},
            {19, 127, 241, 353, 499, 607, 811, 919, 1031, 1301}
    };

    /**

     *


     */
    public static String generateCode(long serialNum, long fresh) {

        fresh = fresh & FRESH_MASK;

        long payload = fresh << FRESH_BIT_OFFSET | serialNum;

        long checkCode = calcCheckCode(payload, (int) fresh);
        System.out.println("checkCode = " + checkCode);

        payload ^= XOR_TABLE[(int) (checkCode & FRESH_MASK)];

        long code = checkCode << CHECK_CODE_BIT_OFFSET | payload;

        return Base32.encode(code);
    }

    private static long calcCheckCode(long payload, int fresh) {

        int[] table = PRIME_TABLE[fresh];

        long sum = 0;
        int index = 0;
        while (payload > 0) {
            sum += (payload & 0xf) * table[index++];
            payload >>>= 4;
        }
        return sum & CHECK_CODE_MASK;
    }

    public static long parseCode(String code) {
        if (code == null || !code.matches(RegexConstants.COUPON_CODE_PATTERN)) {

            throw new BadRequestException("Invalid exchange code");
        }

        long num = Base32.decode(code);

        long payload = num & PAYLOAD_MASK;

        int checkCode = (int) (num >>> CHECK_CODE_BIT_OFFSET);

        payload ^= XOR_TABLE[(checkCode & FRESH_MASK)];

        int fresh = (int) (payload >>> FRESH_BIT_OFFSET & FRESH_MASK);

        if (calcCheckCode(payload, fresh) != checkCode) {
            throw new BadRequestException("Invalid exchange code");
        }
        return payload & SERIAL_NUM_MASK;
    }
}
