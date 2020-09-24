package analytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import model.Reason;

public class Scoring {

    private Scoring() {
    }

    public static int scoreMergedAdditive(Map<Integer, Reason> reasonMap, List<Integer> reasons) {
        int score = scoreAdditive(reasonMap, reasons);
        if (reasons.contains(3) && reasons.contains(10)) {
            score -= 95;
        }
        if (reasons.contains(7) && reasons.contains(80)) {
            score -= 77;
        }
        if (reasons.contains(23) && reasons.contains(52)) {
            score -= 81;
        }
        if (reasons.contains(33) && reasons.contains(145)) {
            score -= 78;
        }
        if (reasons.contains(35) && reasons.contains(102)) {
            score -= 69;
        }
        if (reasons.contains(90) && reasons.contains(91)) {
            score -= 82;
        }
        return score;
    }

    public static int scoreAdditive(Map<Integer, Reason> reasonMap, List<Integer> reasons, int ri, int rj,
            int adjustment) {
        int score = scoreAdditive(reasonMap, reasons);
        if (reasons.contains(ri) && reasons.contains(rj)) {
            score -= adjustment;
        }
        return score;
    }

    public static int scoreAdditive(Map<Integer, Reason> reasonMap, List<Integer> reasons) {
        int totalScore = 0;
        for (Integer reason : reasons) {
            Reason r = reasonMap.get(reason);
            if (r != null && !r.isInactive()) {
                totalScore += r.getWeight();
            }
        }
        return totalScore;
    }

    public static double scoreBayes(Map<Integer, Reason> reasonMap, List<Integer> reasons) {
        double totalScore = 0.75;
        for (Integer reason : reasons) {
            Reason r = reasonMap.get(reason);
            if (r != null && !r.isInactive()) {
                if (r.getWeight() > 1) {
                    double p = r.getWeight() / 100d;
                    totalScore = p * totalScore / (p * totalScore + (1d - p) * (1d - totalScore));
                }
            }
        }
        return totalScore;
    }

    public static double scoreConsensus(Map<Integer, Reason> reasonMap, List<Integer> reasons) {
        List<Double> scores = new ArrayList<>();
        for (Integer reason : reasons) {
            Reason r = reasonMap.get(reason);
            if (r != null && !r.isInactive() && r.getWeight() > 1) {
                scores.add(r.getWeight() / 100d);
            }
        }
        // Handle non-consensus cases
        if (scores.isEmpty()) {
            return 0.75;
        } else if (scores.size() == 1) {
            return scores.get(0);
        }
        double[] s = new double[scores.size()];
        for (int i = 0; i < s.length; i++) {
            s[i] = scores.get(i);
        }
        double[] t = new double[s.length];
        double[][] d = new double[s.length][s.length];
        double[][] w = new double[s.length][s.length];
        double[] sum = new double[s.length];

        double max = Arrays.stream(s).max().getAsDouble();
        double min = Arrays.stream(s).min().getAsDouble();
        while (max - min > 0.00000000001) {
            for (int i = 0; i < s.length; i++) {
                sum[i] = 0d;
                for (int j = 0; j < s.length; j++) {
                    d[i][j] = i == j ? 0d : Math.abs(s[i] - s[j]);
                    sum[i] += 1 / (0.001 + d[i][j]);
                }
                for (int j = 0; j < s.length; j++) {
                    w[i][j] = d[i][j] / sum[i];
                }
            }
            for (int i = 0; i < s.length; i++) {
                t[i] = 0d;
                for (int j = 0; j < s.length; j++) {
                    t[i] += w[i][j] * (s[j] - s[i]);
                }
            }
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s.length; j++) {
                    s[i] = t[i];
                }
            }

            max = Arrays.stream(s).max().getAsDouble();
            min = Arrays.stream(s).min().getAsDouble();
        }
        return min + (max - min) / 2;
    }

}
