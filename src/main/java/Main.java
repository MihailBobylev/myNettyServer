import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/univer","postgres", "root");
            c.setAutoCommit(false);
            System.out.println("-- Opened database successfully");
            String sql;

            //--------------- INSERT ROWS ---------------
            stmt = c.createStatement();
            sql = "INSERT INTO corp_e (id, number, schedule) VALUES (1, 325, 'test' );";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            System.out.println("-- Records created successfully");

            //--------------- SELECT DATA ------------------
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT id, number, schedule FROM corp_e WHERE number = '325';");
            while (rs.next()) {
                int id = rs.getInt("id");
                String s2 = rs.getString("number");
                String s3  = rs.getString("schedule");
                System.out.println(id + " " + s2 + " " + s3);
            }
            rs.close();
            stmt.close();
            c.commit();
            System.out.println("-- Operation SELECT done successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("-- All Operations done successfully");
    }
}
