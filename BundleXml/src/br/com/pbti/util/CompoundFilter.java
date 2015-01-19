package br.com.pbti.util;

import java.util.ArrayList;
import java.util.List;

public class CompoundFilter {

	private List compounds = new ArrayList();

	public void add(CompositeFilter compound) {
		compounds.add(compound);
	}

	public List getContent() {
		return compounds;
	}

}
