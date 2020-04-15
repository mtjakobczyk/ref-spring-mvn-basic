package pl.mtjakobczyk.apps.reference.model;

public class Message {
	private final String generatorName;
	private final String uuid;
	
	public Message(String generatorName, String uuid) {
		this.generatorName = generatorName;
		this.uuid = uuid; 
	}

	public String getGeneratorName() {
		return generatorName;
	}

	public String getUuid() {
		return uuid;
	}
	
}
