package analytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Loader;
import model.Post;
import model.Reason;

public class CsvGenerator {
    private static final String SITEDATA = "postdata.csv";

    public static void main(String... args) {

        // Load Data
        Map<Integer, Post> postMap = Loader.loadPosts();
        Map<Integer, Reason> reasonMap = Loader.loadReasons();
        Map<Integer, List<Integer>> postReasonsMap = Loader.loadPostReasons();
        // Map<Integer, Flag> flagMap = Loader.loadFlags();

        File posts = new File(SITEDATA);
        try {
            posts.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Map<Integer, Integer> tpMap = new HashMap<>();
        Map<Integer, Integer> fpMap = new HashMap<>();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(SITEDATA, false))) {
            w.write("score,modscore,penscore,isSpam\n");
            for (Post p : postMap.values()) {
                int score = Scoring.scoreAdditive(reasonMap, postReasonsMap.get(p.getId()));
                int modScore = Scoring.scoreMergedAdditive(reasonMap, postReasonsMap.get(p.getId()));
                int sqScore = Scoring.scorePenalty(reasonMap, postReasonsMap.get(p.getId()));
                boolean spam = p.isTp();
                w.write(score + "," + modScore + "," + sqScore + "," + (spam ? "spam" : "non-spam") + "\n");
            }
            w.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
