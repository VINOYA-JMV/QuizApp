package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class YoutubeTranscriptService {

    public List<String> fetchTranscript(String youtubeUrl) throws Exception {
        List<String> transcript = new ArrayList<>();

        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            return transcript;
        }

        String videoId = extractVideoId(youtubeUrl.trim());
        if (videoId.isEmpty()) {
            return transcript;
        }

        // Run the Python script with the video ID
        List<String> cmd = Arrays.asList("python", "transcript.py", videoId);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true); // combine stdout and stderr
        Process p = pb.start();

        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                transcript.add(line);
            }
        }

        int exit = p.waitFor();
        if (exit != 0 || transcript.isEmpty()) {
            System.err.println("Transcript fetch failed for video: " + videoId);
            if (transcript.isEmpty()) {
                System.err.println("Python script returned no output.");
            } else {
                for (String line : transcript) {
                    System.err.println("Python said: " + line);
                }
            }
            return new ArrayList<>();
        }

        return transcript;
    }

    private String extractVideoId(String url) {
        if (url.contains("v=")) {
            String after = url.substring(url.indexOf("v="
