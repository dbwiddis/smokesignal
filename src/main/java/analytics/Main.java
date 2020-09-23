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

        /*-
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
        */

        assessScoring(postMap, reasonMap, postReasonsMap);
        /*-
        assessScoring(postMap, reasonMap, postReasonsMap);
        System.out.println();
        assessBayes(postMap, reasonMap, postReasonsMap);
        System.out.println();
        assessConsensus(postMap, reasonMap, postReasonsMap);            
        */
    }

    private static void assessScoring(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {

        System.out.format("%5s %8s %8s %8s %8s %9s%n", "rules", "Min score", "tp", "naa", "fp", "tp %");
//        for (int rules = 1; rules <= 5; rules++) {
        for (int score = 10; score <= 400; score += 10) {
            int tp = 0;
            int fp = 0;
            int naa = 0;
            for (Entry<Integer, List<Integer>> entry : postReasonsMap.entrySet()) {
                Post post = postMap.get(entry.getKey());
                List<Integer> reasons = entry.getValue();
//                    if (reasons.size() == rules) {
                int totalScore = Scoring.scoreAdditive(reasonMap, reasons);
                if (totalScore >= score) {
                    if (post.isTp()) {
                        tp++;
                    } else if (post.isNaa()) {
                        naa++;
                    } else {
                        fp++;
                        /*-
                        if (totalScore > 190 && reasons.size() < 4) {
                            for (int i = 0; i < reasons.size(); i++) {
                                System.out.print(reasons.get(i) + " ");
                            }
                            System.out.println();
                        }
                        */
                    }

                }
//                    }
}
if (tp + fp + naa > 0) {
    System.out.format("%5s %8d %8d %8d %8d %8.2f%%%n", "any", score, tp, naa, fp, 100d * tp / (tp + naa + fp));
//                    System.out.format("%5d %8d %8d %8d %8d %8.2f%%%n", rules, score, tp, naa, fp, 100d * tp / (tp + naa + fp));
            }
        }
    }
//    }

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
}
