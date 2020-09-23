package analytics;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import data.Loader;
import model.Post;
import model.Reason;
import util.Pair;

public class Main {
    public static void main(String... args) {
        // Load Data
        Map<Integer, Post> postMap = Loader.loadPosts();
        Map<Integer, Reason> reasonMap = Loader.loadReasons();
        Pair<Map<Integer, List<Integer>>, Map<Integer, List<Integer>>> pair = Loader.loadPostReasons();
        Map<Integer, List<Integer>> postReasonsMap = pair.getA();
        Map<Integer, List<Integer>> reasonPostsMap = pair.getB();

        System.out.format("%5s %8s %8s %8s %8s %9s%n", "rules", "Min score", "tp", "naa", "fp", "tp %");
        for (int rules = 1; rules <= 5; rules++) {
            for (int score = 10; score <= 400; score += 10) {
                int tp = 0;
                int fp = 0;
                int naa = 0;
                for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                    Post post = postMap.get(entry.getKey());
                    List<Integer> reasons = entry.getValue();
                    if (reasons.size() == rules) {
                        int totalScore = Scoring.scoreAdditive(reasonMap, reasons);
                        if (totalScore >= score) {
                            if (post.isTp()) {
                                tp++;
                            } else if (post.isNaa()) {
                                naa++;
                            } else {
                                fp++;
                            }

                        }
                    }
                }
                if (tp + fp + naa > 0) {
                    System.out.format("%5d %8d %8d %8d %8d %8.2f%%%n", rules, score, tp, naa, fp,
                            100d * tp / (tp + naa + fp));
                }
            }
        }
        System.out.println();
        /*-
        System.out.format(" %12s %8s %8s %8s %9s%n", "Min score", "tp", "naa", "fp", "tp %");
        for (double score = 0.8; score <= 0.9999999999; score += (1d - score) / 2d) {
            int tp = 0;
            int fp = 0;
            int naa = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
        
                double totalScore = Scoring.scoreBayes(reasonMap, reasons);
                if (totalScore >= score) {
                    if (post.isTp()) {
                        tp++;
                    } else if (post.isNaa()) {
                        naa++;
                    } else {
                        fp++;
                    }
                }
            }
            if (tp + fp + naa > 0) {
                System.out.format(" %12.10f %8d %8d %8d %8.2f%%%n", score, tp, naa, fp, 100d * tp / (tp + naa + fp));
            }
        }
        System.out.println();
        
        System.out.format(" %12s %8s %8s %8s %9s%n", "Min score", "tp", "naa", "fp", "tp %");
        for (double score = 0.8; score <= 0.9999999999; score += (1d - score) / 2d) {
            int tp = 0;
            int fp = 0;
            int naa = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
        
                double totalScore = Scoring.scoreConsensus(reasonMap, reasons);
                if (totalScore >= score) {
                    if (post.isTp()) {
                        tp++;
                    } else if (post.isNaa()) {
                        naa++;
                    } else {
                        fp++;
                    }
                }
            }
            if (tp + fp + naa > 0) {
                System.out.format(" %12.10f %8d %8d %8d %8.2f%%%n", score, tp, naa, fp, 100d * tp / (tp + naa + fp));
            }
        }
        */
    }
}
