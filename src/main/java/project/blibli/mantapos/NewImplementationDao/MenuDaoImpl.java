package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Repository;
import project.blibli.mantapos.Model.Menu;
import project.blibli.mantapos.NewInterfaceDao.MenuDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDaoImpl implements MenuDao {

    private static final String tableMenu = "menu";
    private static final String idMenu = "id_menu";
    private static final String namaMenu = "nama_menu";
    private static final String hargaMenu = "harga_menu";
    private static final String lokasiGambarMenu = "lokasi_gambar_menu";
    private static final String kategoriMenu = "kategori_menu";
    private static final String enabled = "enabled";
    private static final String refTableRestoran = "restoran";
    private static final String idResto = "id_resto";

    private Menu menuMapping(ResultSet resultSet) throws SQLException{
        Menu menu = new Menu();
        menu.setId(resultSet.getInt(idMenu));
        menu.setIdResto(resultSet.getInt(idResto));
        menu.setNama_menu(resultSet.getString(namaMenu));
        menu.setHarga_menu(resultSet.getInt(hargaMenu));
        menu.setKategori_menu(resultSet.getString(kategoriMenu));
        menu.setLokasi_gambar_menu(resultSet.getString(lokasiGambarMenu));
        return menu;
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableMenu +
                        "(" + idMenu + " SERIAL PRIMARY KEY, " +
                        namaMenu + " TEXT NOT NULL, " +
                        hargaMenu + " REAL NOT NULL, " +
                        lokasiGambarMenu + " TEXT NOT NULL, " +
                        kategoriMenu + " TEXT, " +
                        idResto + " INT NOT NULL, " +
                        enabled + " boolean not null default true, " +
                        "CONSTRAINT id_resto_fk FOREIGN KEY (" + idResto + ") REFERENCES " + refTableRestoran + "(id))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(Menu modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + tableMenu +
                        "(" +
                        namaMenu + "," +
                        hargaMenu + "," +
                        lokasiGambarMenu + "," +
                        kategoriMenu + "," +
                        this.idResto + ")" +
                        "VALUES(?,?,?,?,?)"
        );
        preparedStatement.setString(1, modelData.getNama_menu());
        preparedStatement.setInt(2, modelData.getHarga_menu());
        preparedStatement.setString(3, modelData.getLokasi_gambar_menu());
        preparedStatement.setString(4, modelData.getKategori_menu());
        preparedStatement.setInt(5, modelData.getIdResto());
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Menu> getAll(String condition) throws SQLException {
        List<Menu> menuList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM " + tableMenu + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            menuList.add(menuMapping(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return menuList;
    }

    @Override
    public Menu getOne(String condition) throws SQLException {
        Menu menu = new Menu();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM " + tableMenu + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            menu = menuMapping(resultSet);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return menu;
    }

    @Override
    public int getId(String condition) throws SQLException {
        return 0;
    }

    @Override
    public int count(String condition) throws SQLException {
        int count=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM " + tableMenu + " WHERE " + condition
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
    public void update(Menu modelData, String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableMenu + " SET " + namaMenu + "=?, " + hargaMenu + "=?, " + kategoriMenu + "=?, " + lokasiGambarMenu + "=?" +
                        " WHERE " + condition
        );
        preparedStatement.setString(1, modelData.getNama_menu());
        preparedStatement.setInt(2, modelData.getHarga_menu());
        preparedStatement.setString(3, modelData.getKategori_menu());
        preparedStatement.setString(4, modelData.getLokasi_gambar_menu());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void deactivate(String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableMenu + " SET " + enabled + "=? WHERE " + condition
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
                "UPDATE " + tableMenu + " SET " + enabled + "=? WHERE " + condition
        );
        preparedStatement.setBoolean(1, true);
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public int getLastId(String condition) throws SQLException {
        int lastId=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT MAX(" + idMenu + ") FROM " + tableMenu + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            lastId = resultSet.getInt(1);
        }
        return lastId;
    }
}
