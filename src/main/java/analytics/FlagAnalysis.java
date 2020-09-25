package analytics;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.Loader;
import model.Flag;
import model.Post;
import model.Reason;

public class FlagAnalysis {
    public static void main(String... args) {
        // Load Data
        Map<Integer, Post> postMap = Loader.loadPosts();
        Map<Integer, Reason> reasonMap = Loader.loadReasons();
        Map<Integer, List<Integer>> postReasonsMap = Loader.loadPostReasons();
        Map<Integer, Flag> flagMap = Loader.loadFlags();

        // Grab posts that were autoflagged
        Set<Post> autoFlagged = new HashSet<>();
        for (Flag flag : flagMap.values()) {
            // Require flag ID over 316 to avoid dry run
            if (flag.getId() > 316 && flag.isAuto()) {
                autoFlagged.add(postMap.get(flag.getPostId()));
            }
        }

        // flags total and impacted
        int sum = 0;
        int sumi = 0;

        // fp's total and impacted
        int fp = 0;
        int fpi = 0;
        int tp = 0;
        int tpi = 0;

        for (Post post : autoFlagged) {
            int scoreBase = Scoring.scoreAdditive(reasonMap, postReasonsMap.get(post.getId()));
            int scoreAdjusted = Scoring.scoreMergedAdditive(reasonMap, postReasonsMap.get(post.getId()));
            boolean impacted = scoreBase > scoreAdjusted;
            boolean isTp = post.isTp();
            sum++;
            sumi += impacted ? 1 : 0;
            if (post.isTp()) {
                tp++;
                tpi += impacted ? 1 : 0;
            } else {
                fp++;
                fpi += impacted ? 1 : 0;
            }
        }
        System.out.println(sumi + " of " + sum + " autoflags affected by correlated scoring.");
        System.out.println(fpi + " of " + fp + " fp autoflags affected by correlated scoring.");
        System.out.println(tpi + " of " + tp + " tp autoflags affected by correlated scoring.");

        System.out.format("%9s %8s %5s %8s %5s %7s %7s  %5s %9s%n", "min score", "tp flags", "Δ tp", "fp flags", "Δ fp",
                "old acc", "new acc", "Δ acc", "Δ fp rate");
        for (int score = 150; score <= 250; score += 10) {
            // total and impacted above threshold
            int fpt = 0;
            int tpt = 0;
            int fpti = 0;
            int tpti = 0;
            int fnt = 0;
            int tnt = 0;
            for (Post post : autoFlagged) {
                int scoreBase = Scoring.scoreAdditive(reasonMap, postReasonsMap.get(post.getId()));
                int scoreAdjusted = Scoring.scoreMergedAdditive(reasonMap, postReasonsMap.get(post.getId()));
                boolean impacted = scoreBase > scoreAdjusted;
                boolean threshold = scoreBase >= score;
                boolean fixed = !(scoreAdjusted >= score);
                if (threshold) {
                    if (post.isTp()) {
                        tpt++;
                        tpti += impacted ? 1 : 0;
                        tnt += (impacted && fixed) ? 1 : 0;
                    } else {
                        fpt++;
                        fpti += impacted ? 1 : 0;
                        fnt += (impacted && fixed) ? 1 : 0;
                    }
                }
            }
            double oldacc = 100d * tpt / (tpt + fpt);
            double newacc = 100d * (tpt - tnt) / (tpt - tnt + fpt - fnt);
            System.out.format("%9d %8d %5d %8d %5d  %3.2f%%  %3.2f%%  %1.2f%%    %1.2f%%%n", score, tpt, -tnt, fpt,
                    -fnt, oldacc, newacc, newacc - oldacc, (oldacc - newacc) / (100d - oldacc));
        }
    }
}
