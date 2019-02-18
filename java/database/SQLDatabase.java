import lombok.Getter;

import java.sql.*;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VariousSQLDatabase {

    /**
     * Used to connect to an Oracle database instance.
     * <-- https://mvnrepository.com/artifact/com.oracle/ojdbc14 -->
     * <dependency>
     *     <groupId>com.oracle</groupId>
     *     <artifactId>ojdbc14</artifactId>
     *     <version>10.2.0.4.0</version>
     * </dependency>
     */
    static class Oracle extends Connectable {
        /**
         * @param driverType possible: thin, oci, kprb
         * @param host localhost or remote server
         * @param port default 1521 or custom port
         * @param serviceName the database instance name
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public Oracle(String driverType, String host, String port, String serviceName, String username, String password) {
            this.connectionString = String.format("jdbc:oracle:%s:%s/%s@%s:%s/%s", driverType, username, password, host, port, serviceName);
        }
    }

    /**
     * Used to connect to an SQLite database file.
     * <-- https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc -->
     * <dependency>
     *     <groupId>org.xerial</groupId>
     *     <artifactId>sqlite-jdbc</artifactId>
     *     <version>3.25.2</version>
     * </dependency>
     */
    static class SQLite extends Connectable {
        /**
         * @param fileName the path and filename to the sqlite file
         */
        public SQLite(String fileName) {
            this.connectionString = String.format("jdbc:sqlite:%s", fileName);
        }
    }

    /**
     * Used to connect to an MongoDB database instance.
     * <-- https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver -->
     * <dependency>
     *     <groupId>org.mongodb</groupId>
     *     <artifactId>mongo-java-driver</artifactId>
     *     <version>3.10.0</version>
     * </dependency>
     */
    static class MongoDB extends Connectable {
        /**
         * @param host localhost or remote server
         * @param port default 27017 or custom port
         * @param database initial database to connect to
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public MongoDB(String host, String port, String database, String username, String password) {
            this.connectionString = String.format("jdbc:mongodb://%s:%s@%s:%s/%s", username, password, host, port, database);
        }
    }

    /**
     * Used to connect to an MariaDB database instance.
     * <-- https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client -->
     * <dependency>
     *     <groupId>org.mariadb.jdbc</groupId>
     *     <artifactId>mariadb-java-client</artifactId>
     *     <version>2.4.0</version>
     * </dependency>
     */
    static class MariaDB extends Connectable {
        /**
         * @param host localhost or remote server
         * @param port default 3306 or custom port
         * @param database initial database to connect to
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public MariaDB(String host, String port, String database, String username, String password) {
            this.connectionString = String.format("jdbc:mariadb://%s:%s/%s?user=%s&password=%s", host, port, database, username, password);
        }
    }

    /**
     * Used to connect to an PostgreSQL database instance.
     * <-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
     * <dependency>
     *     <groupId>org.postgresql</groupId>
     *     <artifactId>postgresql</artifactId>
     *     <version>42.2.5</version>
     * </dependency>
     */
    static class PostgreSQL extends Connectable{
        /**
         * @param host localhost or remote server
         * @param port default 5432 or custom port
         * @param database initial database to connect to
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public PostgreSQL(String host, String port, String database, String username, String password) {
            this.connectionString = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s", host, port, database, username, password);
        }
    }

    /**
     * Used to connect to an Microsoft SQL database instance.
     * <-- https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc -->
     * <dependency>
     *     <groupId>com.microsoft.sqlserver</groupId>
     *     <artifactId>mssql-jdbc</artifactId>
     *     <version>7.2.0.jre11</version>
     *     <scope>test</scope>
     * </dependency>
     */
    static class MSSQL extends Connectable {
        /**
         * @param host localhost or remote server
         * @param port default 1433 or custom port
         * @param database initial database to connect to
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public MSSQL(String host, String port, String database, String username, String password) {
            this.connectionString = String.format("jdbc:sqlserver://%s:%s;database=%s;user=%s;password=%s", host, port, database, username, password);
        }
    }

    /**
     * Used to connect to an MySQL database instance.
     * <-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
     * <dependency>
     *     <groupId>mysql</groupId>
     *     <artifactId>mysql-connector-java</artifactId>
     *     <version>8.0.15</version>
     * </dependency>
     */
    static class MySQL extends Connectable {
        /**
         * @param host localhost or remote server
         * @param port default 3306 or custom port
         * @param database initial database to connect to
         * @param username username to authenticate on the database
         * @param password password to authenticate on the database
         */
        public MySQL(String host, String port, String database, String username, String password) {
            this.connectionString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&useSSL=false&useTimezone=true&serverTimezone=GMT", host, port, database, username, password);
        }
    }

    private static abstract class Connectable {

        @Getter
        private final Logger LOGGER = Logger.getLogger("VariousSQLDatabase");
        @Getter
        private Connection connection;
        @Getter
        private AtomicLong instanceId = new AtomicLong();
        @Getter
        protected String connectionString;

        public void openConnection() {
            try {
                this.connection = DriverManager.getConnection(connectionString);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "There was an error trying to connect to the database using following connection string\n{0}\n{1}", new Object[] {connectionString, e.getMessage()});
            }
        }

        public boolean isConneted() {
            return connection != null;
        }

        public void addTable(String tableName, boolean checkIfExists, String ... columns) {
            String sql = "";
            try {
                sql = String.format("CREATE TABLE %s " + ((checkIfExists) ? "IF NOT EXISTS" : "") + "(%s)", tableName, String.join(", ", columns));
                this.connection.createStatement().execute(sql);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "There was an error trying to create the table {0}. Following sql statement was not successfull executed:\n{1}\n{2}", new Object[] {tableName, sql, e.getMessage()});
            }
        }

        public int update(String sql, Object ... params) {
            PreparedStatement ps = null;
            try {
                if(params != null) {
                    ps = this.connection.prepareStatement(sql);
                    int index = 1;
                    for(Object param : params) {
                        ps.setObject(++index, param);
                    }
                    return ps.executeUpdate();
                } else {
                    return this.connection.createStatement().executeUpdate(sql);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "There was an error executing an sql execution.\n{0}", e.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "There was an error trying to close the query statement\n{0}", e.getMessage());
                }
            }
            return 0;
        }

        public ResultSet query(String sql, Object ... params) {
            PreparedStatement ps = null;
            try {
                if (params != null) {
                    ps = this.connection.prepareStatement(sql);
                    int index = 1;
                    for (Object param : params) {
                        ps.setObject(++index, param);
                    }
                    return ps.executeQuery();
                } else {
                    return this.connection.createStatement().executeQuery(sql);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "There was an error executing an sql query.\n{0}", e.getMessage());
            } /*finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "There was an error trying to close the query statement\n{0}", e.getMessage());
                }
            }*/
            return null;
        }

        public void closeConnection() {
            try {
                this.connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "There was an error trying to close the database connection\n{0}", e.getMessage());
            }
        }

    }

}
