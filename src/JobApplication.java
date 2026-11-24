
public class JobApplication {
    private final int id;
    private final int studentId;
    private final int jobId;
    private ApplicationStatus status;

    public JobApplication(int id, int studentId, int jobId, ApplicationStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getJobId() { return jobId; }
    public ApplicationStatus getStatus() { return status; }

    public boolean updateStatus(ApplicationStatus newStatus) {
        if (this.status != ApplicationStatus.PENDING || newStatus == null) return false;
        this.status = newStatus;
        return true;
    }
}