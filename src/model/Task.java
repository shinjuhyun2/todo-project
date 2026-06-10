package model;

import service.*;
import ui.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private int id;
    private String title;
    private LocalDate dueDate;
    private Priority priority;
    private boolean completed;

    public enum Priority {
        HIGH("높음"),
        MEDIUM("보통"),
        LOW("낮음");

        private final String korean;

        Priority(String korean) {
            this.korean = korean;
        }

        public String getKorean() {
            return korean;
        }

        @Override
        public String toString() {
            return korean;
        }
    }

    public Task(String title, LocalDate dueDate, Priority priority) {
        this.id = nextId++;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "마감일 없음";
        }
        return dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)",
                completed ? "✓" : " ",
                title,
                getFormattedDueDate(),
                priority.getKorean());
    }

    public static void setNextId(int nextId) {
        Task.nextId = nextId;
    }
}
