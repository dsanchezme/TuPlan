package com.dadm.tuplan.models;

import java.util.List;
import java.util.Map;

public class Group {

    private String name;
    private List<String> members;

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<String> members) {
        this.name = name;
        this.members = members;
    }

    public Group(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.members = (List<String>) map.get("members");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}
