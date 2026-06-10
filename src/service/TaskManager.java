package service;

import model.*;
import ui.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> taskList;
    private static final String DATA_FILE = "tasks.dat";

    public TaskManager() {
        this.taskList = new ArrayList<>();
        loadTasks();
    }

    public boolean addTask(Task task) {
        if (!Validator.validateTask(task)) {
            return false;
        }
        taskList.add(task);
        saveTasks();
        return true;
    }

    public boolean deleteTask(Task task) {
        if (task == null) {
            return false;
        }
        boolean removed = taskList.remove(task);
        if (removed) {
            saveTasks();
        }
        return removed;
    }

    public boolean updateTask(Task oldTask, Task newTask) {
        if (!Validator.validateTask(newTask)) {
            return false;
        }

        int index = taskList.indexOf(oldTask);
        if (index != -1) {
            newTask.setCompleted(oldTask.isCompleted());
            taskList.set(index, newTask);
            saveTasks();
            return true;
        }
        return false;
    }

    public List<Task> viewTasks() {
        return new ArrayList<>(taskList);
    }

    public List<Task> searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return viewTasks();
        }

        String lowerKeyword = keyword.toLowerCase();
        return taskList.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public void sortTasks(SortCriteria criteria) {
        switch (criteria) {
            case PRIORITY:
                taskList.sort(Comparator.comparing(Task::getPriority));
                break;
            case DUE_DATE:
                taskList.sort(Comparator.comparing(Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case TITLE:
                taskList.sort(Comparator.comparing(Task::getTitle));
                break;
            case COMPLETED:
                taskList.sort(Comparator.comparing(Task::isCompleted));
                break;
        }
        saveTasks();
    }

    public boolean toggleTaskCompletion(Task task) {
        if (task != null && taskList.contains(task)) {
            task.toggleCompleted();
            saveTasks();
            return true;
        }
        return false;
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(taskList);
            oos.writeInt(getMaxId() + 1);
        } catch (IOException e) {
            System.err.println("작업 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            taskList = (List<Task>) ois.readObject();
            int nextId = ois.readInt();
            Task.setNextId(nextId);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("작업 불러오기 중 오류 발생: " + e.getMessage());
            taskList = new ArrayList<>();
        }
    }

    private int getMaxId() {
        return taskList.stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0);
    }

    public int getTaskCount() {
        return taskList.size();
    }

    public int getCompletedCount() {
        return (int) taskList.stream()
                .filter(Task::isCompleted)
                .count();
    }

    public enum SortCriteria {
        PRIORITY("우선순위"),
        DUE_DATE("마감일"),
        TITLE("제목"),
        COMPLETED("완료 여부");

        private final String korean;

        SortCriteria(String korean) {
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
}

