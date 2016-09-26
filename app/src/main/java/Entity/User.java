package Entity;

/**
 * Created by liangchenzhou on 18/08/16.
 */
public class User {
    private int userId;
    private String userName;
    private String password;
    private String emailAddress;

    public User() {
    }

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User(String userName, String password, String emailAddress) {
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    public User(int userId, String userName, String password, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
