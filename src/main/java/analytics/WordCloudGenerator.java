package analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.ColorPalette;

import data.Loader;
import model.Post;

public class WordCloudGenerator {

    public static void main(String[] args) throws IOException {
        final List<WordFrequency> wordFrequencies = generateFrequencies();

        final Dimension dimension = new Dimension(532, 627);
//        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new PixelBoundryBackground("stackoverflow.png"));
//        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1),                new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setColorPalette(new ColorPalette(new Color(0xF48023), new Color(0xBCBBBB), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("wordcloud_small.png");
    }

    private static List<WordFrequency> generateFrequencies() throws IOException {
        Map<String, Integer> freqMap = new HashMap<>();
        Map<Integer, Post> postMap = Loader.loadPosts();
        for (Post p : postMap.values()) {
            if (p.isTp()) {
                if (p.getTitle() != null) {
                    String[] sa = p.getTitle().toLowerCase().split("\\W+");
                    for (String s : sa) {
                        int count = freqMap.getOrDefault(s, 0);
                        freqMap.put(s, count - 1);
                    }
                }
                if (p.getBody() != null) {
                    String[] sa = p.getBody().toLowerCase().split("\\W+");
                    for (String s : sa) {
                        int count = freqMap.getOrDefault(s, 0);
                        freqMap.put(s, count - 1);
                    }
                }
            }
        }
        List<String> stopWords = Files.readAllLines(Paths.get("stop_words.txt"), StandardCharsets.UTF_8);
        List<WordFrequency> freqs = new ArrayList<>();
        List<Entry<String, Integer>> sortedList = new ArrayList<>(freqMap.entrySet());
        sortedList.sort(Entry.comparingByValue());
        int limit = 1000;
        for (Entry<String, Integer> e : sortedList) {
            if (e.getKey().length() >= 4 && !stopWords.contains(e.getKey())) {
                if (limit-- <= 0) {
                    break;
                }
                freqs.add(new WordFrequency(e.getKey(), -1 * e.getValue()));
            }
        }
        System.out.println(limit);
        return freqs;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
