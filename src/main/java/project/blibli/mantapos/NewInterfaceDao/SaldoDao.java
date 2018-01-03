package project.blibli.mantapos.NewInterfaceDao;

import project.blibli.mantapos.Model.Saldo;

import java.sql.SQLException;

public interface SaldoDao extends DaoInterface<Saldo, String> {
    int getBulanAwal(String condition) throws SQLException;
}
