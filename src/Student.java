
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Student extends User {
    private final String department;
    private final int[] marks;
    private final HashSet<String> skills;

    public Student(int id, String name, String department,
                   int[] marks, Set<String> skills) {
        super(id, name);
        if (marks == null || marks.length != 8) {
            throw new IllegalArgumentException("Exactly eight semester marks are required.");
        }
        this.department = department;
        this.marks = marks.clone();
        this.skills = new HashSet<>();
        if (skills != null) {
            for (String skill : skills) {
                if (skill != null && !skill.isBlank()) {
                    this.skills.add(skill.trim().toUpperCase(Locale.ROOT));
                }
            }
        }
    }

    public String getDepartment() { return department; }
    public int[] getMarks() { return marks.clone(); }
    public Set<String> getSkills() { return Collections.unmodifiableSet(skills); }

    public double getAverage() {
        int total = 0;
        for (int mark : marks) total += mark;
        return (double) total / marks.length;
    }

    @Override
    public void showProfile() {
        System.out.println("=== Student Profile ===");
        System.out.printf("ID         : %d%n", getId());
        System.out.printf("Name       : %s%n", getName());
        System.out.printf("Department : %s%n", department);
        System.out.print("Marks      : ");
        for (int i = 0; i < marks.length; i++) {
            System.out.printf("Sem%d=%d%s", i + 1, marks[i],
                    (i < marks.length - 1) ? ", " : "");
        }
        System.out.println();
        System.out.printf("Average    : %.2f%%%n", getAverage());
        System.out.println("Skills     : " + (skills.isEmpty() ? "(none)" : String.join(", ", skills)));
    }
}