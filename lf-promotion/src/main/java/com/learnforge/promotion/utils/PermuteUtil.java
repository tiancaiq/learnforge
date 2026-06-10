package com.learnforge.promotion.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**

 */
public class PermuteUtil {
    /**

     *


     */
    public static List<List<Byte>> permute(int n) {
        List<List<Byte>> res = new ArrayList<>();

        List<Byte> input = new ArrayList<>(n);
        for (byte i = 0; i < n; i++) {
            input.add(i);
        }

        backtrack(n, input, res, 0);
        return res;
    }

    /**

     *


     */
    public static <T> List<List<T>> permute(List<T> input) {
        List<List<T>> res = new ArrayList<>();
        backtrack(input.size(), input, res, 0);
        return res;
    }

    /**
     * Generate every non-empty ordered subset.
     */
    public static <T> List<List<T>> permuteSubsets(List<T> input) {
        List<List<T>> result = new ArrayList<>();
        buildSubsets(input, new boolean[input.size()], new ArrayList<>(), result);
        return result;
    }

    private static <T> void buildSubsets(
            List<T> input, boolean[] used, List<T> current, List<List<T>> result) {
        if (!current.isEmpty()) {
            result.add(new ArrayList<>(current));
        }
        for (int i = 0; i < input.size(); i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            current.add(input.get(i));
            buildSubsets(input, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    private static <T> void backtrack(int n, List<T> input, List<List<T>> res, int first) {

        if (first == n) {
            res.add(new ArrayList<>(input));
        }
        for (int i = first; i < n; i++) {

            Collections.swap(input, first, i);

            backtrack(n, input, res, first + 1);

            Collections.swap(input, first, i);
        }
    }
}
