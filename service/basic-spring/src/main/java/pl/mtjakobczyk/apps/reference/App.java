package pl.mtjakobczyk.apps.reference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import pl.mtjakobczyk.apps.reference.controllers.UUIDController;

@SpringBootApplication
@ComponentScan(basePackageClasses = UUIDController.class)
public class App 
{
	
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }

}
