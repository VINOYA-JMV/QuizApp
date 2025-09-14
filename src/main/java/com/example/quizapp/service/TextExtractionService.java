package com.example.quizapp.service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractionService {
    public String extractText(MultipartFile file) throws Exception{
        if(file==null || file.isEmpty())return "";

        String name = file.getOriginalFilename();
        if(name == null) name="";

        name = name.toLowerCase();

        if(name.endsWith(".pdf")){
            try(InputStream in = file.getInputStream();
                PDDocument doc = PDDocument.load(in)){
                    PDFTextStripper stripper= new PDFTextStripper();
                    return stripper.getText(doc);
                }
        }
        else if(name.endsWith(".docx")){
            try(InputStream in = file.getInputStream();
                XWPFDocument doc = new XWPFDocument(in)){
                    XWPFWordExtractor extractor=new XWPFWordExtractor(doc);
                    return extractor.getText();
                }
        }
        else{
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
    }
}
