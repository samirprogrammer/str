package kids.stories.utils.model;

/**
 * Created by ravi on 20/02/18.
 */

public class Note {
    public static final String TABLE_NAME = "stories";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE= "title";
    public static final String COLUMN_DETAIL = "detail";
    public static final String COLUMN_CAT = "cat";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String title;
    private String detail;
    private String cat;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_DETAIL + " TEXT,"
                    + COLUMN_CAT + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Note() {
    }

    public Note(int id, String title, String detail , String cat, String timestamp) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.cat = cat;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public String getCat() {
        return title;
    }

    public void setCat(String Title) {
        this.cat = cat;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
