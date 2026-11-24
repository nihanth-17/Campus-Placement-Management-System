
public class Job {
    private final int id;
    private final String companyName;
    private final String title;
    private final double minPercentage;

    public Job(int id, String companyName, String title, double minPercentage) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.minPercentage = minPercentage;
    }

    public int getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getTitle() { return title; }
    public double getMinPercentage() { return minPercentage; }

    public void showDetails() {
        System.out.printf("[%d] %s — %s | Min Avg: %.2f%%%n",
                id, companyName, title, minPercentage);
    }
}