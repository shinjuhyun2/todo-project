package ui;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditTaskDialog extends JDialog {
    private JTextField titleField;
    private JTextField dueDateField;
    private JComboBox<Task.Priority> priorityCombo;
    private Task originalTask;
    private Task resultTask;
    private boolean confirmed;

    public EditTaskDialog(Frame parent, Task task) {
        super(parent, "작업 수정", true);
        this.originalTask = task;
        initComponents();
        loadTaskData();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 제목
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("작업 제목:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        titleField = new JTextField(20);
        mainPanel.add(titleField, gbc);

        // 마감일
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("마감일:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        dueDateField = new JTextField(20);
        dueDateField.setToolTipText("형식: yyyy-MM-dd (예: 2026-12-31)");
        mainPanel.add(dueDateField, gbc);

        // 안내 레이블
        gbc.gridx = 1;
        gbc.gridy = 2;
        JLabel dateHintLabel = new JLabel("(형식: yyyy-MM-dd, 비워두면 마감일 없음)");
        dateHintLabel.setFont(new Font(dateHintLabel.getFont().getName(), Font.ITALIC, 10));
        dateHintLabel.setForeground(Color.GRAY);
        mainPanel.add(dateHintLabel, gbc);

        // 우선순위
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("우선순위:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        priorityCombo = new JComboBox<>(Task.Priority.values());
        mainPanel.add(priorityCombo, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("저장");
        JButton cancelButton = new JButton("취소");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveButton);
    }

    private void loadTaskData() {
        if (originalTask != null) {
            titleField.setText(originalTask.getTitle());

            if (originalTask.getDueDate() != null) {
                dueDateField.setText(originalTask.getDueDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            priorityCombo.setSelectedItem(originalTask.getPriority());
        }
    }

    private void onSave() {
        String title = titleField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();
        Task.Priority priority = (Task.Priority) priorityCombo.getSelectedItem();

        LocalDate dueDate = null;
        if (!dueDateStr.isEmpty()) {
            try {
                dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "날짜 형식이 올바르지 않습니다. (형식: yyyy-MM-dd)",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Task newTask = new Task(title, dueDate, priority);

        if (!Validator.validateTask(newTask)) {
            JOptionPane.showMessageDialog(this,
                    Validator.getValidationErrorMessage(newTask),
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultTask = newTask;
        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public Task getTask() {
        return resultTask;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
