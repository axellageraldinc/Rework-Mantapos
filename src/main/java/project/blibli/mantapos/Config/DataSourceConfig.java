package project.blibli.mantapos.Config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceConfig {

    // Class yang mengatur konfigurasi database PostgreSQL (sama seperti di application.properties
    public static DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/MantaposDatabase");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("postgres");
        return driverManagerDataSource;
    }
}
