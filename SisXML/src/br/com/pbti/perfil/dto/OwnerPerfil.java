package br.com.pbti.perfil.dto;

import org.w3c.dom.Element;

import br.com.pbti.perfil.xml.MontarXmlPerfil;

public class OwnerPerfil
	{

		//
		private Element owner;
		private Element montarOwner;
		private Element refence;
		//
		private String ownerName;

		public MontarXmlPerfil montarXml = new MontarXmlPerfil();

		@SuppressWarnings("static-access")
		public void montarOwner() {
			montarOwner = montarXml.getDoc().createElement("Owner");
			setOwner(montarOwner);
			refence();
		}

		@SuppressWarnings("static-access")
		private void refence() {

			if (getOwnerName().equals("Sem dono"))
				{
					refence = montarXml.getDoc().createElement("Reference");
					refence.setAttribute("class", "sailpoint.object.Identity");
					refence.setAttribute("name", "spadmin");
					owner.appendChild(refence);
				} else
				{

					refence = montarXml.getDoc().createElement("Reference");
					refence.setAttribute("class", "sailpoint.object.Identity");
					refence.setAttribute("name", getOwnerName());
					owner.appendChild(refence);

				}

		}

		public String getOwnerName() {
			return ownerName;
		}

		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}

		public Element getOwner() {
			return owner;
		}

		public void setOwner(Element owner) {
			this.owner = owner;
		}
	}
