package org.jboss.seam.wiki.util;

import java.util.Arrays;
import java.util.List;

/**
 * String comparison and diff algorithms with customizable result rendering.
 * <p>
 * TODO: Support new methods for word and character diff
 * <p>
 * @author Christian Bauer
 */
public abstract class Diff {

    /**
     * Compares two strings, left and right side, renders a new string with custom boundary markers
     * for deleted and added lines.
     *
     * @param x The "left" side of the comparison.
     * @param y The "right" side of the comparison.
     * @param ignoreStrings These strings are ignored and not marked as different.
     * @return String A result with all deletions and modifications highlighted with custom boundary markers.
     */
    public String[] createDiff(String[] x, String[] y, String... ignoreStrings) {
        List<String> ignoreList = Arrays.asList(ignoreStrings);

        int M = x.length;
        int N = y.length;

        String[] result = new String[M + N]; // N + M? Not nice but safe
        int k = 0;

        int[][] opt = new int[M + 1][N + 1];

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x[i].equals(y[j]))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        int i = 0, j = 0;
        while (i < M && j < N) {
            if (x[i].equals(y[j])) {
                result[k++] = (x[i]);
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                if (ignoreList.contains(x[i]) || "".equals(x[i]) ) {
                    result[k++] = x[i++];
                } else {
                    result[k++] = getDeletionStartMarker() + x[i++] + getDeletionEndMarker();
                }
            } else {
                if (ignoreList.contains(y[j]) || "".equals(y[j]) ) {
                    result[k++] = y[j++];
                 } else {
                     result[k++] = getAdditionStartMarker() + y[j++] + getAdditionEndMarker();
                }
            }
        }

        while (i < M || j < N) {
            if (j == N)  {
                if (ignoreList.contains(x[i]) || "".equals(x[i]) ) {
                    result[k++] = x[i++];
                } else {
                    result[k++] = getDeletionStartMarker() + x[i++] + getDeletionEndMarker();
                }
            } else if (i == M) {
                if (ignoreList.contains(y[j]) || "".equals(y[j]) ) {
                    result[k++] = y[j++];
                 } else {
                     result[k++] = getAdditionStartMarker() + y[j++] + getAdditionEndMarker();
                }
            }
        }
        return result;
    }

    public static String renderWithDelimiter(String[] strings, String delimiter) {
        StringBuilder diff = new StringBuilder();
        for (String s: strings) {
            if (s != null) {
                diff.append(s);
                diff.append(delimiter);
            }
        }
        return diff.toString();
    }

    protected abstract String getDeletionStartMarker();
    protected abstract String getDeletionEndMarker();
    protected abstract String getAdditionStartMarker();
    protected abstract String getAdditionEndMarker();

}
