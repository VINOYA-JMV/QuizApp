package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeTranscriptService {

    public List<String> fetchTranscript(String videoUrl) {
        String videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            return List.of("Invalid YouTube URL");
        }

        List<String> transcript = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "transcript.py", videoId);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    transcript.add(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return List.of("Failed to fetch transcript. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            return List.of("Error: " + e.getMessage());
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
