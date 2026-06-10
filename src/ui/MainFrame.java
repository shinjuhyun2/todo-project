package ui;

import java.util.*;

import model.*;
import service.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private TaskManager taskManager;
    private TaskListPanel taskListPanel;
    private JTextField searchField;
    private JComboBox<TaskManager.SortCriteria> sortCombo;
    private JLabel statusLabel;

    public MainFrame() {
        taskManager = new TaskManager();
        initComponents();
        updateStatusBar();

        setTitle("My Focusing Time - To-Do List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 상단 패널 (검색, 정렬, 추가)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 검색 패널
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("검색:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchField);

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);

        JButton resetButton = new JButton("전체보기");
        resetButton.addActionListener(e -> resetSearch());
        searchPanel.add(resetButton);

        // 정렬 및 추가 패널
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(new JLabel("정렬:"));
        sortCombo = new JComboBox<>(TaskManager.SortCriteria.values());
        sortCombo.addActionListener(e -> performSort());
        controlPanel.add(sortCombo);

        JButton addButton = new JButton("작업 추가");
        addButton.setFont(new Font(addButton.getFont().getName(), Font.BOLD, 14));
        addButton.addActionListener(e -> addTask());
        controlPanel.add(addButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 (작업 목록)
        taskListPanel = new TaskListPanel(taskManager, this);
        add(taskListPanel, BorderLayout.CENTER);

        // 하단 상태바
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel();
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // 메뉴바
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        JMenuItem exitItem = new JMenuItem("종료");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // 작업 메뉴
        JMenu taskMenu = new JMenu("작업");
        JMenuItem addItem = new JMenuItem("작업 추가");
        addItem.addActionListener(e -> addTask());
        taskMenu.add(addItem);

        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        JMenuItem aboutItem = new JMenuItem("정보");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(taskMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void addTask() {
        AddTaskDialog dialog = new AddTaskDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Task newTask = dialog.getTask();
            if (taskManager.addTask(newTask)) {
                JOptionPane.showMessageDialog(this,
                        "작업이 추가되었습니다.",
                        "성공",
                        JOptionPane.INFORMATION_MESSAGE);
                taskListPanel.refreshTaskList();
                updateStatusBar();
            } else {
                JOptionPane.showMessageDialog(this,
                        "작업 추가에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        java.util.List<Task> results = taskManager.searchTasks(keyword);

        if (results.isEmpty() && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "검색 결과가 없습니다.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // TaskListPanel에 검색 결과 표시를 위한 임시 메서드
        displaySearchResults(results);
    }

    private void displaySearchResults(java.util.List<Task> tasks) { // java.awt.List와의 충돌을 방지하기 위해 java.util.List를 명시적으로 사용함!
        // TaskTableModel에 직접 접근하기 위해 TaskListPanel을 다시 생성
        taskListPanel.showTasks(tasks);
    }

    private void updateTaskDisplay(java.util.List<Task> tasks) {
        // 이 메서드는 TaskListPanel의 내부 구현에 따라 달라질 수 있음
        // 현재는 전체 새로고침으로 처리
        taskListPanel.refreshTaskList();
    }

    private void resetSearch() {
        searchField.setText("");
        taskListPanel.refreshTaskList();
    }

    private void performSort() {
        if (taskManager.getTaskCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "정렬할 작업이 없습니다.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        TaskManager.SortCriteria criteria =
                (TaskManager.SortCriteria) sortCombo.getSelectedItem();

        if (criteria != null) {
            taskManager.sortTasks(criteria);
            taskListPanel.refreshTaskList();
        }
    }

    public void updateStatusBar() {
        int total = taskManager.getTaskCount();
        int completed = taskManager.getCompletedCount();
        int remaining = total - completed;

        statusLabel.setText(String.format(
                "전체: %d개 | 완료: %d개 | 남은 작업: %d개",
                total, completed, remaining));
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this, "My Focusing Time v1.0 " + "간단하고 효율적인 할 일 관리 프로그램 " + "개발자: 신주현 (22411976) " + "Email: s5264075@gmail.com", "정보",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
