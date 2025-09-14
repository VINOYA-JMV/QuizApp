package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        // Full path to transcript.py (recommended for Windows users)
        String scriptPath = "transcript.py"; // put transcript.py in project root

        ProcessBuilder pb = new ProcessBuilder("python", scriptPath, videoId);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transcript.add(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || transcript.isEmpty()) {
            System.err.println("Python script failed or returned no transcript");
            return new ArrayList<>();
        }

        return transcript;
    }

    private String extractVideoId(String url) {
        if (url.contains("v=")) {
            String after = url.substring(url.indexOf("v=") + 2);
            int amp = after.indexOf('&');
            return amp == -1 ? after : after.substring(0, amp);
        } else if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf('/') + 1);
        } else {
            return url;
        }
    }
}
