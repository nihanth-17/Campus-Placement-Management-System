
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path STUDENTS_FILE = DATA_DIR.resolve("students.txt");
    private static final Path JOBS_FILE = DATA_DIR.resolve("jobs.txt");
    private static final Path APPLICATIONS_FILE = DATA_DIR.resolve("applications.txt");
    private static final Path REPORT_FILE = DATA_DIR.resolve("report.txt");

    private static void ensureDir() {
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    // ─────────── Students ───────────

    public static void loadStudents(List<Student> out) {
        ensureDir();
        if (!Files.exists(STUDENTS_FILE)) return;
        try (BufferedReader r = Files.newBufferedReader(STUDENTS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length != 5) { warn("students", line); continue; }
                try {
                    out.add(new Student(
                            Integer.parseInt(p[0]),
                            p[1],
                            p[2],
                            parseMarks(p[3]),
                            parseSkills(p[4])));
                } catch (Exception e) {
                    warn("students", line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students.txt: " + e.getMessage());
        }
    }

    public static void saveStudents(List<Student> students) {
        ensureDir();
        try (BufferedWriter w = Files.newBufferedWriter(STUDENTS_FILE, StandardCharsets.UTF_8)) {
            for (Student s : students) {
                StringBuilder marks = new StringBuilder();
                int[] m = s.getMarks();
                for (int i = 0; i < m.length; i++) {
                    if (i > 0) marks.append(',');
                    marks.append(m[i]);
                }
                w.write(s.getId() + "|" + s.getName() + "|" + s.getDepartment()
                        + "|" + marks + "|" + String.join(",", s.getSkills()));
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing students.txt: " + e.getMessage());
        }
    }

    // ─────────── Jobs ───────────

    public static void loadJobs(List<Job> out) {
        ensureDir();
        if (!Files.exists(JOBS_FILE)) return;
        try (BufferedReader r = Files.newBufferedReader(JOBS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length != 4) { warn("jobs", line); continue; }
                try {
                    out.add(new Job(
                            Integer.parseInt(p[0]),
                            p[1],
                            p[2],
                            Double.parseDouble(p[3])));
                } catch (Exception e) {
                    warn("jobs", line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading jobs.txt: " + e.getMessage());
        }
    }

    public static void saveJobs(List<Job> jobs) {
        ensureDir();
        try (BufferedWriter w = Files.newBufferedWriter(JOBS_FILE, StandardCharsets.UTF_8)) {
            for (Job j : jobs) {
                w.write(j.getId() + "|" + j.getCompanyName() + "|" + j.getTitle()
                        + "|" + j.getMinPercentage());
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing jobs.txt: " + e.getMessage());
        }
    }

    // ─────────── Applications ───────────

    public static void loadApplications(List<JobApplication> out) {
        ensureDir();
        if (!Files.exists(APPLICATIONS_FILE)) return;
        try (BufferedReader r = Files.newBufferedReader(APPLICATIONS_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length != 4) { warn("applications", line); continue; }
                try {
                    out.add(new JobApplication(
                            Integer.parseInt(p[0]),
                            Integer.parseInt(p[1]),
                            Integer.parseInt(p[2]),
                            ApplicationStatus.valueOf(p[3].trim().toUpperCase(Locale.ROOT))));
                } catch (Exception e) {
                    warn("applications", line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading applications.txt: " + e.getMessage());
        }
    }

    public static void saveApplications(List<JobApplication> apps) {
        ensureDir();
        try (BufferedWriter w = Files.newBufferedWriter(APPLICATIONS_FILE, StandardCharsets.UTF_8)) {
            for (JobApplication a : apps) {
                w.write(a.getId() + "|" + a.getStudentId() + "|" + a.getJobId()
                        + "|" + a.getStatus().name());
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing applications.txt: " + e.getMessage());
        }
    }

    // ─────────── Report ───────────

    public static boolean saveReport(String reportContent) {
        ensureDir();
        try (BufferedWriter w = Files.newBufferedWriter(REPORT_FILE, StandardCharsets.UTF_8)) {
            w.write(reportContent);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing report.txt: " + e.getMessage());
            return false;
        }
    }

    // ─────────── Helpers ───────────

    /**
     * Parses 8 comma-separated semester marks.
     * Throws if the count isn't exactly 8 or any value is out of [0, 100].
     */
    private static int[] parseMarks(String raw) {
        String[] parts = raw.split(",");
        if (parts.length != 8) {
            throw new IllegalArgumentException("Eight marks required");
        }
        int[] m = new int[8];
        for (int i = 0; i < 8; i++) {
            m[i] = Integer.parseInt(parts[i].trim());
            if (m[i] < 0 || m[i] > 100) {
                throw new IllegalArgumentException("Out of range");
            }
        }
        return m;
    }

    private static Set<String> parseSkills(String raw) {
        Set<String> set = new HashSet<>();
        if (raw == null || raw.isBlank()) return set;
        for (String s : raw.split(",")) {
            if (!s.isBlank()) set.add(s.trim().toUpperCase(Locale.ROOT));
        }
        return set;
    }

    private static void warn(String file, String line) {
        System.err.println("Skipping malformed line in " + file + ": " + line);
    }
}