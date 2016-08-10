package sailpoint.connector2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Connecto implements Connector {

	@Override
	public Map<String, Object> authenticate(String arg0, String arg1)
			throws ConnectorException, AuthenticationFailedException,
			ExpiredPasswordException, ObjectNotFoundException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(String arg0, ConnectorConfig arg1)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> create(Map<String, Object> arg0)
			throws ConnectorException, ObjectAlreadyExistsException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String arg0) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable(String arg0) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Schema discoverSchema() throws ConnectorException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enable(String arg0) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public ConnectorConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Partition> getIteratorPartitions(String arg0, int arg1,
			Filter arg2, Map<String, Object> arg3) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Log getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> getSupportedFeatures(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSupportedObjectTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Map<String, Object>> iterate(Filter arg0)
			throws ConnectorException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Map<String, Object>> iterate(Partition arg0, String arg1)
			throws ConnectorException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> read(String arg0) throws ConnectorException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLog(Log arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPassword(String arg0, String arg1, String arg2,
			Map<String, Object> arg3) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void testConnection() throws ConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlock(String arg0) throws ConnectorException,
			ObjectNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> update(Map<String, Object> arg0,
			UpdateOptions arg1) throws ConnectorException,
			ObjectNotFoundException, IllegalArgumentException,
			UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
