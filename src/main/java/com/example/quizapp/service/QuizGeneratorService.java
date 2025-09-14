package com.example.quizapp.service;

import com.example.quizapp.model.Question;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class QuizGeneratorService {
    private static final Set<String> STOPWORDS = Set.of(
        "the","and","to","of","a","in","is","that","it","for","on","with","as","are","was","were","by"
    );

    public List<Question> generate(String text, int maxQuestions){
        if(text == null || text.isBlank()) return Collections.emptyList();

        String[] sentences = text.split("(?<=[.!?])\\s+");
        List<Question> out = new ArrayList<>();

        for(String s : sentences){
            if(out.size()>=maxQuestions) break;
            String cleaned = s.replaceAll("[^\\p{L}\\p{N}\\s]", "").trim();
            if(cleaned.length()<20) continue;

            String [] words = cleaned.split("\\s+");
            Optional<String> candidateOpt = Arrays.stream(words)
                .map(w -> w.replaceAll("\\W", ""))
                .filter(w -> w.length() > 3)
                .filter(w -> !STOPWORDS.contains(w.toLowerCase()))
                .max(Comparator.comparingInt(String::length));

            if (candidateOpt.isEmpty()) continue;
            String candidate = candidateOpt.get();

            String prompt = cleaned.replaceFirst("(?i)\\b" + Pattern.quote(candidate) + "\\b", "_____");
            out.add(new Question(prompt, candidate));
        }

        return out;
    }
}
