
package com.pps.movie;
import com.pps.core.datasource.MyBatisDataSourceProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Pu PanSheng, 2021/5/11
 * @version OPRA v1.0
 */
@SpringBootApplication(scanBasePackages = {"com.pps.core", "com.pps.movie"})
@EnableAsync
@EnableScheduling
@Import(MyBatisDataSourceProcessor.class)
@EnableTransactionManagement
public class SyncMovieApp {


    public static void main(String[] args) {
        SpringApplication.run(SyncMovieApp.class, args);
    }


}
