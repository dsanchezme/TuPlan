package com.dadm.tuplan.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Plan {
    private String title;
    private String description;
    private String status;
    private String startDate;
    private User owner;
    private String priority;
    private String sharedWith;

    public Plan(String title, String description, String status, String startDate, User owner, String priority, String sharedWith) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.owner = owner;
        this.priority = priority;
        this.sharedWith = sharedWith;
    }
    public Plan(Map<String, Object> map){
        this.title = (String) map.get("title");
        this.description = (String) map.get("description");
        this.status = (String) map.get("status");
        this.startDate = (String) map.get("startDate");
        this.owner = new User((Map<String, Object>) map.get("owner"));
        this.priority = (String) map.get("priority");
        this.sharedWith = (String) map.get("sharedWith");
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

    public String getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startDate='" + startDate + '\'' +
                ", owner=" + owner +
                ", priority='" + priority + '\'' +
                ", sharedWith=" + sharedWith +
                '}';
    }
}
