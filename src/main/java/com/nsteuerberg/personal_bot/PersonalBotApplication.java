package com.nsteuerberg.personal_bot;

import com.nsteuerberg.personal_bot.configuration.CustomBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonalBotApplication {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.version"));
		System.out.println(System.getProperty("os.arch"));
		SpringApplication app = new SpringApplication(PersonalBotApplication.class);
		app.setBeanNameGenerator(new CustomBeanNameGenerator());
		app.run(args);
	}

}
