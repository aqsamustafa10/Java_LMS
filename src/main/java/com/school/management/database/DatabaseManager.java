package com.school.management.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.management.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_FILE_PATH = "data/database.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static DatabaseModel dataModel = new DatabaseModel();

    // Lock for file access
    private static final Object lock = new Object();

    public static class DatabaseModel {
        public List<User> users = new ArrayList<>();
        public List<SchoolClass> classes = new ArrayList<>();
        public List<Course> courses = new ArrayList<>();
        public List<CourseAssignment> assignments = new ArrayList<>();
        public List<Attendance> attendance = new ArrayList<>();
        public List<Quiz> quizzes = new ArrayList<>();
        public List<QuizResult> quizResults = new ArrayList<>();
    }

    static {
        load();
    }

    public static void load() {
        synchronized (lock) {
            File file = new File(DB_FILE_PATH);
            if (!file.exists()) {
                // Create parent directories if they don't exist
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                dataModel = new DatabaseModel();
                save();
                return;
            }

            try (FileReader reader = new FileReader(file)) {
                DatabaseModel loaded = gson.fromJson(reader, DatabaseModel.class);
                if (loaded != null) {
                    dataModel = loaded;
                    // Ensure initialized lists
                    if (dataModel.users == null) dataModel.users = new ArrayList<>();
                    if (dataModel.classes == null) dataModel.classes = new ArrayList<>();
                    if (dataModel.courses == null) dataModel.courses = new ArrayList<>();
                    if (dataModel.assignments == null) dataModel.assignments = new ArrayList<>();
                    if (dataModel.attendance == null) dataModel.attendance = new ArrayList<>();
                    if (dataModel.quizzes == null) dataModel.quizzes = new ArrayList<>();
                    if (dataModel.quizResults == null) dataModel.quizResults = new ArrayList<>();
                } else {
                    dataModel = new DatabaseModel();
                }
            } catch (IOException e) {
                System.err.println("Error reading database file: " + e.getMessage());
                dataModel = new DatabaseModel();
            }
        }
    }

    public static void save() {
        synchronized (lock) {
            File file = new File(DB_FILE_PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(dataModel, writer);
            } catch (IOException e) {
                System.err.println("Error writing to database file: " + e.getMessage());
            }
        }
    }

    // --- User operations ---
    public static List<User> getUsers() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.users);
        }
    }

    public static void addUser(User user) {
        synchronized (lock) {
            dataModel.users.add(user);
            save();
        }
    }

    public static User getUserById(String id) {
        synchronized (lock) {
            for (User u : dataModel.users) {
                if (u.getId().equalsIgnoreCase(id)) {
                    return u;
                }
            }
            return null;
        }
    }

    // --- SchoolClass operations ---
    public static List<SchoolClass> getClasses() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.classes);
        }
    }

    public static void addClass(SchoolClass sc) {
        synchronized (lock) {
            dataModel.classes.add(sc);
            save();
        }
    }

    public static SchoolClass getClassById(String id) {
        synchronized (lock) {
            for (SchoolClass sc : dataModel.classes) {
                if (sc.getClassId().equalsIgnoreCase(id)) {
                    return sc;
                }
            }
            return null;
        }
    }

    public static SchoolClass getClassByName(String name) {
        synchronized (lock) {
            for (SchoolClass sc : dataModel.classes) {
                if (sc.getClassName().equalsIgnoreCase(name)) {
                    return sc;
                }
            }
            return null;
        }
    }

    // --- Course operations ---
    public static List<Course> getCourses() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.courses);
        }
    }

    public static void addCourse(Course course) {
        synchronized (lock) {
            dataModel.courses.add(course);
            save();
        }
    }

    public static Course getCourseById(String id) {
        synchronized (lock) {
            for (Course c : dataModel.courses) {
                if (c.getCourseId().equalsIgnoreCase(id)) {
                    return c;
                }
            }
            return null;
        }
    }

    // --- CourseAssignment operations ---
    public static List<CourseAssignment> getAssignments() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.assignments);
        }
    }

    public static void addAssignment(CourseAssignment assignment) {
        synchronized (lock) {
            dataModel.assignments.add(assignment);
            save();
        }
    }

    public static void removeAssignment(String assignmentId) {
        synchronized (lock) {
            dataModel.assignments.removeIf(a -> a.getAssignmentId().equalsIgnoreCase(assignmentId));
            save();
        }
    }

    // --- Attendance operations ---
    public static List<Attendance> getAttendanceRecords() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.attendance);
        }
    }

    public static void addAttendance(Attendance attendance) {
        synchronized (lock) {
            dataModel.attendance.removeIf(a -> 
                a.getStudentId().equalsIgnoreCase(attendance.getStudentId()) &&
                a.getClassName().equalsIgnoreCase(attendance.getClassName()) &&
                a.getCourseId().equalsIgnoreCase(attendance.getCourseId()) &&
                a.getDate().equals(attendance.getDate())
            );
            dataModel.attendance.add(attendance);
            save();
        }
    }

    // --- Quiz operations ---
    public static List<Quiz> getQuizzes() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.quizzes);
        }
    }

    public static void addQuiz(Quiz quiz) {
        synchronized (lock) {
            dataModel.quizzes.add(quiz);
            save();
        }
    }

    public static Quiz getQuizById(String id) {
        synchronized (lock) {
            for (Quiz q : dataModel.quizzes) {
                if (q.getQuizId().equalsIgnoreCase(id)) {
                    return q;
                }
            }
            return null;
        }
    }

    // --- QuizResult operations ---
    public static List<QuizResult> getQuizResults() {
        synchronized (lock) {
            return new ArrayList<>(dataModel.quizResults);
        }
    }

    public static void addQuizResult(QuizResult result) {
        synchronized (lock) {
            dataModel.quizResults.add(result);
            save();
        }
    }

    // --- ID Auto-Generation helpers ---
    public static String generateNextTeacherId() {
        synchronized (lock) {
            int max = 1000;
            for (User u : dataModel.users) {
                if (u.getRole() == User.Role.TEACHER && u.getId() != null && u.getId().startsWith("TCH-")) {
                    try {
                        int num = Integer.parseInt(u.getId().substring(4));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            return "TCH-" + (max + 1);
        }
    }

    public static String generateNextStudentId() {
        synchronized (lock) {
            int max = 1000;
            for (User u : dataModel.users) {
                if (u.getRole() == User.Role.STUDENT && u.getId() != null && u.getId().startsWith("STD-")) {
                    try {
                        int num = Integer.parseInt(u.getId().substring(4));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            return "STD-" + (max + 1);
        }
    }

    public static String generateNextCourseId() {
        synchronized (lock) {
            int max = 1000;
            for (Course c : dataModel.courses) {
                if (c.getCourseId() != null && c.getCourseId().startsWith("CRS-")) {
                    try {
                        int num = Integer.parseInt(c.getCourseId().substring(4));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            return "CRS-" + (max + 1);
        }
    }

    public static String generateNextClassId() {
        synchronized (lock) {
            int max = 1000;
            for (SchoolClass sc : dataModel.classes) {
                if (sc.getClassId() != null && sc.getClassId().startsWith("CLS-")) {
                    try {
                        int num = Integer.parseInt(sc.getClassId().substring(4));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            return "CLS-" + (max + 1);
        }
    }
}
