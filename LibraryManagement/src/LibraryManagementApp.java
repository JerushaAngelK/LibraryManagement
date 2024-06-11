import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASS= "Talktime2003";
    private static void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "is_Borrowed BOOLEAN NOT NULL DEFAULT 0)";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(200) NOT NULL UNIQUE," +
                     "password TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "borrowedBook INTEGER,"+
                    "FOREIGN KEY (borrowedBook) REFERENCES books(id))";;
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addBookToDatabase(Book book) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO books (title, author, is_Borrowed) VALUES (?, ?, ?)")) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isBorrowed());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Book> getAllBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM books");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean isBorrowed = resultSet.getBoolean("is_Borrowed");
                Book book = new Book(id, title, author, isBorrowed);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private static void displayAllBooks(List<Book> books) {
        System.out.println("Library Books:");
        int i=1;
        for (Book book : books) {
            System.out.println(i+" "+book);
            i++;
        }
    }
  private static List<Book> findBookByName(String title) {
        List<Book> foundBooks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM books WHERE title LIKE ?")) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String bookTitle = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean isBorrowed = resultSet.getBoolean("is_Borrowed");
                Book book = new Book(id, bookTitle, author, isBorrowed);
                foundBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundBooks;
    }
        public static void addUser(String name, String password) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "INSERT INTO users (name, password,role) VALUES (?, ?,'User')";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a user by ID
    public static void deleteUser(String name) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "DELETE FROM users WHERE name = ? and role=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, "User");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a user's password by ID
    public static void updateUserPassword(String name, String newPassword) {
        try {
           Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "UPDATE users SET password = ? WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newPassword);
            statement.setString(2, name);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updateBookBorrowedStatus(int bookId, boolean isBorrowed,String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE books SET is_Borrowed = ? WHERE id = ?")) {
            pstmt.setBoolean(1, isBorrowed);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
            PreparedStatement pstmt1 = conn.prepareStatement("UPDATE users SET borrowedBook = ? WHERE name = ?");
            pstmt1.setInt(1, bookId);
            pstmt1.setString(2, username);
            pstmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     private static boolean isValidAdminCredentials(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS count FROM users WHERE name = ? AND password = ? AND role = 'admin'")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
      private static boolean isValidUserCredentials(String username, String password) {
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(
"SELECT COUNT(*) AS count FROM users WHERE name = ? AND password = ? AND role = 'user'")) {
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("count") > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
private static void bookReturned(int bookId, String username) {
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        // Update the book status
        String updateBookSql = "UPDATE books SET is_Borrowed = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }

        // Get the user ID based on the username
        int userId = -1;
        String getUserIdSql = "SELECT id FROM users WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(getUserIdSql)) {
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        }

        // Update the user's borrowedBook
        String updateUserSql = "UPDATE users SET borrowedBook = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateUserSql)) {
            pstmt.setNull(1, Types.INTEGER); 
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        createTableIfNotExists();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Library Management System!");
        System.out.println("Please log in to continue.");
        boolean isUserLoggedIn = false;
        boolean isAdminLoggedIn = false;
        String username, password;
        do {
            System.out.print("Username: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();

            isUserLoggedIn = isValidUserCredentials(username, password);
            isAdminLoggedIn = isValidAdminCredentials(username, password);

            if (!isUserLoggedIn && !isAdminLoggedIn) {
                System.out.println("Invalid credentials. Please try again.");
            }
        } while (!isUserLoggedIn && !isAdminLoggedIn);

        if (isUserLoggedIn) {
            System.out.println("User Login successful!\n");
            while (true) {
                System.out.println("==== Library Management System ====");
                System.out.println("1. Add Book");
                System.out.println("2. Display All Books");
                System.out.println("3. Rent a Book");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1-4): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 
    
                switch (choice) {
                    case 1:
                        System.out.print("Enter book title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter book author: ");
                        String author = scanner.nextLine();
                        Book newBook = new Book(-1, title, author, false);
                        addBookToDatabase(newBook);
                        System.out.println("Book added successfully!\n");
                        break;
    
                    case 2:
                        List<Book> books = getAllBooksFromDatabase();
                        displayAllBooks(books);
                        System.out.println();
                        break;
    
    
                    case 3:
                                System.out.print("Enter book title to search: ");
                                String searchTitle = scanner.nextLine();
                                List<Book> foundBooks = findBookByName(searchTitle);
                                if (!foundBooks.isEmpty()) {
                                    System.out.println("Books found:");
                                    displayAllBooks(foundBooks);
                                    System.out.println("Enter Book Number to Rent: ");
                                    Book bookToRent = foundBooks.get(scanner.nextInt()-1);
                                    scanner.nextLine();
                                        if (bookToRent.isBorrowed()) {
                                            System.out.println("Book is already Borrowed.");
                                        } else {
                                            updateBookBorrowedStatus(bookToRent.getId(), true,username);
                                            System.out.println("Book Borrowed successfully!");
                                        }
                                } else {
                            System.out.println("No books found with the given title.\n");
                        }
                        break;
    
                    case 4:
                        System.out.println("Exiting Library Management System. Goodbye!");
                        scanner.close();
                        System.exit(0);
    
                    default:
                        System.out.println("Invalid choice. Please enter a valid option (1-4).\n");
                }
        } }
        else if (isAdminLoggedIn) {
            System.out.println("Admin Login successful!\n");
            while (true) {
                System.out.println("==== Library Management System ====");
                System.out.println("1. Add User");
                System.out.println("2. Remove User");
                System.out.println("3. Update Password of User");
                System.out.println("4. Update Book Record");
                System.out.println("5. Exit");
                System.out.print("Enter your choice (1-5): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 
    
                switch (choice) {
                    case 1:
                System.out.print("Enter username: ");
                String name = scanner.next();
                System.out.print("Enter password: ");
                String newpassword = scanner.next();
                addUser(name,newpassword);
                System.out.println("User added successfully!");
                break;
            case 2:
                System.out.print("Enter username to delete: ");
                String oldname = scanner.next();
                deleteUser(oldname);
                System.out.println("User deleted successfully!");
                break;
            case 3:
                System.out.print("Enter username to update password: ");
                String userUpdate = scanner.next();
                System.out.print("Enter new password: ");
                String newPassword = scanner.next();
                updateUserPassword(userUpdate, newPassword);
                System.out.println("Password updated successfully!");
                break;
                 case 4:
                    System.out.print("Enter username: ");
                    String userReturn = scanner.next();
                        List<Book> books = getAllBooksFromDatabase();
                        displayAllBooks(books);
                        System.out.println();
                    System.out.print("Enter BookId: ");
                    int bookId = scanner.nextInt();
                    bookReturned(bookId, userReturn);
                        break;
                    case 5:
                        System.out.println("Exiting Library Management System. Goodbye!");
                        scanner.close();
                        System.exit(0);
    
                    default:
                        System.out.println("Invalid choice. Please enter a valid option (1-5).\n");
                }
        } 
        }
        }
    }

