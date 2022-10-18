package com.dadm.tuplan.models;

import java.util.Map;

public class Plan {
    private String title;
    private String description;
    private String status;
    private String startDate;
    private User owner;
    private String priority;

    public Plan(String title, String description, String status, String startDate, User owner, String priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.owner = owner;
        this.priority = priority;
    }
    public Plan(Map<String, Object> map){
        this.title = (String) map.get("title");
        this.description = (String) map.get("description");
        this.status = (String) map.get("status");
        this.startDate = (String) map.get("startDate");
        this.owner = new User((Map<String, Object>) map.get("owner"));
        this.priority = (String) map.get("priority");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
