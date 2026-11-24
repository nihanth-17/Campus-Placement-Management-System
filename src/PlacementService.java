
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class PlacementService {

    private final ArrayList<Student> students = new ArrayList<>();
    private final ArrayList<Job> jobs = new ArrayList<>();
    private final ArrayList<JobApplication> applications = new ArrayList<>();
    private final Admin admin = new Admin();

    private int nextStudentId = 1;
    private int nextJobId = 1;
    private int nextApplicationId = 1;

    public void loadAll() {
        FileManager.loadStudents(students);
        FileManager.loadJobs(jobs);
        FileManager.loadApplications(applications);
        recomputeNextIds();
        System.out.println("Records loaded: " + students.size() + " students, "
                + jobs.size() + " jobs, " + applications.size() + " applications.");
    }

    private void recomputeNextIds() {
        nextStudentId = students.stream().mapToInt(Student::getId).max().orElse(0) + 1;
        nextJobId = jobs.stream().mapToInt(Job::getId).max().orElse(0) + 1;
        nextApplicationId = applications.stream().mapToInt(JobApplication::getId).max().orElse(0) + 1;
    }

    public Admin getAdmin() { return admin; }
    public List<Student> getStudents() { return students; }
    public List<Job> getJobs() { return jobs; }
    public List<JobApplication> getApplications() { return applications; }

    public Optional<Student> findStudent(int id) {
        for (Student s : students) if (s.getId() == id) return Optional.of(s);
        return Optional.empty();
    }

    public Optional<Job> findJob(int id) {
        for (Job j : jobs) if (j.getId() == id) return Optional.of(j);
        return Optional.empty();
    }

    public Optional<JobApplication> findApplication(int id) {
        for (JobApplication a : applications) if (a.getId() == id) return Optional.of(a);
        return Optional.empty();
    }

    public Student registerStudent(String name, String department,
                                   int[] marks, List<String> skills) {
        Student s = new Student(nextStudentId++, name, department, marks, new HashSet<>(skills));
        students.add(s);
        FileManager.saveStudents(students);
        return s;
    }

    public Job addJob(String company, String title, double minPercentage) {
        Job j = new Job(nextJobId++, company, title, minPercentage);
        jobs.add(j);
        FileManager.saveJobs(jobs);
        return j;
    }

    public JobApplication applyForJob(int studentId, int jobId) {
        Optional<Student> sOpt = findStudent(studentId);
        if (sOpt.isEmpty()) { System.out.println("Student not found."); return null; }
        Optional<Job> jOpt = findJob(jobId);
        if (jOpt.isEmpty()) { System.out.println("Job not found."); return null; }

        Student student = sOpt.get();
        Job job = jOpt.get();

        if (student.getAverage() < job.getMinPercentage()) {
            System.out.printf("Not eligible. Your average %.2f%% is below the required %.2f%%%n",
                    student.getAverage(), job.getMinPercentage());
            return null;
        }

        for (JobApplication existing : applications) {
            if (existing.getStudentId() == studentId && existing.getJobId() == jobId) {
                System.out.println("You have already applied for this job.");
                return null;
            }
        }

        JobApplication app = new JobApplication(nextApplicationId++, studentId, jobId, ApplicationStatus.PENDING);
        applications.add(app);
        FileManager.saveApplications(applications);
        return app;
    }

    public boolean updateApplicationStatus(int applicationId, ApplicationStatus newStatus) {
        Optional<JobApplication> opt = findApplication(applicationId);
        if (opt.isEmpty()) { System.out.println("Application not found."); return false; }
        boolean ok = opt.get().updateStatus(newStatus);
        if (ok) FileManager.saveApplications(applications);
        else System.out.println("Cannot update — application is already decided.");
        return ok;
    }

    public String buildReport() {
        long pending = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
        long selected = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
        long rejected = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();

        return "===== PLACEMENT REPORT =====\n"
                + "Total Students      : " + students.size() + "\n"
                + "Total Jobs          : " + jobs.size() + "\n"
                + "Total Applications  : " + applications.size() + "\n"
                + "PENDING             : " + pending + "\n"
                + "SELECTED            : " + selected + "\n"
                + "REJECTED            : " + rejected + "\n";
    }

    public void saveReport() {
        if (FileManager.saveReport(buildReport())) {
            System.out.println("Report saved to data/report.txt");
        }
    }
}