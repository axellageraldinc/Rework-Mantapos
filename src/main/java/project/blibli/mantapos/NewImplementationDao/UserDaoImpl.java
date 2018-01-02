package project.blibli.mantapos.NewImplementationDao;

import project.blibli.mantapos.Model.User;
import project.blibli.mantapos.NewInterfaceDao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final String tableUser = "users";
    private static final String idUser = "id_user";
    private static final String roleUser = "role";
    private static final String usernameUser = "username";
    private static final String passwordUser = "password";
    private static final String enabled = "enabled";
    private static final String namaLengkap = "nama_lengkap";
    private static final String jenisKelamin = "jenis_kelamin";
    private static final String nomorKtp = "nomor_ktp";
    private static final String nomorTelepon = "nomor_telepon";
    private static final String alamatUser = "alamat";
    private static final String idResto = "id_resto";

    private static final String roleType = "role_type";
    private static final String roleAdmin = "admin";
    private static final String roleOwner = "owner";
    private static final String roleManager = "manager";
    private static final String roleCashier = "cashier";

    private static final String tableUserRoles = "users_roles";
    private static final String refTableRestoran = "restoran";

    private User userMapping(ResultSet resultSet) throws SQLException{
        User user = new User();
        user.setId(resultSet.getInt(idUser));
        user.setIdResto(resultSet.getInt(idResto));
        user.setNamaLengkap(resultSet.getString(namaLengkap));
        user.setPassword(resultSet.getString(passwordUser));
        user.setJenisKelamin(resultSet.getString(jenisKelamin));
        user.setUsername(resultSet.getString(usernameUser));
        user.setNomorKtp(resultSet.getString(nomorKtp));
        user.setNomorTelepon(resultSet.getString(nomorTelepon));
        user.setAlamat(resultSet.getString(alamatUser));
        user.setRole(resultSet.getString(roleUser));
        user.setEnabled(resultSet.getBoolean(enabled));
        return user;
    }

    @Override
    public void createRoleUsers() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "CREATE TYPE " + roleType + " AS ENUM " + "(" +
                            "'" + roleAdmin + "'," +
                            "'" + roleOwner + "'," +
                            "'" + roleManager + "'," +
                            "'" + roleCashier + "'" +
                            ")"
            );
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("user roles sudah ada");
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableUser +
                        "(" +
                        idUser + " SERIAL PRIMARY KEY, " +
                        usernameUser + " TEXT NOT NULL, " +
                        passwordUser + " TEXT NOT NULL, " +
                        enabled + " boolean NOT NULL DEFAULT FALSE, " +
                        namaLengkap + " TEXT NOT NULL, " +
                        jenisKelamin + " TEXT NOT NULL, " +
                        nomorKtp + " TEXT NOT NULL, " +
                        nomorTelepon + " TEXT NOT NULL, " +
                        alamatUser + " TEXT NOT NULL, " +
                        idResto + " INT NOT NULL, " +
                        "UNIQUE (" + usernameUser + ")," +
                        "CONSTRAINT id_restoran_fk FOREIGN KEY (" + idResto + ") REFERENCES " + refTableRestoran + "(id))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void createTableUsersRole() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableUserRoles +
                        "(" +
                        idUser + " INT NOT NULL, " +
                        usernameUser + " TEXT NOT NULL, " +
                        roleUser + " " + roleType + " NOT NULL, " +
                        "UNIQUE (" + usernameUser + "), " +
                        "CONSTRAINT id_user_fk FOREIGN KEY (" + idUser + ")" + "REFERENCES " + tableUser + "(" + idUser + "))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(User modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + tableUser +
                            "(" +
                            usernameUser + "," +
                            passwordUser + "," +
                            enabled + "," +
                            namaLengkap + "," +
                            jenisKelamin + "," +
                            nomorKtp + "," +
                            nomorTelepon + "," +
                            alamatUser + "," +
                            this.idResto +
                            ")" + " VALUES (?,?,?,?,?,?,?,?,?)"
            );
            preparedStatement.setString(1, modelData.getUsername());
            preparedStatement.setString(2, modelData.getPassword());
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, modelData.getNamaLengkap());
            preparedStatement.setString(5, modelData.getJenisKelamin());
            preparedStatement.setString(6, modelData.getNomorKtp());
            preparedStatement.setString(7, modelData.getNomorTelepon());
            preparedStatement.setString(8, modelData.getAlamat());
            preparedStatement.setInt(9, modelData.getIdResto());
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("user sudah ada");
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insertTableUsersRole(User modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + tableUserRoles +
                            "(" +
                            idUser + "," +
                            usernameUser + "," +
                            roleUser +
                            ")" + " VALUES (?,?,?::" + roleType + ")"
            );
            preparedStatement.setInt(1, getId("username='" + modelData.getUsername() + "'"));
            preparedStatement.setString(2, modelData.getUsername());
            preparedStatement.setString(3, modelData.getRole());
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("sudah ada user roles");
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<User> getAll(String condition) throws SQLException {
        List<User> userList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *,users_roles.role FROM " +
                        tableUser + "," + tableUserRoles +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            userList.add(userMapping(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return userList;
    }

    @Override
    public User getOne(String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *, users_roles.role FROM " + tableUser + "," + tableUserRoles +
                        " WHERE " + condition + " AND users_roles.id_user=users.id_user"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        User user = new User();
        while(resultSet.next()){
            user = userMapping(resultSet);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return user;
    }

    @Override
    public int getId(String condition) throws SQLException {
        int idUser=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " + this.idUser + " FROM " + tableUser + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            idUser = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return idUser;
    }

    @Override
    public int count(String condition) throws SQLException {
        int count=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM " + tableUser + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            count = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return count;
    }

    @Override
    public void update(User modelData, String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableUser + " SET " + namaLengkap + "=?" +
                        "," + usernameUser + "=?" +
                        "," + passwordUser + "=?" +
                        "," + nomorTelepon + "=?" +
                        "," + nomorKtp + "=?" +
                        "," + alamatUser + "=?" +
                        "," + jenisKelamin + "=?" +
                        " WHERE " + condition
        );
        preparedStatement.setString(1, modelData.getNamaLengkap());
        preparedStatement.setString(2, modelData.getUsername());
        preparedStatement.setString(3, modelData.getPassword());
        preparedStatement.setString(4, modelData.getNomorTelepon());
        preparedStatement.setString(5, modelData.getNomorKtp());
        preparedStatement.setString(6, modelData.getAlamat());
        preparedStatement.setString(7, modelData.getJenisKelamin());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void updateUserWithoutNewPassword(User modelData, String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableUser + " SET " + namaLengkap + "=?" +
                        "," + usernameUser + "=?" +
                        "," + nomorTelepon + "=?" +
                        "," + nomorKtp + "=?" +
                        "," + alamatUser + "=?" +
                        "," + jenisKelamin + "=?" +
                        " WHERE " + condition
        );
        preparedStatement.setString(1, modelData.getNamaLengkap());
        preparedStatement.setString(2, modelData.getUsername());
        preparedStatement.setString(3, modelData.getNomorTelepon());
        preparedStatement.setString(4, modelData.getNomorKtp());
        preparedStatement.setString(5, modelData.getAlamat());
        preparedStatement.setString(6, modelData.getJenisKelamin());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void updateTableUsersRole(User modelData, String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableUserRoles + " SET " + usernameUser + "=?, " + roleUser + "=?::" + roleType + " WHERE " + condition
        );
        preparedStatement.setString(1, modelData.getUsername());
        preparedStatement.setString(2, modelData.getRole());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void deactivate(String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableUser + " SET " + enabled + "=? WHERE " + condition
        );
        preparedStatement.setBoolean(1, false);
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void activate(String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableUser + " SET " + enabled + "=? WHERE " + condition
        );
        preparedStatement.setBoolean(1, true);
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }
}
