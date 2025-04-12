package accommodationfinder.listing;

import accommodationfinder.data.DatabaseConnection;

public class AccommodationDao {

    private final DatabaseConnection dbConnection;

    public AccommodationDao(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }


}
