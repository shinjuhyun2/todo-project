package ui;

import service.*;
import model.*;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class TaskListPanel extends JPanel {
    private JTable taskTable;
    private TaskTableModel tableModel;
    private TaskManager taskManager;
    private MainFrame mainFrame;

    public TaskListPanel(TaskManager taskManager, MainFrame mainFrame) {
        this.taskManager = taskManager;
        this.mainFrame = mainFrame;
        initComponents();
        refreshTaskList();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 테이블 생성
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(30);
        taskTable.getTableHeader().setReorderingAllowed(false);

        // 컬럼 너비 설정
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // 완료
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(250); // 제목
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 마감일
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // 우선순위

        // 더블클릭 이벤트
        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTask();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton toggleButton = new JButton("완료 토글");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        toggleButton.addActionListener(e -> toggleTaskCompletion());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());

        buttonPanel.add(toggleButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTaskList() {
        List<Task> tasks = taskManager.viewTasks(); //이놈이 범인임. 검색기능을 쓸려고 해도 이놈이 모든 페이지를 다 가져옴 viewTasks를 해버려서
        tableModel.setTasks(tasks);
    }

    public void showTasks(List<Task> tasks) {
        tableModel.setTasks(tasks);
    }

    private void toggleTaskCompletion() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "작업을 선택해주세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Task task = tableModel.getTaskAt(selectedRow);
        taskManager.toggleTaskCompletion(task);
        refreshTaskList();
        mainFrame.updateStatusBar();
    }

    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "수정할 작업을 선택해주세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Task task = tableModel.getTaskAt(selectedRow);
        EditTaskDialog dialog = new EditTaskDialog((Frame) SwingUtilities.getWindowAncestor(this), task);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (taskManager.updateTask(task, dialog.getTask())) {
                JOptionPane.showMessageDialog(this,
                        "작업이 수정되었습니다.",
                        "성공",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTaskList();
                mainFrame.updateStatusBar();
            } else {
                JOptionPane.showMessageDialog(this,
                        "작업 수정에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "삭제할 작업을 선택해주세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Task task = tableModel.getTaskAt(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "'" + task.getTitle() + "' 작업을 삭제하시겠습니까?",
                "삭제 확인",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (taskManager.deleteTask(task)) {
                JOptionPane.showMessageDialog(this,
                        "작업이 삭제되었습니다.",
                        "성공",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTaskList();
                mainFrame.updateStatusBar();
            } else {
                JOptionPane.showMessageDialog(this,
                        "작업 삭제에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 내부 TableModel 클래스
    private class TaskTableModel extends AbstractTableModel {
        private List<Task> tasks;
        private final String[] columnNames = {"완료", "작업 제목", "마감일", "우선순위"};

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return tasks == null ? 0 : tasks.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (tasks == null || rowIndex >= tasks.size()) {
                return null;
            }

            Task task = tasks.get(rowIndex);
            switch (columnIndex) {
                case 0: return task.isCompleted() ? "✓" : "";
                case 1: return task.getTitle();
                case 2: return task.getFormattedDueDate();
                case 3: return task.getPriority().getKorean();
                default: return null;
            }
        }

        public Task getTaskAt(int rowIndex) {
            if (tasks == null || rowIndex >= tasks.size()) {
                return null;
            }
            return tasks.get(rowIndex);
        }
    }
}
