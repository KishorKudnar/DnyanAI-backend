package com.dnyanai.DnyanAI.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String progressJson;

    @Column(columnDefinition = "TEXT")
    private String historyJson;

    @Column(columnDefinition = "TEXT")
    private String weeklyJson;

    public UserProgress() {
    }

    public UserProgress(String email) {
        this.email = email;
        this.progressJson = "{}";
        this.historyJson = "[]";
        this.weeklyJson = "{}";
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgressJson() {
        return progressJson;
    }

    public void setProgressJson(String progressJson) {
        this.progressJson = progressJson;
    }

    public String getHistoryJson() {
        return historyJson;
    }

    public void setHistoryJson(String historyJson) {
        this.historyJson = historyJson;
    }

    public String getWeeklyJson() {
        return weeklyJson;
    }

    public void setWeeklyJson(String weeklyJson) {
        this.weeklyJson = weeklyJson;
    }
}
