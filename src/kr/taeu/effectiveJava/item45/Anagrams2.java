package kr.taeu.effectiveJava.item45;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 스트림을 과하게 사용했다. - 따라 하지 말것!
public class Anagrams2 {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(
                    Collectors.groupingBy(word -> word.chars().sorted()
                                            .collect(StringBuilder::new,
                                                    (stringBuilder, value) -> stringBuilder.append((char) value),
                                                    StringBuilder::append).toString()))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .map(group -> group.size() + ": " + group)
                    .forEach(System.out::println);
        }
    }
}