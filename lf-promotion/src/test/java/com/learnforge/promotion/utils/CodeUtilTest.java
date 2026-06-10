package com.learnforge.promotion.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeUtilTest {

    @Test
    void shouldGenerateEveryOrderedNonEmptySubset() {
        List<List<Integer>> result = PermuteUtil.permuteSubsets(Arrays.asList(1, 2, 3));

        assertEquals(15, result.size());
        assertTrue(result.contains(Arrays.asList(1)));
        assertTrue(result.contains(Arrays.asList(1, 2)));
        assertTrue(result.contains(Arrays.asList(2, 1)));
        assertTrue(result.contains(Arrays.asList(3, 2, 1)));
    }
}
