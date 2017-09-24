package project.blibli.mantapos.Dao;

import project.blibli.mantapos.Beans_Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserDao {
    private static final String table_name = "users";
    private static final String id = "id";
    private static final String username = "username";
    private static final String password = "password";
    private static final String role = "role";
    private static final String status_user = "status";
    private static final String enabled = "enabled";
    private static final String nama_lengkap = "nama_lengkap";
    private static final String nomor_ktp = "nomor_ktp";
    private static final String nomor_telepon = "nomor_telepon";
    private static final String alamat = "alamat";
    private static final String id_restaurant = "id_restaurant";

    private static final String role_type = "role_type";
    private static final String role_owner = "owner";
    private static final String role_manager = "manager";
    private static final String role_cashier = "cashier";
    private static final String status_type = "status_type";
    private static final String status_aktif = "active";
    private static final String status_tidak_aktif = "inactive";

    private static final String table_role = "user_roles";

    private static final String ref_table_restaurant = "restaurant";

    public static void CreateTable(){
        Connection connection = DbConnection.startConnection();
        PreparedStatement preparedStatementTableUsers = null;
        PreparedStatement preparedStatementRole = null;
        PreparedStatement preparedStatementStatus = null;
        PreparedStatement preparedStatementTableRole = null;
        try{
            //Create ENUM FOR ROLE TYPE : OWNER, MANAGER, CASHIER
            preparedStatementRole = connection.prepareStatement(
                    "CREATE TYPE " + role_type + " AS ENUM " + "(" +
                            "'" + role_owner + "'," +
                            "'" + role_manager + "'," +
                            "'" + role_cashier + "'" +
                            ")"
            );
            //Create ENUM FOR STATUS TYPE : ACTIVE (Still hired) or INACTIVE (Not hired anymore)
//            preparedStatementStatus = connection.prepareStatement(
//                    "CREATE TYPE " + status_type + " AS ENUM " + "(" +
//                            "'" + status_aktif + "'," +
//                            "'" + status_tidak_aktif + "'" +
//                            ")"
//            );
            //Create TABLE user
            preparedStatementTableUsers = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + table_name +
                            "(" +
                            id + " SERIAL PRIMARY KEY, " +
                            username + " TEXT NOT NULL, " +
                            password + " TEXT NOT NULL, " +
                            enabled + " boolean NOT NULL DEFAULT FALSE, " +
                            nama_lengkap + " TEXT NOT NULL, " +
                            nomor_ktp + " TEXT NOT NULL, " +
                            nomor_telepon + " TEXT NOT NULL, " +
                            alamat + " TEXT NOT NULL, " +
                            id_restaurant + " INT NOT NULL, " +
                            "CONSTRAINT id_restaurant_FK FOREIGN KEY (" + id_restaurant + ") REFERENCES " + ref_table_restaurant + "(" + id + "))"
            );
            preparedStatementTableRole = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + table_role +
                            "(" +
                            username + " TEXT NOT NULL, " +
                            role + " " + role_type + " NOT NULL)"
            );
            preparedStatementRole.executeUpdate();
//            preparedStatementStatus.executeUpdate();
            preparedStatementTableRole.executeUpdate();
            preparedStatementTableUsers.executeUpdate();
            System.out.println("Create table user success!");
        } catch (Exception ex){
            System.out.println("Create table user failed : " + ex.toString());
        } finally {
            DbConnection.ClosePreparedStatement(preparedStatementTableUsers);
            DbConnection.ClosePreparedStatement(preparedStatementRole);
//            DbConnection.ClosePreparedStatement(preparedStatementStatus);
            DbConnection.ClosePreparedStatement(preparedStatementTableRole);
            DbConnection.CloseConnection(connection);
        }
    }

    public static int Insert(User user){
        int status=0;
        Connection connection = DbConnection.startConnection();
        PreparedStatement preparedStatementTableUsers = null;
        PreparedStatement preparedStatementTableRole = null;
        try{
            preparedStatementTableUsers = connection.prepareStatement(
                    "INSERT INTO " + table_name +
                            "(" +
                            username + "," +
                            password + "," +
                            enabled + "," +
                            nama_lengkap + "," +
                            nomor_ktp + "," +
                            nomor_telepon + "," +
                            alamat + "," +
                            id_restaurant +
                            ")" + " VALUES (?,?,?,?,?,?,?,?)"
            );
            preparedStatementTableUsers.setString(1, user.getUsername());
            preparedStatementTableUsers.setString(2, user.getPassword());
            preparedStatementTableUsers.setBoolean(3, true);
            preparedStatementTableUsers.setString(4, user.getNama_lengkap());
            preparedStatementTableUsers.setString(5, user.getNomor_ktp());
            preparedStatementTableUsers.setString(6, user.getNomor_telepon());
            preparedStatementTableUsers.setString(7, user.getAlamat());
            preparedStatementTableUsers.setInt(8, user.getId_restaurant());
            status = preparedStatementTableUsers.executeUpdate();
            if (status==1)
                System.out.println("Insert into table users success!");
            preparedStatementTableRole = connection.prepareStatement(
                    "INSERT INTO " + table_role +
                            "(" +
                            username + "," +
                            role +
                            ")" + " VALUES (?,?::" +role_type + ")"
            );
            preparedStatementTableRole.setString(1, user.getUsername());
            preparedStatementTableRole.setString(2, user.getRole());
            int status2 = preparedStatementTableRole.executeUpdate();
            if(status2==1)
                System.out.println("Insert into table roles success!");
        } catch (Exception ex){
            System.out.println("Insert into users failed : " + ex.toString());
        } finally {
            DbConnection.ClosePreparedStatement(preparedStatementTableUsers);
            DbConnection.ClosePreparedStatement(preparedStatementTableRole);
            DbConnection.CloseConnection(connection);
        }
        return status;
    }
}