package accommodationfinder.data;

import accommodationfinder.auth.User;
import accommodationfinder.data.DatabaseConnection;

public class UserDao {
    private final DatabaseConnection dbConnection;

    public UserDao(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
}
