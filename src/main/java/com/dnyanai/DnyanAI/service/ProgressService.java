package com.dnyanai.DnyanAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dnyanai.DnyanAI.model.UserProgress;
import com.dnyanai.DnyanAI.repository.UserProgressRepository;

@Service
public class ProgressService {

    @Autowired
    private UserProgressRepository repo;

    public UserProgress getOrCreate(String email) {
        return repo.findByEmail(email).orElseGet(() -> {
            UserProgress p = new UserProgress(email);
            repo.save(p);
            return p;
        });
    }

    public void save(String email, String progress, String history, String weekly) {
        UserProgress p = getOrCreate(email);

        if (progress != null) p.setProgressJson(progress);
        if (history != null) p.setHistoryJson(history);
        if (weekly != null) p.setWeeklyJson(weekly);

        repo.save(p);
    }

    public void reset(String email) {
        UserProgress p = getOrCreate(email);
        p.setProgressJson("{}");
        p.setHistoryJson("[]");
        p.setWeeklyJson("{}");
        repo.save(p);
    }
}
