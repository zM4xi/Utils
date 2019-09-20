import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.sql.*;

/**
 * This class is a util class for simpler interaction with a mysql database server.
 * <br>
 * How to use:
 * <pre>
 *   DatabaseUtil databaseUtil = new DatabaseUtil("localhost", "3306", "initialDatabase", "username", "password");
 *
 *   boolean hasResult = databaseUtil.execute("CREATE TABLE table (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, email VARCHAR(50))") == 1;
 *   int manipulationsInsert = databaseUtil.execute("INSERT INTO table VALUES (?, ?)", 1, "default@email.com");
 *   int manipulationsUpdate = databaseUtil.execute("UPDATE table SET email=?", 1, "john@doe.com");
 *
 *   Table<Integer, String, Object> result = databaseUtil.query("SELECT TOP 1 email FROM table WHERE id=?", 1);
 *
 *   if(result.isEmpty())
 *       return;
 *
 *   String email = String.valueOf(result.row(1).get("email"));
 * </pre>
 * <br>
 * This class uses <a href="https://github.com/google/guava">Google Guava Java Library</a> for the {@link #query(String, Object...)} method so the following dependency is needed
 * <br>
 *
 * <pre>
 * {@code
 *     <!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
 *     <dependency>
 *         <groupId>com.google.guava</groupId>
 *         <artifactId>guava</artifactId>
 *         <version>28.1-jre</version>
 *     </dependency>
 * }
 * </pre>
 *
 * @author IDK_WHO_AM_I
 * @version 0.0.1
 */
public class DatabaseUtil {

    private Connection connection;

    /**
     * Initializes a {@link java.sql.Connection} object with the provided credentials
     *
     * @param host     mysql server host address
     * @param port     mysql server port (default: 3306)
     * @param database initial database the connection uses
     * @param username mysql database username
     * @param password mysql database password
     */
    public DatabaseUtil(String host, String port, String database, String username, String password) {
        if (port.isEmpty())
            port = "3306";
        try {
            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&create=true", host, port, database), username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the current connection is active
     *
     * @return true if the action didn't die yet - false if it died
     */
    public boolean isConnectionActive() {
        return this.connection != null;
    }

    /**
     * Executes a sql statement
     *
     * @param sql        the sql statement to execute
     * @param parameters the parameters in the order they are inserted into the statement
     * @return a {@link Integer} that represents the count of manipulations or the result of the executed statement (see {@link java.sql.PreparedStatement#executeUpdate()} and {@link java.sql.Statement#execute(String)})
     */
    public int execute(String sql, Object... parameters) {
        if (parameters == null) {
            try (Statement st = this.connection.createStatement()) {
                return st.execute(sql) ? 1 : 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
                for (int i = 0; i < parameters.length; i++) {
                    ps.setObject(i + 1, parameters[i]);
                }
                return ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Runs a sql query statement over the connected database
     *
     * @param sql        the sql statement to run
     * @param parameters the parameters in the order they are inserted into the statement
     * @return a {@link com.google.common.collect.Table} containing the data the query returned
     */
    public Table<Integer, String, Object> query(String sql, Object... parameters) {
        if (parameters == null)
            return null;
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                ps.setObject(i + 1, parameters[i]);
            }
            ResultSet rs = ps.executeQuery();
            HashBasedTable<Integer, String, Object> table = HashBasedTable.create();
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i < metaData.getColumnCount(); i++) {
                    String column = metaData.getColumnName(i);
                    table.put(i, column, rs.getObject(column));
                }
            }
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.connection = null;
    }


}
