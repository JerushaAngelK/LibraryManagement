public class User {
    private int id;
    private String name;
    private String role;
    private int borrowedBook;
    private String password;
    public User(int id, String name, String role, int borrowedBook,String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.borrowedBook = borrowedBook;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
    public int borrowedBook() {
        return borrowedBook;
    }
    public String getPassword(){
        return password;
    }
    public void setBorrowed(int Borrowed) {
        this.borrowedBook = Borrowed;
    }
}
