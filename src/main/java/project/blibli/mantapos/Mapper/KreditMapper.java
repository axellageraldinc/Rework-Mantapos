package project.blibli.mantapos.Mapper;

import org.springframework.jdbc.core.RowMapper;
import project.blibli.mantapos.Beans_Model.Ledger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KreditMapper implements RowMapper<Ledger> {
    @Override
    public Ledger mapRow(ResultSet resultSet, int i) throws SQLException {
        Ledger ledger = new Ledger();
        ledger.setWaktu(resultSet.getString("waktu"));
        ledger.setKeperluan(resultSet.getString("keperluan"));
        ledger.setBiaya(resultSet.getInt("biaya"));
        return ledger;
    }
}
