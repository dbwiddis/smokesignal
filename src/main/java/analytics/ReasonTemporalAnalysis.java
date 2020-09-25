package analytics;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import data.Loader;
import model.Post;
import model.Reason;

public class ReasonTemporalAnalysis {
    public static void main(String... args) {
        // Load Data
        Map<Integer, Post> postMap = Loader.loadPosts();
        Map<Integer, Reason> reasonMap = Loader.loadReasons();
        Map<Integer, List<Integer>> postReasonsMap = Loader.loadPostReasons();

        assessTemporalScoring(postMap, reasonMap, postReasonsMap);
    }

    private static void assessTemporalScoring(Map<Integer, Post> postMap, Map<Integer, Reason> reasonMap,
            Map<Integer, List<Integer>> postReasonsMap) {
        boolean header = false;
        for (Reason reason : reasonMap.values()) {
            if (!reason.isInactive() && reason.getWeight() > 1) {
                if (!header) {
                    System.out.format("%6s %4s %5s %5s %5s %5s %8s%n", "reason", "year", "tp", "fp", "naa", "weight",
                            "accuracy");
                    header = true;
                }

                for (int year = 2016; year <= 2020; year++) {
                    final int y = year - 1900;
                    Set<Post> yearPosts = postMap.values().stream()
                            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().getYear() == y)
                            .collect(Collectors.toSet());
                    int tp = 0;
                    int fp = 0;
                    int naa = 0;
                    for (Post post : yearPosts) {
                        List<Integer> reasons = postReasonsMap.get(post.getId());
                        if (reasons.contains(reason.getId())) {
                            if (post.isTp()) {
                                tp++;
                            } else if (post.isNaa()) {
                                naa++;
                            } else {
                                fp++;
                            }
                        }
                    }
                    if (tp + fp + naa >= 200) {
                        double accuracy = 100d * tp / (tp + naa + fp);
                        System.out.format("%6d %4d %5d %5d %5d %5d     %2.1f%%%s%n", reason.getId(), year, tp, fp,
                                naa,
                                reason.getWeight(), accuracy,
                                Math.abs(reason.getWeight() - accuracy) > 5 ? " ***" : "");
                        header = false;
                    }
                }
            }
        }
    }

}
