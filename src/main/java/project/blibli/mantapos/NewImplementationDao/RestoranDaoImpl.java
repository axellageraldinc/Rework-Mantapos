package project.blibli.mantapos.NewImplementationDao;

import project.blibli.mantapos.Model.Restoran;
import project.blibli.mantapos.NewInterfaceDao.RestoranDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestoranDaoImpl implements RestoranDao {

    private static final String tableRestoran = "restoran";
    private static final String idResto = "id";
    private static final String namaResto = "nama_restoran";
    private static final String alamatResto = "alamat_resto";

    private static final String idUser = "id_user";
    private static final String usernameUser = "username";
    private static final String passwordUser = "password";
    private static final String enabled = "enabled";
    private static final String namaLengkap = "nama_lengkap";
    private static final String jenisKelamin = "jenis_kelamin";
    private static final String nomorKtp = "nomor_ktp";
    private static final String nomorTelepon = "nomor_telepon";
    private static final String alamatUser = "alamat";

    private Restoran restoranAndUsersMapping(ResultSet resultSet) throws SQLException   {
        Restoran restoran = new Restoran();
        restoran.setId(resultSet.getInt(idResto));
        restoran.setNamaResto(resultSet.getString(namaResto));
        restoran.setEnabled(resultSet.getBoolean(enabled));
        restoran.setLokasiResto(resultSet.getString(alamatResto));
        restoran.setIdUser(resultSet.getInt(idUser));
        restoran.setNamaLengkap(resultSet.getString(namaLengkap));
        restoran.setJenisKelamin(resultSet.getString(jenisKelamin));
        restoran.setUsername(resultSet.getString(usernameUser));
        restoran.setPassword(resultSet.getString(passwordUser));
        restoran.setNomorKtp(resultSet.getString(nomorKtp));
        restoran.setNomorTelepon(resultSet.getString(nomorTelepon));
        restoran.setAlamat(resultSet.getString(alamatUser));
        return restoran;
    }

    private Restoran restoranOnlyMapping(ResultSet resultSet) throws SQLException{
        Restoran restoran = new Restoran();
        restoran.setId(resultSet.getInt(idResto));
        restoran.setNamaResto(resultSet.getString(namaResto));
        restoran.setAlamat(resultSet.getString(alamatResto));
        return restoran;
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableRestoran + "(" +
                        idResto + " SERIAL PRIMARY KEY, " +
                        namaResto + " TEXT NOT NULL, " +
                        alamatResto + " TEXT NOT NULL, " +
                        "UNIQUE (" + namaResto + "))" //TODO : Kenapa nama resto gakmau ter-constraint unique (nama resto sama masih bisa)
                //kenapa nama resto dibuat unique???
                //supaya waktu get ID resto berdasarkan nama restoran, return nama resto nya cuma 1 aja
                //jadi gimana kalau ada 2 restoran yang sama yg menggunakan mantapos?
                //misal Afui, ya dibuat Afui 1 dan Afui 2. Atau Afui babarsari dan Afui jakal
                //pokoknya namanya dibuat unique
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(Restoran modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + tableRestoran + "(" + namaResto + "," + alamatResto + ")" +
                            "VALUES(?,?)"
            );
            preparedStatement.setString(1, modelData.getNamaResto());
            preparedStatement.setString(2, modelData.getLokasiResto());
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("sudah ada restoran");
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<Restoran> getAll(String condition) throws SQLException {
        List<Restoran> restoranList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT" +
                        " *, " + //semua field restoran
                        " *" + //semua field user dari table users
                        " FROM restoran, users, users_roles" +
                        " WHERE users.id_resto=restoran.id " +
                        "AND users_roles.id_user=users.id_user " +
                        "AND " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            restoranList.add(restoranAndUsersMapping(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return restoranList;
    }

    @Override
    public Restoran getOne(String condition) throws SQLException {
        Restoran restoran = new Restoran();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM " + tableRestoran + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            restoran = restoranOnlyMapping(resultSet);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return restoran;
    }

    @Override
    public int getId(String condition) throws SQLException {
        int idRestoran=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " + this.idResto +
                        " FROM " + tableRestoran + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            idRestoran = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return idRestoran;
    }

    @Override
    public int getIdBerdasarkanUsernameUser(String condition) throws SQLException {
        int idRestoran=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT users.id_resto" +
                        " FROM users, restoran WHERE users.id_resto=restoran.id AND " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            idRestoran = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return idRestoran;
    }

    @Override
    public int count(String condition) throws SQLException {
        int count=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM " + tableRestoran + " WHERE " + namaResto + " NOT LIKE ?"
        );
        preparedStatement.setString(1, "ADMIN");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            count = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return count;
    }

    // TODO : perlukah update informasi restoran?
    @Override
    public void update(Restoran modelData, String condition) throws SQLException {

    }

    // Tidak perlu nonaktifkan restoran, cukup owner-nya saja.
    @Override
    public void deactivate(String condition) throws SQLException {

    }

    // Tidak perlu aktifkan restoran, cukup owner-nya saja.
    @Override
    public void activate(String condition) throws SQLException {

    }

}
