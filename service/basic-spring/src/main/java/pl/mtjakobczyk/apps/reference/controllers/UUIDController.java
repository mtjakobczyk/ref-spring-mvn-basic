package pl.mtjakobczyk.apps.reference.controllers;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.mtjakobczyk.apps.reference.model.Message;

@RestController
public class UUIDController {
	
	@Value("${GENERATOR_NAME}")
	private String generatorName;
	
	private static final Logger LOGGER = Logger.getLogger(UUIDController.class.getName());
	
	@GetMapping("/uuid")
	public Message get() {
		LOGGER.info(String.format("Launching UUID Generator: %s", generatorName));
		UUID uuid = UUID.randomUUID();
		LOGGER.info(String.format("Generated UUID: %s", uuid));
		return new Message(generatorName, uuid.toString());
	}
	
}
