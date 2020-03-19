package ro.jademy.contactlist.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {

    public Connection getConnection() throws SQLException {

        Connection conn = null;

        try (InputStream input = JDBCUtil.class.getResourceAsStream("/db.properties")) {

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String port = props.getProperty("db.port");
            String database = props.getProperty("db.database");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            conn = DriverManager.getConnection(("jdbc:mysql://" + url + ":" + port + "/" + database), user, password);
//            conn.setAutoCommit(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
