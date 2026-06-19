package com.school.management.model;

public class Attendance {
    private String attendanceId;
    private String date;
    private String className;
    private String courseId;
    private String studentId;
    private String status; // "PRESENT" or "ABSENT"

    public Attendance() {}

    public Attendance(String attendanceId, String date, String className, String courseId, String studentId, String status) {
        this.attendanceId = attendanceId;
        this.date = date;
        this.className = className;
        this.courseId = courseId;
        this.studentId = studentId;
        this.status = status;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
