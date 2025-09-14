package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import com.example.quizapp.service.TextExtractionService;
import com.example.quizapp.service.YoutubeTranscriptService;
import com.example.quizapp.service.QuizGeneratorService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UploadController {
    private final TextExtractionService textService;
    private final YoutubeTranscriptService ytService;
    private final QuizGeneratorService quizService;

    public UploadController(TextExtractionService textService, YoutubeTranscriptService ytService, QuizGeneratorService quizService){
        this.textService = textService;
        this.ytService = ytService;
        this.quizService = quizService;
    }

    @GetMapping("/upload")
    public String index(){
        return "upload";
    }

    @PostMapping("/process")
    public String process(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl, Model model){
        try{
            String text = "";
            if(file != null && !file.isEmpty()){
                text = textService.extractText(file);
            }
            else if(youtubeUrl != null && !youtubeUrl.trim().isEmpty(){
                List<String> transcriptLines = ytService.fetchTranscript(youtubeUrl.trim());
                text = String.join(" ", transcriptLines);
                if(text == null || text.isBlank()){
                    model.addAttribute("error", "Could not fetch transcript from the YouTube URL.");
                    return "upload";
                }
            }
            else{
                model.addAttribute("error", "Please upload a file or paste a YouTube link.");
                return "upload";
            }

            List<Question> questions = quizService.generate(text, 10);
            if (questions.isEmpty()){
                model.addAttribute("error", "No suitable content found to create questions.");
                return "upload";
            }

            model.addAttribute("questions", questions);
            return "quiz";
        }
        catch (Exception e){
            model.addAttribute("error", "Processing error: " + e.getMessage());
            return "upload";
        }
    }
    
}
