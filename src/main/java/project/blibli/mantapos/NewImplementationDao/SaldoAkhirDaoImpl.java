package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Repository;
import project.blibli.mantapos.Model.Saldo;
import project.blibli.mantapos.NewInterfaceDao.SaldoDao;

import java.sql.*;
import java.util.List;

@Repository
public class SaldoAkhirDaoImpl implements SaldoDao {

    private static final String tableSaldoAkhir = "saldo_akhir";
    private static final String idResto = "id_resto";
    private static final String saldoAkhir = "saldo_akhir";
    private static final String dateCreated = "date_created";
    private static final String refTableResto = "restoran";

    private Saldo saldoMapping(ResultSet resultSet) throws SQLException{
        Saldo saldo = new Saldo();
        saldo.setId_resto(resultSet.getInt(this.idResto));
        saldo.setDateCreated(resultSet.getTimestamp(dateCreated));
        saldo.setSaldo(resultSet.getInt(this.saldoAkhir));
        return saldo;
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableSaldoAkhir +
                        "(" +
                        idResto + " INT NOT NULL, " +
                        saldoAkhir + " REAL NOT NULL, " +
                        dateCreated + " TIMESTAMP NOT NULL DEFAULT NOW(), " +
                        "CONSTRAINT id_resto_fk FOREIGN KEY (" + idResto + ")" + "REFERENCES " + refTableResto + "(id))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(Saldo modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + tableSaldoAkhir + "(" + this.idResto + "," + saldoAkhir + "," + dateCreated + ")" +
                        "VALUES(?,?,?)"
        );
        preparedStatement.setInt(1, modelData.getId_resto());
        preparedStatement.setInt(2, modelData.getSaldo());
        preparedStatement.setTimestamp(3, modelData.getDateCreated());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<Saldo> getAll(String condition) throws SQLException {
        return null;
    }

    @Override
    public Saldo getOne(String condition) throws SQLException {
        Saldo saldo = new Saldo();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *" + " FROM " + tableSaldoAkhir + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            saldo = saldoMapping(resultSet);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return saldo;
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
                "SELECT COUNT(*) FROM " + tableSaldoAkhir + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return count;
    }

    @Override
    public void update(Saldo modelData, String condition) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE " + tableSaldoAkhir + " SET " + saldoAkhir + "=? WHERE " + condition
        );
        preparedStatement.setInt(1, modelData.getSaldo());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void deactivate(String condition) throws SQLException {

    }

    @Override
    public void activate(String condition) throws SQLException {

    }

    public int getBulanAwal(String condition) throws SQLException {
        int bulanAwal = 0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT MIN(EXTRACT(MONTH FROM " + dateCreated + ")) FROM " + tableSaldoAkhir + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            bulanAwal = resultSet.getInt(1);
        }
        return bulanAwal;
    }

}
