package project.blibli.mantapos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.*;
import project.blibli.mantapos.NewImplementationDao.*;
import project.blibli.mantapos.NewInterfaceDao.SaldoDao;
import project.blibli.mantapos.Service.CashierService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class MantaposApplication {

	static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MantaposApplication.class, args);

		RestoranDaoImpl restoranDao = new RestoranDaoImpl();
		restoranDao.createTable();

		UserDaoImpl userDao = new UserDaoImpl();
		userDao.createRoleUsers();
		userDao.createTable();
		userDao.createTableUsersRole();

		Restoran restoran = new Restoran("ADMIN", "ADMIN");
		restoranDao.insert(restoran);

		restoran = new Restoran("Damai Foody", "Lumajang");
		restoranDao.insert(restoran);

		User user = new User(
				"axell", bCryptPasswordEncoder.encode("axell123"), "admin", "Axellageraldinc",
				"123", "456", "jogja", "L", GetIdResto.getIdRestoBasedOnNamaResto("ADMIN"), true
		);
		userDao.insert(user);
		userDao.insertTableUsersRole(user);
		user = new User(
				"damai", bCryptPasswordEncoder.encode("damai123"), "owner", "Damai Marisa Bachri",
				"123", "456", "Lumajang nun jauh disana", "P", 2, true
		);
		userDao.insert(user);
		userDao.insertTableUsersRole(user);

		MenuDaoImpl menuDao = new MenuDaoImpl();
		menuDao.createTable();

		Menu menu = new Menu(2, "Soto Sapi", "/images/1.jpg", "food", 12000);
		menuDao.insert(menu);
		menu = new Menu(2, "Soto Ayam", "images/2.jpg", "food", 9000);
		menuDao.insert(menu);
		menu = new Menu(2, "Es Teh", "images/3.png", "drink", 3000);
		menuDao.insert(menu);

		SaldoAwalDaoImpl saldoAwalDao = new SaldoAwalDaoImpl();
		saldoAwalDao.createTable();

		Saldo saldo = new Saldo(2, 5000, "awal");
		saldoAwalDao.insert(saldo);

		SaldoAkhirDaoImpl saldoAkhirDao = new SaldoAkhirDaoImpl();
		saldoAkhirDao.createTable();

		LedgerDaoImpl ledgerDao = new LedgerDaoImpl();
		ledgerDao.createTipeLedger();
		ledgerDao.createTable();
		MenuYangDipesanDaoImpl menuYangDipesanDao = new MenuYangDipesanDaoImpl();
		menuYangDipesanDao.createTable();

		Ledger ledger = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date;
		Timestamp timestamp;
		String customDate;
		Random random = new Random();
		int biaya;
		int i;
		for (i=1; i<13; i++){
			for(int j=1; j<13; j++) {
				biaya = random.nextInt();
				if(biaya%2==0){
					ledger = new Ledger(2, 9000, "penjualan menu", "debit");
				} else{
					ledger = new Ledger(2, 3000, "pengeluaran", "kredit");
				}
				customDate = "2018-" + i + "-" + j + " 10:00:00";
				date = sdf.parse(customDate);
				timestamp = new Timestamp(date.getTime());
				ledger.setDateCreated(timestamp);
				ledgerDao.insert(ledger);
				updateSaldoAkhir(i, 2018, 2, ledgerDao, saldoAkhirDao, saldoAwalDao);
			}
			System.out.println("nilai i : " + i);
		}

	}

	private static void updateSaldoAkhir(int bulan,
										 int tahun,
										 int idResto,
										 LedgerDaoImpl ledgerDao,
										 SaldoAkhirDaoImpl saldoAkhirDao,
										 SaldoAwalDaoImpl saldoAwalDao) throws SQLException {
		Saldo saldo = new Saldo();
		saldo.setId_resto(idResto);
		int saldoAwal=0;
		if(bulan==1){
			saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
					" AND EXTRACT(MONTH FROM date_created)=12 AND EXTRACT(YEAR from date_created)=" + (tahun-1)).getSaldo();
		} else {
			saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
					" AND EXTRACT(MONTH FROM date_created)=" + (bulan-1) + " AND EXTRACT(YEAR FROM date_created)=" + tahun).getSaldo();
		}
		if(saldoAwal==0){
			saldoAwal = saldoAwalDao.getOne("id_resto=" + idResto).getSaldo();
		}
		String condition = "id_resto=" + idResto +
				" AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun; //between 2018-01-01 00:00:00 AND 2018-01-31 23:59:59
		int totalPemasukkanBulanIni = ledgerDao.getTotal(condition + " AND tipe='debit'");
		int totalPengeluaranBulanIni = ledgerDao.getTotal(condition + " AND tipe='kredit'");
		saldo.setSaldo(saldoAwal + totalPemasukkanBulanIni - totalPengeluaranBulanIni);
		if(saldoAkhirDao.count("id_resto=" + idResto)==0){
			saldoAkhirDao.insert(saldo);
		} else{
			saldoAkhirDao.update(saldo, "id_resto=" + idResto +
					" AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun);
		}
	}

}
