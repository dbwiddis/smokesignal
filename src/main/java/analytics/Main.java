package analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

        assessMultiMergedScoring(postMap, reasonMap, postReasonsMap);
//        assessMergedScoring(postMap, reasonMap, postReasonsMap);
//        assessScoring(postMap, reasonMap, postReasonsMap);
//        assessBayes(postMap, reasonMap, postReasonsMap);
//        assessConsensus(postMap, reasonMap, postReasonsMap);            
//        assessOverlap(reasonMap, reasonPostsMap);
    }

    private static void assessMultiMergedScoring(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {
        System.out.format("%9s %8s %8s %8s %8s %8s %9s%n", "min score", "Δ tp", "Δ naa", "Δ fp", "old acc", "new acc",
                "Δ tp%");
        for (int score = 50; score <= 300; score += 5) {
            int tp0 = 0;
            int fp0 = 0;
            int naa0 = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
                int totalScore = Scoring.scoreAdditive(reasonMap, reasons);
                if (totalScore >= score) {
                    if (post.isTp()) {
                        tp0++;
                    } else if (post.isNaa()) {
                        naa0++;
                    } else {
                        fp0++;
                    }
                }
            }
            double accuracyBaseline = 100d * tp0 / (tp0 + naa0 + fp0);

            int tp = 0;
            int fp = 0;
            int naa = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
                int totalScore = Scoring.scoreMergedAdditive(reasonMap, reasons);
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
                double accuracy = 100d * tp / (tp + naa + fp);

                System.out.format("%9d %8d %8d %8d %8.2f %8.2f %8.2f%%%n", score, tp - tp0, naa - naa0, fp - fp0,
                        accuracyBaseline, accuracy, accuracy - accuracyBaseline);

            }
        }
    }

    private static void assessMergedScoring(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {
        int tp0 = 0;
        int fp0 = 0;
        int naa0 = 0;
        for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
            Post post = postMap.get(entry.getKey());
            List<Integer> reasons = entry.getValue();
            int totalScore = Scoring.scoreAdditive(reasonMap, reasons);
            if (totalScore >= 159) {
                if (post.isTp()) {
                    tp0++;
                } else if (post.isNaa()) {
                    naa0++;
                } else {
                    fp0++;
                }
            }
        }
        double accuracyBaseline = 100d * tp0 / (tp0 + naa0 + fp0);
        System.out.println("Baseline accuracy: " + accuracyBaseline);

        List<Integer> reasonList = reasonMap.keySet().stream()
                .filter(k -> reasonMap.get(k).getWeight() > 1 && !reasonMap.get(k).isInactive())
                .collect(Collectors.toList());
        int size = reasonList.size();
        System.out.format("%3s %3s %8s %8s %8s %9s%n", "r1", "r2", "Δ tp", "Δ naa", "Δ fp", "Δ tp%");
        for (int i = 0; i < size; i++) {
            int ri = reasonList.get(i);
            for (int j = i + 1; j < size; j++) {
                int rj = reasonList.get(j);
                int adjustment = Math.min(reasonMap.get(ri).getWeight(), reasonMap.get(rj).getWeight());
                int tp = 0;
                int fp = 0;
                int naa = 0;
                for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                    Post post = postMap.get(entry.getKey());
                    List<Integer> reasons = entry.getValue();
                    int totalScore = Scoring.scoreAdditive(reasonMap, reasons, ri, rj, adjustment);
                    if (totalScore >= 159) {
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
                    double accuracy = 100d * tp / (tp + naa + fp);
                    if (accuracy > accuracyBaseline + 0.02 && fp < fp0 - 10) {
                        System.out.format("%3d %3d %8d %8d %8d %8.2f%%%n", ri, rj, tp - tp0, naa - naa0, fp - fp0,
                                accuracy - accuracyBaseline);
                    }
                }
            }
        }
    }

    private static void assessScoring(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {

        System.out.format("%9s %8s %8s %8s %9s%n", "Min Score", "tp", "naa", "fp", "tp %");
        for (int score = 10; score <= 400; score += 10) {
            int tp = 0;
            int fp = 0;
            int naa = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
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
            if (tp + fp + naa > 0) {
                System.out.format("%9d %8d %8d %8d %8.2f%%%n", score, tp, naa, fp, 100d * tp / (tp + naa + fp));
            }
        }
    }

    private static void assessBayes(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {
        System.out.println();
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
    }

    private static void assessConsensus(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {
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
    }

    private static void assessOverlap(Map<Integer, Reason> reasonMap, Map<Integer, List<Integer>> reasonPostsMap) {
        List<Integer> reasonList = reasonMap.keySet().stream()
                .filter(k -> reasonMap.get(k).getWeight() > 1 && !reasonMap.get(k).isInactive())
                .collect(Collectors.toList());
        int size = reasonList.size();
        System.out.println(size + " active reasons.");
        for (int i = 0; i < size; i++) {
            int ri = reasonList.get(i);
            List<Integer> iPosts = reasonPostsMap.get(ri);
            System.out.println("Reason " + ri + " (Weight " + reasonMap.get(ri).getWeight() + "): "
                    + reasonMap.get(ri).getReasonName());
            for (int j = i + 1; j < size; j++) {
                int rj = reasonList.get(j);
                List<Integer> jPosts = reasonPostsMap.get(rj);
                List<Integer> ijPosts = new ArrayList<>();
                ijPosts.addAll(iPosts);
                ijPosts.retainAll(jPosts);
                int iSize = iPosts.size();
                int jSize = jPosts.size();
                int ijSize = ijPosts.size();
                if (ijSize > iSize / 10 && ijSize > jSize / 10) {
                    System.out.format("Reasons %d (%d posts) and %d (%d posts) overlap is %d (%.3f%% / %.3f%%)%n", ri,
                            iSize, rj, jSize, ijSize, 100d * ijSize / iSize, 100d * ijSize / jSize);
                }
            }
        }
    }

}
