package com.school.management.model;

import java.util.List;

public class Quiz {
    private String quizId;
    private String title;
    private String className;
    private String courseId;
    private String teacherId;
    private List<Question> questions;
    private int timeLimitMinutes;

    public Quiz() {}

    public Quiz(String quizId, String title, String className, String courseId, String teacherId, List<Question> questions, int timeLimitMinutes) {
        this.quizId = quizId;
        this.title = title;
        this.className = className;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.questions = questions;
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }
}
