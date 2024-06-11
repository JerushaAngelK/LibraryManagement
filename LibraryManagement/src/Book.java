public class Book {
    private int id;
    private String title;
    private String author;
    private boolean isBorrowed;

    public Book(int id, String title, String author, boolean isBorrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = isBorrowed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean Borrowed) {
        isBorrowed = Borrowed;
    }

    @Override
    public String toString() {
        return title + " by " + author + " - Borrowed: " + isBorrowed;
    }
}