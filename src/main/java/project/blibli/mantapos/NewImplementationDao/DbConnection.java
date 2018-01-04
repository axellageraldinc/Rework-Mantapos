package project.blibli.mantapos.NewImplementationDao;

import java.sql.*;

public class DbConnection {
    private static Connection connection;

    public static Connection openConnection(){
        try{
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:1414/MantaposDatabase",
                    "postgres",
                    "postgres"
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
