package com.school.management.model;

public class QuizResult {
    private String resultId;
    private String quizId;
    private String quizTitle;
    private String studentId;
    private String studentName;
    private int score;
    private int totalQuestions;
    private String dateTaken;

    public QuizResult() {}

    public QuizResult(String resultId, String quizId, String quizTitle, String studentId, String studentName, int score, int totalQuestions, String dateTaken) {
        this.resultId = resultId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.dateTaken = dateTaken;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }
}
