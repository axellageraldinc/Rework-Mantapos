package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Repository;
import project.blibli.mantapos.Model.Saldo;
import project.blibli.mantapos.NewInterfaceDao.SaldoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class                                                                                                                                                     SaldoAwalDaoImpl implements SaldoDao {

    private static final String tableSaldoAwal = "saldo_awal";
    private static final String idResto = "id_resto";
    private static final String saldoAwal = "saldo_awal";
    private static final String dateCreated = "date_created";
    private static final String refTableResto = "restoran";

    private Saldo saldoMapping(ResultSet resultSet) throws SQLException{
        Saldo saldo = new Saldo();
        saldo.setId_resto(resultSet.getInt(this.idResto));
        saldo.setDateCreated(resultSet.getTimestamp(dateCreated));
        saldo.setSaldo(resultSet.getInt(this.saldoAwal));
        return saldo;
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableSaldoAwal +
                        "(" +
                        idResto + " INT NOT NULL, " +
                        saldoAwal + " REAL NOT NULL, " +
                        dateCreated + " TIMESTAMP NOT NULL DEFAULT NOW(), " +
                        "UNIQUE (" + idResto + "), " +
                        "CONSTRAINT id_resto_fk FOREIGN KEY (" + idResto + ")" + "REFERENCES " + refTableResto + "(id))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(Saldo modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + tableSaldoAwal + "(" + this.idResto + "," + saldoAwal + ")" +
                            "VALUES(?,?)"
            );
            preparedStatement.setInt(1, modelData.getId_resto());
            preparedStatement.setInt(2, modelData.getSaldo());
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("Gagal insert saldo awal : " + ex.toString());
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<Saldo> getAll(String condition) throws SQLException {
        List<Saldo> saldoList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *" + " FROM " + tableSaldoAwal + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            saldoList.add(saldoMapping(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return saldoList;
    }

    @Override
    public Saldo getOne(String condition) throws SQLException {
        Saldo saldo = new Saldo();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *" + " FROM " + tableSaldoAwal + " WHERE " + condition
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
        return 0;
    }

    @Override
    public void update(Saldo modelData, String condition) throws SQLException {

    }

    @Override
    public void deactivate(String condition) throws SQLException {

    }

    @Override
    public void activate(String condition) throws SQLException {

    }

    @Override
    public int getBulanAwal(String condition) throws SQLException {
        return 0;
    }
}
