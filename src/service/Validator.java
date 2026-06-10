package service;

import model.*;
import ui.*;

import java.time.LocalDate;

public class Validator {

    public static boolean validateTask(Task task) {
        if (task == null) {
            return false;
        }

        if (!validateTitle(task.getTitle())) {
            return false;
        }

        if (!validateDueDate(task.getDueDate())) {
            return false;
        }

        if (task.getPriority() == null) {
            return false;
        }

        return true;
    }

    public static boolean validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }

        if (title.length() > 100) {
            return false;
        }

        return true;
    }

    public static boolean validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            return true;
        }

        return true;
    }

    public static String getValidationErrorMessage(Task task) {
        if (task == null) {
            return "작업 정보가 없습니다.";
        }

        if (!validateTitle(task.getTitle())) {
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                return "작업 제목을 입력해주세요.";
            }
            if (task.getTitle().length() > 100) {
                return "작업 제목은 100자를 초과할 수 없습니다.";
            }
        }

        if (task.getPriority() == null) {
            return "우선순위를 선택해주세요.";
        }

        return "입력값이 유효하지 않습니다.";
    }
}
