package project.blibli.mantapos.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    DataSource dataSource;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityConfig(@Qualifier("dataSource") DataSource dataSource,
                          BCryptPasswordEncoder bCryptPasswordEncoder){
        this.dataSource = dataSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //Class yang mengatur konfigurasi mengenai security

    //Melakukan AutoWired untuk hal Authentication
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource) //dataSource diambil dari DataSource
                .usersByUsernameQuery(
                        "select username, password, enabled from users where username=?") //query Username
                .authoritiesByUsernameQuery(
                        "select username, role from users_roles where username=?") //query authority (role nya)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    //Konfigurasi mengenai page mana yang boleh diakses oleh siapa
    //Konfigurasi mengenai page redirect jika login berhasil dan login gagal
    //Konfigurasi mengenai page redirect jika logout berhasil
    //Konfigurasi jika user ingin mengakses page yang tidak bisa (role tidak sesuai)
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeRequests()
                //pada access disini pakai hasAuthority (instead of hasRole) karena
                //kalau pake hasRole, di database harus ada prefix ROLE_ pada record-nya, contoh ROLE_CASHIER
                .antMatchers("/cashier/**").access("hasAnyAuthority('cashier', 'manager', 'owner')")
                .antMatchers("/dashboard/**", "/menu/**", "/outcome/**", "/employee/**", "/range/**", "/add-menu/**", "/edit/menu/**", "/add-cashier/**", "/outcome-post/**", "/daily/**", "/saldo/**").access("hasAnyAuthority('manager', 'owner')")
                .antMatchers("/restaurant/**").access("hasAuthority('admin')")
                .antMatchers("/delete/user/**", "/active/user/**").access("hasAnyAuthority('admin', 'manager', 'owner')")
                .and()
                .formLogin().loginPage("/login").failureUrl("/login?error")
                .defaultSuccessUrl("/")
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().logoutSuccessUrl("/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/privilege")
                .and()
                .csrf().disable();
    }
}