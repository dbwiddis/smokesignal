package analytics;

import java.util.List;
import java.util.Map;

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
    }

}
