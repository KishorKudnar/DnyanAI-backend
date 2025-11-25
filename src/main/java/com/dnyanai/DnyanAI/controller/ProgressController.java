package com.dnyanai.DnyanAI.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnyanai.DnyanAI.model.UserProgress;
import com.dnyanai.DnyanAI.service.ProgressService;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {

    @Autowired
    private ProgressService service;

    @PostMapping("/get")
    public ResponseEntity<Map<String, Object>> getProgress(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        UserProgress p = service.getOrCreate(email);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("progress", p.getProgressJson());
        res.put("history", p.getHistoryJson());
        res.put("weekly", p.getWeeklyJson());

        System.out.println("GET PROGRESS -> " + email);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveProgress(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String progress = req.get("progress");
        String history = req.get("history");
        String weekly = req.get("weekly");

        System.out.println("=== SAVING PROGRESS ===");
        System.out.println("Email: " + email);
        System.out.println("Progress: " + progress);
        System.out.println("History: " + history);
        System.out.println("Weekly: " + weekly);

        service.save(email, progress, history, weekly);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> reset(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        service.reset(email);

        System.out.println("RESET PROGRESS -> " + email);

        return ResponseEntity.ok(Map.of("success", true));
    }
}
