package database;

public class DBRequest {
    private final String sql;
    private final Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public String getSql() {
        return sql;
    }

    public DBRequest(String sql, Object... args) {
        this.sql = sql;
        this.args = args;
    }
}
