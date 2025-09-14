package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class YoutubeTranscriptService {

    public String fetchTranscript(String videoUrl) {
        String videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            return "Invalid YouTube URL";
        }

        StringBuilder transcript = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "transcript.py", videoId);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    transcript.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Failed to fetch transcript. Exit code: " + exitCode;
            }

        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }

        return transcript.toString();
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
