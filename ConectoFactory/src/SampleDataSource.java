/* (c) Copyright 2012 SailPoint Technologies, Inc., All Rights Reserved. */


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.LiveReport;
import sailpoint.object.QueryOptions;
import sailpoint.object.Sort;
import sailpoint.task.Monitor;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SampleDataSource implements JavaDataSource {

	private Monitor monitor;
	private SailPointContext context;
	private QueryOptions baseQueryOptions;
	private Integer startRow;
	private Integer pageSize;
	private Object[] currentRow;
	private Iterator<Object[]> iterator;
	
	public static void main(String[] args) {
		
	}

	public void initialize(SailPointContext context, LiveReport report,
			Attributes<String, Object> arguments, String groupBy,
			List<Sort> sort) throws GeneralException {
		this.context = context;
		baseQueryOptions = new QueryOptions();
		if (arguments.containsKey("applications")) {
			List<String> applicationIds = arguments.getList("applications");
			baseQueryOptions.add(Filter.in("links.application.id",
					applicationIds));
		}
		if (arguments.containsKey("managers")) {
			List<String> managersIds = arguments.getList("managers");
			baseQueryOptions.add(Filter.in("manager.id", managersIds));
		}
		if (sort != null) {
			for (Sort sortItem : sort) {
				baseQueryOptions.addOrdering(sortItem.getField(),
						sortItem.isAscending());
			}
		}
		if (groupBy != null)
			baseQueryOptions.setGroupBys(Arrays.asList(groupBy));
	}

	private void prepare() throws GeneralException {
		QueryOptions ops = new QueryOptions(baseQueryOptions);
		if (startRow != null && startRow > 0) {
			ops.setFirstRow(startRow);
		}
		if (pageSize != null && pageSize > 0) {
			ops.setResultLimit(pageSize);
		}
		iterator = context.search(Identity.class, ops,
				Arrays.asList("name", "displayName", "managerStatus"));
	}

	public boolean next() throws JRException {
		if (iterator == null) {
			try {
				prepare();
			} catch (GeneralException e) {
				throw new JRException(e);
			}
		}
		if (iterator.hasNext()) {
			currentRow = iterator.next();
			return true;
		}
		return false;
	}

	public Object getFieldValue(String field) throws GeneralException {
		if ("name".equals(field)) {
			return currentRow[0];
		} else if ("displayName".equals(field)) {
			return currentRow[1];
		} else if ("managerStatus".equals(field)) {
			return currentRow[2];
		} else {
			throw new GeneralException("Unknown column '" + field + "'");
		}
	}

	public void setLimit(int startRow, int pageSize) {
		this.startRow = startRow;
		this.pageSize = pageSize;
	}

	public int getSizeEstimate() throws GeneralException {
		return context.countObjects(Identity.class, baseQueryOptions);
	}

	public void close() {
	}

	public Object getFieldValue(JRField jrField) throws JRException {
		String name = jrField.getName();
		try {
			return getFieldValue(name);
		} catch (GeneralException e) {
			throw new JRException(e);
		}
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public QueryOptions getBaseQueryOptions() {
		return baseQueryOptions;
	}

	/**
	 * Unused since this is not an hql report.
	 */
	public String getBaseHql() {
		return null;
	}
}