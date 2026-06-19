package com.school.management.model;

public class CourseAssignment {
    private String assignmentId;
    private String teacherId;
    private String className;
    private String courseId;

    public CourseAssignment() {}

    public CourseAssignment(String assignmentId, String teacherId, String className, String courseId) {
        this.assignmentId = assignmentId;
        this.teacherId = teacherId;
        this.className = className;
        this.courseId = courseId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
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
}
