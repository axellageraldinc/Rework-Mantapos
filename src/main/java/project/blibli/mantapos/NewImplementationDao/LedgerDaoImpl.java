package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Repository;
import project.blibli.mantapos.Model.Ledger;
import project.blibli.mantapos.NewInterfaceDao.LedgerDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LedgerDaoImpl implements LedgerDao {

    private static final String tableLedger = "ledger_harian";
    private static final String idLedger = "id";
    private static final String tipe = "tipe";
    private static final String keperluan = "keperluan";
    private static final String biaya = "biaya";
    private static final String dateCreated = "date_created";
    private static final String idResto = "id_resto";
    private static final String refTableResto = "restoran";

    private static final String tipeLedger = "tipe_ledger";
    private static final String tipeDebit = "debit";
    private static final String tipeKredit = "kredit";

    private Ledger ledgerMapping(ResultSet resultSet) throws SQLException{
        Ledger ledger = new Ledger();
        ledger.setId_resto(resultSet.getInt(idResto));
        ledger.setBiaya(resultSet.getInt(biaya));
        ledger.setTipe(resultSet.getString(tipe));
        ledger.setDateCreated(resultSet.getTimestamp(dateCreated));
        ledger.setKeperluan(resultSet.getString(keperluan));
        return ledger;
    }

    private Ledger ledgerMappingBulanan(ResultSet resultSet) throws SQLException{
        Ledger ledger = new Ledger();
        ledger.setMonth(resultSet.getInt(1));
        ledger.setTipe(resultSet.getString(2));
        ledger.setBiaya(resultSet.getInt(3));
        return ledger;
    }

    private Ledger ledgerMappingTahunan(ResultSet resultSet) throws SQLException{
        Ledger ledger = new Ledger();
        ledger.setYear(resultSet.getInt(1));
        ledger.setTipe(resultSet.getString(2));
        ledger.setBiaya(resultSet.getInt(3));
        return ledger;
    }

    @Override
    public void createTipeLedger() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "CREATE TYPE " + tipeLedger + " AS ENUM " + "(" +
                            "'" + tipeDebit + "'," +
                            "'" + tipeKredit + "'" +
                            ")"
            );
            preparedStatement.executeUpdate();
        } catch (Exception ex){
            System.out.println("tipe ledger sudah ada");
        }
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableLedger +
                        "(" +
                        idLedger + " SERIAL PRIMARY KEY, " +
                        idResto + " INT NOT NULL, " +
                        tipe + " " + tipeLedger + " NOT NULL, " +
                        biaya + " INT NOT NULL, " +
                        dateCreated + " TIMESTAMP NOT NULL DEFAULT NOW(), " +
                        keperluan + " TEXT NOT NULL, " +
                        "CONSTRAINT id_resto_fk FOREIGN KEY(" + idResto + ")" + " REFERENCES " + refTableResto + "(id))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(Ledger modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + tableLedger +
                        "(" +
                        this.idResto + "," +
                        tipe + "," +
                        biaya + "," +
                        keperluan + ")" +
                        "VALUES (?,?::" + tipeLedger + ",?,?)"
        );
        preparedStatement.setInt(1, modelData.getId_resto());
        preparedStatement.setString(2, modelData.getTipe());
        preparedStatement.setInt(3, modelData.getBiaya());
        preparedStatement.setString(4, modelData.getKeperluan() + "(" + modelData.getQuantity() + ")");
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<Ledger> getAll(String condition) throws SQLException {
        List<Ledger> ledgerList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT *" + " FROM " + tableLedger +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            ledgerList.add(ledgerMapping(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return ledgerList;
    }

    @Override
    public List<Ledger> getAllBulanan(String condition) throws SQLException {
        List<Ledger> ledgerList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EXTRACT(MONTH FROM " + dateCreated + ")," + tipe + ", SUM(" + biaya + ") FROM " + tableLedger +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            ledgerList.add(ledgerMappingBulanan(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return ledgerList;
    }

    @Override
    public List<Ledger> getAllTahunan(String condition) throws SQLException {
        List<Ledger> ledgerList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EXTRACT(YEAR FROM " + dateCreated + ")," + tipe + ", SUM(" + biaya + ") FROM " + tableLedger +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            ledgerList.add(ledgerMappingTahunan(resultSet));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return ledgerList;
    }

    @Override
    public List<String> getAllBulananUntukDashboard(String condition) throws SQLException {
        List<String> ledgerList = new ArrayList<>();
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EXTRACT(MONTH FROM " + dateCreated + ")," + tipe + ", SUM(" + biaya + ") FROM " + tableLedger +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            ledgerList.add(resultSet.getString("sum"));
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return ledgerList;
    }

    @Override
    public int getTotal(String condition) throws SQLException {
        int totalBiaya=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT SUM(" + biaya + ") FROM " + tableLedger +
                        " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            totalBiaya = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return totalBiaya;
    }

    @Override
    public Ledger getOne(String condition) throws SQLException {
        return null;
    }

    @Override
    public int getId(String condition) throws SQLException {
        int lastId=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT MAX(" + idLedger + ") FROM " + tableLedger + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            lastId = resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return lastId;
    }

    @Override
    public int count(String condition) throws SQLException {
        int count=0;
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM " + tableLedger + " WHERE " + condition
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            count=resultSet.getInt(1);
        }
        DbConnection.closeResultSet(resultSet);
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
        return count;
    }

    @Override
    public void update(Ledger modelData, String condition) throws SQLException {

    }

    @Override
    public void deactivate(String condition) throws SQLException {

    }

    @Override
    public void activate(String condition) throws SQLException {

    }

}
