
import java.util.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final PlacementService service = new PlacementService();

    public static void main(String[] args) {
        System.out.println("=== Campus Placement Management System ===");
        service.loadAll();

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1 -> registerStudent();
                case 2 -> adminMenu();
                case 3 -> studentMenu();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
    }

    // ─────────── Menus ───────────

    private static void showMainMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("1. Register student");
        System.out.println("2. Admin menu");
        System.out.println("3. Student menu");
        System.out.println("0. Exit");
    }

    private static void showAdminMenu() {
        System.out.println("\nADMIN MENU");
        System.out.println("1. View all students");
        System.out.println("2. Add a job");
        System.out.println("3. View all jobs");
        System.out.println("4. View all applications");
        System.out.println("5. Update application status");
        System.out.println("6. View reports");
        System.out.println("0. Back to main menu");
    }

    private static void showStudentMenu(int id) {
        System.out.println("\nSTUDENT MENU — ID " + id);
        System.out.println("1. View my profile");
        System.out.println("2. View jobs");
        System.out.println("3. Apply for a job");
        System.out.println("4. View my applications");
        System.out.println("0. Back to main menu");
    }

    // ─────────── Register Student (8 semesters) ───────────

    private static void registerStudent() {
        System.out.println("\n--- Register Student ---");
        String name = readNonBlank("Name: ");
        String dept = readNonBlank("Department: ");

        int[] marks = new int[8];
        for (int i = 0; i < 8; i++) {
            while (true) {
                int v = readInt("Semester " + (i + 1) + " %: ");
                if (v >= 0 && v <= 100) {
                    marks[i] = v;
                    break;
                }
                System.out.println("Mark must be between 0 and 100.");
            }
        }

        System.out.print("Skills (comma-separated, optional): ");
        String raw = sc.nextLine().trim();
        List<String> skills = new ArrayList<>();
        if (!raw.isBlank()) {
            for (String s : raw.split(",")) {
                String t = s.trim();
                if (!t.isBlank() && !t.contains("|")) skills.add(t);
            }
        }

        Student s = service.registerStudent(name, dept, marks, skills);
        System.out.println("✓ Registered. Student ID: " + s.getId());
        System.out.printf("Average: %.2f%%%n", s.getAverage());
        System.out.println("Unique skills: " + (s.getSkills().isEmpty() ? "(none)" : s.getSkills()));
    }

    // ─────────── Admin ───────────

    private static void adminMenu() {
        boolean back = false;
        while (!back) {
            showAdminMenu();
            int c = readInt("Choose: ");
            switch (c) {
                case 1 -> viewAllStudents();
                case 2 -> addJob();
                case 3 -> viewAllJobs();
                case 4 -> viewAllApplications();
                case 5 -> updateApplicationStatus();
                case 6 -> reportsMenu();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        if (service.getStudents().isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (Student s : service.getStudents()) {
            s.showProfile();
            System.out.println();
        }
    }

    private static void addJob() {
        System.out.println("\n--- Add Job ---");
        String company = readNonBlank("Company: ");
        String title = readNonBlank("Job title: ");
        double min;
        while (true) {
            min = readDouble("Min average %: ");
            if (min >= 0 && min <= 100) break;
            System.out.println("Must be between 0 and 100.");
        }
        Job j = service.addJob(company, title, min);
        System.out.println("✓ Job saved. Job ID: " + j.getId());
    }

    private static void viewAllJobs() {
        System.out.println("\n--- All Jobs ---");
        if (service.getJobs().isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (Job j : service.getJobs()) j.showDetails();
    }

    private static void viewAllApplications() {
        System.out.println("\n--- All Applications ---");
        if (service.getApplications().isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (JobApplication a : service.getApplications()) {
            String jobTitle = service.findJob(a.getJobId())
                    .map(Job::getTitle).orElse("(job deleted)");
            System.out.printf("[%d] Student %d | Job %d (%s) | %s%n",
                    a.getId(), a.getStudentId(), a.getJobId(), jobTitle, a.getStatus());
        }
    }

    private static void updateApplicationStatus() {
        System.out.println("\n--- Update Application Status ---");
        if (service.getApplications().isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        viewAllApplications();
        int id = readInt("Application ID: ");
        System.out.println("New status: 1=SELECTED 2=REJECTED");
        int opt = readInt("Choose: ");
        ApplicationStatus ns = switch (opt) {
            case 1 -> ApplicationStatus.SELECTED;
            case 2 -> ApplicationStatus.REJECTED;
            default -> null;
        };
        if (ns == null) {
            System.out.println("Invalid status.");
            return;
        }
        if (service.updateApplicationStatus(id, ns)) {
            System.out.println("✓ Application status saved.");
        }
    }

    private static void reportsMenu() {
        System.out.println();
        System.out.print(service.buildReport());
        System.out.print("Save this report to data/report.txt? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (ans.equals("y")) service.saveReport();
    }

    // ─────────── Student ───────────

    private static void studentMenu() {
        int id = readInt("Enter your student ID: ");
        if (service.findStudent(id).isEmpty()) {
            System.out.println("Student not found.");
            return;
        }
        boolean back = false;
        while (!back) {
            showStudentMenu(id);
            int c = readInt("Choose: ");
            switch (c) {
                case 1 -> service.findStudent(id).ifPresent(Student::showProfile);
                case 2 -> viewAllJobs();
                case 3 -> applyForJob(id);
                case 4 -> viewMyApplications(id);
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void applyForJob(int studentId) {
        viewAllJobs();
        int jobId = readInt("Enter job ID to apply: ");
        JobApplication app = service.applyForJob(studentId, jobId);
        if (app != null) {
            System.out.println("✓ Application " + app.getId() + " saved. Status: " + app.getStatus());
        }
    }

    private static void viewMyApplications(int studentId) {
        System.out.println("\n--- My Applications ---");
        boolean any = false;
        for (JobApplication a : service.getApplications()) {
            if (a.getStudentId() == studentId) {
                any = true;
                String title = service.findJob(a.getJobId()).map(Job::getTitle).orElse("(unknown)");
                System.out.printf("Application %d | %s | %s%n", a.getId(), title, a.getStatus());
            }
        }
        if (!any) System.out.println("No records found.");
    }

    // ─────────── Input helpers ───────────

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isBlank() && !line.contains("|")) return line;
            System.out.println("Cannot be empty or contain '|'.");
        }
    }
}