package com.jzycc.layout.dampscrollview.vo;

import java.util.List;

/**
 * Created by Jzy on 2018/4/29.
 */

public class Movie {
    private String title;
    private List<Subjects> subjects;

    public List<Subjects> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subjects> subjects) {
        this.subjects = subjects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
