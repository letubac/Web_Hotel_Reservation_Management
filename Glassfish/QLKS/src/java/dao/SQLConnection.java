//https://www.youtube.com/watch?v=ik7QhaRmeqM&t=278s
//https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-ver15
package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
            System.out.println("ket noi");
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QuanLyKhachSanDb;user=admin;password=123");
            System.out.println("ket noi thanh cong");
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

}
