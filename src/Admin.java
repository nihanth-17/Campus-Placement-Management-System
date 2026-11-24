
public class Admin extends User {

    public Admin() {
        super(0, "College Admin");
    }

    @Override
    public void showProfile() {
        System.out.println("=== Admin Profile ===");
        System.out.println("ID   : " + getId());
        System.out.println("Name : " + getName());
        System.out.println("Role : Administrator");
    }
}