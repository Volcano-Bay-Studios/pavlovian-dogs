package xyz.volcanobay.pavloviandogs.util;

import java.util.Arrays;
import java.util.List;

public class LevenshteinDistance {
    public static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }
    public static BestFit bestFitWord(String t1, String t2) {
        String[] words = t1.split(" ");
        int size = words.length;
        int best = 999999999;
        int index = 0;
        for (int i = 0; i < size; i++) {
            String text = words[i];
            int distance = calculate(text,t2);
            if (distance< best) {
                best = distance;
                index = i;
            }
        }
        String[] next = new String[size-index];
        System.arraycopy(words, index, next, 0, size - index);
        return new BestFit(words[index],best,next);
    }

    public static BestFit bestFitWord(List<String> words, String t2) {
        int size = words.size();
        int best = 999999999;
        int index = 0;
        for (int i = 0; i < size; i++) {
            String text = words.get(i);
            int distance = calculate(text,t2);
            if (distance< best) {
                best = distance;
                index = i;
            }
        }
        String[] next = new String[size-index];
        System.arraycopy(words.toArray(), index, next, 0, size - index);
        return new BestFit(words.get(index),best,next);
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
    public record BestFit(String string, int distance, String[] words) {
    };
}
