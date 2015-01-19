package br.com.pbti.dto;

public class Cargo {

	private String operation;
	private String property;
	private String value;
	
	public Cargo()
	{
		this.operation = operation;
		this.property = property;
		this.value = value;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
