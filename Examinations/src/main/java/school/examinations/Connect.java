package school.examinations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Connect {
    public static Connection connection(){
        Connection con;
        String url = "jdbc:mysql80://localhost:3306/examinations";
        String user = "root";
        String password = "175960wiz";
        try{
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }
}
