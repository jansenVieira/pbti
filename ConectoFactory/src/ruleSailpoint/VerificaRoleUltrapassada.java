package ruleSailpoint;

import sailpoint.object.*;
import sailpoint.api.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VerificaRoleUltrapassada {

	int umDia = 86400000;
	List perfilExcluido = new ArrayList();
	
	public List listaTodosSisPerfil() {

		
		List retornaSistema = new ArrayList();
		List roleTypes = new ArrayList();
		roleTypes.add("PERFIL");

		queryOptions = new QueryOptions();
		queryOptions.addFilter(Filter.eq("disabled", false));
		queryOptions.addFilter(Filter.in("type", roleTypes));
		resultIterator = context.search(Bundle.class, queryOptions, "name");

		while (resultIterator.hasNext()) {
			Object thisSistema = resultIterator.next();
			nomeSistema = thisSistema[0];
			retornaSistema.add(nomeSistema);
		}

		return retornaSistema;

	}
	
	public Date tempoSistema(String sistema)
	{
		List type = new ArrayList();
		type.add("SISTEMA");
		Date tempoSistem;
		
		queryOptions = new QueryOptions();
		queryOptions.addFilter(Filter.eq("disabled", false));
		queryOptions.addFilter(Filter.in("type", type));
		queryOptions.addFilter(Filter.eq("name", sistema));

		resultIterator = context.search(Bundle.class, queryOptions,
				"modified");

		if (resultIterator.hasNext() == true) {

			while (resultIterator.hasNext()) {
				Object thisSistema = resultIterator.next();
				if (thisSistema[0] != null) {
					tempoSistem = thisSistema[0];
				}
			}

		} else {

			return null;
		}
		
		return tempoSistem;
	}
	
	
	public Date tempoPerfil(String sisPerfil)
	{
		Date tempoPerfil;
		
		List types = new ArrayList();
		types.add("PERFIL");

		queryOptions = new QueryOptions();
		queryOptions.addFilter(Filter.eq("disabled", false));
		queryOptions.addFilter(Filter.in("type", types));
		queryOptions.addFilter(Filter.eq("name", sisPerfil));

		resultIterator = context.search(Bundle.class, queryOptions,
				"modified");

		while (resultIterator.hasNext()) {
			Object thisSistema = resultIterator.next();

			if (thisSistema[0] != null) {
				tempoPerfil = thisSistema[0];
			}
		}

		if (tempoPerfil == null) {

			queryOptions = new QueryOptions();
			queryOptions.addFilter(Filter.eq("disabled", false));
			queryOptions.addFilter(Filter.in("type", types));
			queryOptions.addFilter(Filter.eq("name", sisPerfil));

			resultIterator = context.search(Bundle.class,
					queryOptions, "created");

			while (resultIterator.hasNext()) {
				Object thisSistema = resultIterator.next();

				tempoPerfil = thisSistema[0];
			}
		}
		
			
		return tempoPerfil;
		
	}
	
	
	

	public void abas() {

		List sistemaPerfil = listaTodosSisPerfil();

		System.out.println();
		for (String sisPerfil : sistemaPerfil) {

			String[] sepraPerfil = sisPerfil.split("_");

			if (sepraPerfil.length > 1) {

				String sistema = sepraPerfil[0];
				String perfil = sepraPerfil[1];

				Date tempSistem = tempoSistema(sistema);
				
				Date tempPerfil = tempoPerfil(sisPerfil);

				int tempo = tempSistem.getTime() - tempPerfil.getTime();

				// return tempo;

				if (tempo > umDia) {

					perfilExcluido.add(sisPerfil);

				}
			}

		}

	}

}
