
package com.tecnocom.mediosdepago.sat.webservice.xsd;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Registro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Registro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parametros" type="{http://webservice.sat.mediosdepago.tecnocom.com/xsd}Parametro" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class Registro {
    protected List<Parametro> parametros;

    /**
     * Gets the value of the parametros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parametros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParametros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parametro }
     * 
     * 
     */
    public List<Parametro> getParametros() {
        if (parametros == null) {
            parametros = new ArrayList<Parametro>();
        }
        return this.parametros;
    }

}
