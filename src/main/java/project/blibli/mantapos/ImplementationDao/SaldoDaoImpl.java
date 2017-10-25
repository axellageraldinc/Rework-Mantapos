package project.blibli.mantapos.ImplementationDao;

import org.springframework.jdbc.core.JdbcTemplate;
import project.blibli.mantapos.Config.DataSourceConfig;
import project.blibli.mantapos.InterfaceDao.SaldoDao;

import java.time.LocalDate;

public class SaldoDaoImpl implements SaldoDao {
    private static final String table_name = "saldo_awal";
    private static final String id_resto = "id_resto";
    private static final String saldo_awal = "saldo_awal";
    private static final String month = "month";
    private static final String year = "year";
    private static final String ref_table_resto = "restoran";

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public SaldoDaoImpl(){
        jdbcTemplate.setDataSource(DataSourceConfig.dataSource());
    }

    @Override
    public void CreateTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table_name +
                "(" +
                id_resto + " INT NOT NULL, " +
                saldo_awal + " REAL NOT NULL, " +
                month + " INT NOT NULL, " +
                year + " INT NOT NULL, " +
                "CONSTRAINT id_resto_fk FOREIGN KEY (" + id_resto + ")" + "REFERENCES " + ref_table_resto + "(id))";
        try{
            jdbcTemplate.execute(query);
        } catch (Exception ex){
            System.out.println("Gagal create table saldo_awal : " + ex.toString());
        }
    }

    @Override
    public void AddSaldoAwal(int id_restoo, int saldoAwal) {
        String query = "INSERT INTO " + table_name + "(" + id_resto + "," + saldo_awal + "," + month + "," + year + ")" +
                "VALUES(?,?,?,?)";
        try{
            jdbcTemplate.update(query, new Object[] {id_restoo, saldoAwal, LocalDate.now().getMonthValue(), LocalDate.now().getYear()});
        } catch (Exception ex){
            System.out.println("Gagal add saldo awal : " + ex.toString());
        }
    }

    @Override
    public int getSaldoAwal(int id_restoo, int monthh, int yearr) {
        int saldo_awall=0;
        String query = "SELECT " + saldo_awal + " FROM " + table_name + " WHERE " + id_resto + "=? AND " + month + "=? AND " + year + "=?";
        try{
            saldo_awall = jdbcTemplate.queryForObject(query, new Object[] {id_restoo, monthh, yearr}, Integer.class);
        } catch (Exception ex){
            System.out.println("Gagal get saldo awal : " + ex.toString());
        }
        return saldo_awall;
    }
}