package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

@Service
public class YoutubeTranscriptService {

    public String getTranscript(String videoUrl) {
        try {
            // Extract video ID from YouTube URL
            String videoId = extractVideoId(videoUrl);
            if (videoId == null) {
                return "Invalid YouTube URL.";
            }

            // For now, simulate transcript (since we don’t yet use YouTube API)
            // Later we’ll integrate official API
            return "Transcript for video ID: " + videoId + " (simulated response)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching transcript: " + e.getMessage();
        }
    }

    private String extractVideoId(String videoUrl) {
        if (videoUrl.contains("v=")) {
            return videoUrl.substring(videoUrl.indexOf("v=") + 2, videoUrl.indexOf("v=") + 13);
        } else if (videoUrl.contains("youtu.be/")) {
            return videoUrl.substring(videoUrl.indexOf("youtu.be/") + 9, videoUrl.indexOf("youtu.be/") + 20);
        }
        return null;
    }
    public List<String> fetchTranscript(String videoUrl) {
        // TODO: Replace with real YouTube transcript fetching logic
        List<String> transcript = new ArrayList<>();
        transcript.add("This is a placeholder transcript line 1.");
        transcript.add("This is a placeholder transcript line 2.");
        transcript.add("This is a placeholder transcript line 3.");
        return transcript;
    }
}
