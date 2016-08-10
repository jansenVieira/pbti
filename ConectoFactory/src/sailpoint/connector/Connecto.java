package sailpoint.connector;

import java.util.List;
import java.util.Map;

import sailpoint.api.ExpiredPasswordException;
import sailpoint.object.Application;
import sailpoint.object.Application.Feature;
import sailpoint.object.AttributeDefinition;
import sailpoint.object.Filter;
import sailpoint.object.Partition;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningResult;
import sailpoint.object.ResourceObject;
import sailpoint.object.Schema;
import sailpoint.tools.CloseableIterator;
import sailpoint.tools.GeneralException;

public class Connecto implements Connector {

	@Override
	public ResourceObject authenticate(String arg0, String arg1)
			throws ConnectorException, ObjectNotFoundException,
			AuthenticationFailedException, ExpiredPasswordException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProvisioningResult checkStatus(String arg0)
			throws ConnectorException, GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema discoverSchema(String arg0, Map<String, Object> arg1)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Application getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConnectorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AttributeDefinition> getDefaultAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schema> getDefaultSchemas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstance() {
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
	public ResourceObject getObject(String arg0, String arg1,
			Map<String, Object> arg2) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Application getProxiedApplication(String arg0,
			Map<String, Object> arg1) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> getSupportedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSystemIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Application getTargetApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTargetInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseableIterator<ResourceObject> iterateObjects(Partition arg0)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseableIterator<ResourceObject> iterateObjects(String arg0,
			Filter arg1, Map<String, Object> arg2) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProvisioningResult provision(ProvisioningPlan arg0)
			throws ConnectorException, GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setApplication(Application arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInstance(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSystemIdentity(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTargetApplication(Application arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTargetInstance(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testConfiguration() throws ConnectorException {
		// TODO Auto-generated method stub

	}

}
