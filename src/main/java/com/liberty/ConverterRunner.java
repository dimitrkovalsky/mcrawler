package com.liberty;

import com.liberty.service.DataConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class ConverterRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ConverterRunner.class).web(false).run(args);
        DataConverter converter = context.getBean(DataConverter.class);
        //converter.runArtistConverter();
        converter.runAlbumConverter();
    }
}
