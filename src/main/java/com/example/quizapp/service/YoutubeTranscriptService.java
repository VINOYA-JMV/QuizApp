package com.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.List;

@Service
public class YoutubeTranscriptService {
    public String fetchTranscript(String youtubeUrl) throws Exception{
        if(youtubeUrl==null || youtubeUrl.trim().isEmpty()) return "";

        String videoId = extractVideoId(youtubeUrl.trim());
        if(videoId.isEmpty()) return "";

        List<String> cmd = Arrays.asList(
            "python", "-c",
            "from youtube_transcript_api import YoutubeTranscriptApi;"+
            "print('\\n'.join([t['text'] for t in YoutubeTranscriptApi.get_transcript(\""+ videoId + "\")]))"
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        StringBuilder out = new StringBuilder();
        try(BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))){
            String line;
            while((line = r.readLine()) != null){
                out.append(line).append("\n");
            }
        }
        int exit = p.waitFor();
        if(exit != 0 || out.length()==0){
            return "";
        }
        return out.toString();
    }

    private String extractVideoId(String url){
        if(url.contains("v=")){
            String after = url.substring(url.indexOf("v=")+2);
            int amp = after.indexOf('&');
            return amp==-1 ? after : after.substring(0, amp);
        }
        else if(url.contains("youtu.be/")){
            return url.substring(url.lastIndexOf('/')+1);
        }
        else{
            return url;
        }
    }
}
