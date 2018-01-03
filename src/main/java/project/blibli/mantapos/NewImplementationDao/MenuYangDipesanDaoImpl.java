package project.blibli.mantapos.NewImplementationDao;

import org.springframework.stereotype.Repository;
import project.blibli.mantapos.Model.OrderedMenu;
import project.blibli.mantapos.NewInterfaceDao.MenuYangDipesanDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MenuYangDipesanDaoImpl implements MenuYangDipesanDao {

    private static final String tableMenuYangDipesan = "menu_yang_dipesan";
    private static final String idOrder = "id_order";
    private static final String idMenu = "id_menu";
    private static final String qty = "quantity";
    private static final String refTableOrder = "ledger_harian";
    private static final String refTableMenu = "menu";

    @Override
    public void createTable() throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + tableMenuYangDipesan +
                        "(" +
                        idOrder + " INT NOT NULL, " +
                        idMenu + " INT NOT NULL, " +
                        qty + " INT NOT NULL, " +
                        "CONSTRAINT id_order_fk FOREIGN KEY (" + idOrder + ") REFERENCES " + refTableOrder + "(id)," +
                        "CONSTRAINT id_menu_fk FOREIGN KEY (" + idMenu + ") REFERENCES " + refTableMenu + "(id_menu))"
        );
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public void insert(OrderedMenu modelData) throws SQLException {
        Connection connection = DbConnection.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + tableMenuYangDipesan +
                        "(" +
                        this.idOrder + "," +
                        this.idMenu + "," +
                        this.qty + ")" +
                        " VALUES (?,?,?)"
        );
        preparedStatement.setInt(1, modelData.getIdOrder());
        preparedStatement.setInt(2, modelData.getIdMenu());
        preparedStatement.setInt(3, modelData.getQty());
        preparedStatement.executeUpdate();
        DbConnection.closePreparedStatement(preparedStatement);
        DbConnection.closeConnection(connection);
    }

    @Override
    public List<OrderedMenu> getAll(String condition) throws SQLException {
        return null;
    }

    @Override
    public OrderedMenu getOne(String condition) throws SQLException {
        return null;
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
    public void update(OrderedMenu modelData, String condition) throws SQLException {

    }

    @Override
    public void deactivate(String condition) throws SQLException {

    }

    @Override
    public void activate(String condition) throws SQLException {

    }
}
