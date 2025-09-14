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

        List<String> cmd = Arrays.asList(
            "python", "-c",
            "from youtube_transcript_api import YouTubeTranscriptApi;" +
            "print('\\n'.join([t['text'] for t in YouTubeTranscriptApi.get_transcript('" + videoId + "')]))"
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();

        // ✅ capture stdout
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                transcript.add(line);
            }
        }

        // ✅ capture stderr (errors from Python)
        List<String> errors = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                errors.add(line);
            }
        }

        int exit = p.waitFor();
        if (exit != 0 || transcript.isEmpty()) {
            System.err.println("Python error while fetching transcript: " + String.join("\n", errors));
            return new ArrayList<>(); // return empty list on failure
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
