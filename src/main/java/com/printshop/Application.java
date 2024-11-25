package com.printshop;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
    	
    /*	DESACTIVAR PROTOCOLOS "TLS"
     * String disabledAlgorithms = Security.getProperty("jdk.tls.disabledAlgorithms");
		disabledAlgorithms = disabledAlgorithms.replace("TLSv1, TLSv1.1,", "");
		Security.setProperty("jdk.tls.disabledAlgorithms", disabledAlgorithms);*/
    	
        SpringApplication.run(Application.class, args);
    }
}