package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DbConnection {
    private static Connection connection;

    public static Connection openConnection(){
        try{
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://ec2-54-235-165-114.compute-1.amazonaws.com:5432/d307isekgs7dgp",
                    "lqfpawnvxitkpg",
                    "f7432f942755985fac374c24aa648d2eece3e5bba9e71642aa129bb69ae7899d"
            );
            System.out.println("DB Connection opened successfully!");
        } catch (Exception ex){
            System.out.println("Error start connection DB : " + ex.toString());
        }
        return connection;
    }

    public static void closeConnection(Connection connection){
        if(connection==null){
            return;
        } else{
            try {
                connection.close();
                System.out.println("DB Connection closed successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed closing connection : " + e.toString());
            }
        }
    }
    public static void closePreparedStatement(PreparedStatement preparedStatement){
        if(preparedStatement==null){
            return;
        } else{
            try {
                preparedStatement.close();
                System.out.println("Prepared Statement closed successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed closing prepared statement : " + e.toString());
            }
        }
    }
    public static void closeResultSet(ResultSet resultSet){
        if(resultSet==null){
            return;
        } else{
            try {
                resultSet.close();
                System.out.println("Result Set closed successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed closing result set : " + e.toString());
            }
        }
    }

}
