package database;

import collection.CollectionItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBStorable extends CollectionItem {
    static DBRequest deleteAll(String db_name) {
        return new DBRequest("TRUNCATE TABLE "+db_name);
    }

    static DBRequest deleteUserOwned(String db_name, String user) {
        return new DBRequest("DELETE FROM "+db_name+" WHERE \"user\" = ?", user);
    }

    static DBRequest selectAll(String db_name) {
        return new DBRequest("SELECT * FROM "+db_name);
    }

    static DBRequest selectByUser(String db_name, String user) {
        return new DBRequest("SELECT * FROM "+db_name+" WHERE \"user\" = ?", user);
    }

    static DBRequest selectByUserAndID(String db_name, String user, Long id) {
        return new DBRequest("SELECT * FROM "+db_name+" WHERE \"user\" = ? AND \"id\" = ?", user, id);
    }

    static DBRequest selectByID(String db_name, Long id) {
        return new DBRequest("SELECT * FROM "+db_name+" WHERE \"id\" = ?", id);
    }


    String getUser();
    void setUser(String user);

    DBRequest delete();
    DBRequest insert();
    void parse(ResultSet set) throws SQLException;
}
