package com.school.management.model;

public class User {
    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    private String id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private String className; // Applicable to students
    private String teacherId; // Applicable to students: tracks assigned teacher
    private String courseId;  // Applicable to students: tracks assigned course

    public User() {}

    public User(String id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String id, String name, String password, Role role, String className) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.className = className;
    }

    public User(String id, String name, String password, Role role, String className, String teacherId, String courseId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.className = className;
        this.teacherId = teacherId;
        this.courseId = courseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
