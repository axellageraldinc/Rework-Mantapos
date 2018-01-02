package project.blibli.mantapos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.*;
import project.blibli.mantapos.NewImplementationDao.*;
import java.util.List;

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

		User user = new User(
				"axell", bCryptPasswordEncoder.encode("axell123"), "admin", "Axellageraldinc",
				"123", "456", "jogja", "L", GetIdResto.getIdRestoBasedOnNamaResto("ADMIN"), true
		);
		userDao.insert(user);
		userDao.insertTableUsersRole(user);

		MenuDaoImpl menuDao = new MenuDaoImpl();
		menuDao.createTable();


		SaldoAwalDaoImpl saldoAwalDao = new SaldoAwalDaoImpl();
		saldoAwalDao.createTable();
		SaldoAkhirDaoImpl saldoAkhirDao = new SaldoAkhirDaoImpl();
		saldoAkhirDao.createTable();

		LedgerDaoImpl ledgerDao = new LedgerDaoImpl();
		ledgerDao.createTipeLedger();
		ledgerDao.createTable();
		MenuYangDipesanDaoImpl menuYangDipesanDao = new MenuYangDipesanDaoImpl();
		menuYangDipesanDao.createTable();
	}

}
