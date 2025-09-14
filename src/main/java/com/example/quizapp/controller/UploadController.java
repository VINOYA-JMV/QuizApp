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
    public String process(@RequestParam(value = "file", required = false) MultipartFile file,
                          @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl,
                          Model model) {
        try {
            List<String> transcriptLines = null;
            String text = "";

            // 1️⃣ Process file upload
            if (file != null && !file.isEmpty()) {
                text = textService.extractText(file);
            }
            // 2️⃣ Process YouTube URL
            else if (youtubeUrl != null && !youtubeUrl.trim().isEmpty()) {
                transcriptLines = ytService.fetchTranscript(youtubeUrl.trim());
                if (transcriptLines.isEmpty()) {
                    model.addAttribute("error", "Could not fetch transcript from the YouTube URL.");
                    return "upload";
                }
                // Combine transcript lines for quiz generation
                text = String.join(" ", transcriptLines);
            }
            // 3️⃣ No input
            else {
                model.addAttribute("error", "Please upload a file or paste a YouTube link.");
                return "upload";
            }

            // Generate quiz questions
            List<Question> questions = quizService.generate(text, 10);
            if (questions.isEmpty()) {
                model.addAttribute("error", "No suitable content found to create questions.");
                return "upload";
            }

            model.addAttribute("questions", questions);

            // Pass transcript to template if YouTube was used
            if (transcriptLines != null) {
                model.addAttribute("transcript", transcriptLines);
            }

            return "quiz";
        } catch (Exception e) {
            model.addAttribute("error", "Processing error: " + e.getMessage());
            return "upload";
        }
    }
}
