package br.com.pbti.util;

import java.util.ArrayList;
import java.util.List;

import br.com.pbti.dto.Cargo;

public class CompositeFilter {

	@SuppressWarnings("rawtypes")
	private List filters = new ArrayList();
	private String operations = "Add";

	
	
	@SuppressWarnings("unchecked")
	public void add(Cargo cargoFilter) {
		filters.add(cargoFilter);
	}

	@SuppressWarnings("rawtypes")
	public List getContent() {
		return filters;
	}

	public List getFilters() {
		return filters;
	}

	public void setFilters(List filters) {
		this.filters = filters;
	}

	public String getOperations() {
		return operations;
	}

	public void setOperations(String operations) {
		this.operations = operations;
	}

}
