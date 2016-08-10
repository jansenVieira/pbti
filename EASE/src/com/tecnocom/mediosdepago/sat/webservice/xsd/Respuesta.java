/**
 * Respuesta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tecnocom.mediosdepago.sat.webservice.xsd;

import java.util.List;

public class Respuesta  implements java.io.Serializable {
    private java.lang.String descRetorno;

    private List<Parametro> registros;

    private java.lang.String retorno;

    public Respuesta() {
    }

    public Respuesta(
           java.lang.String descRetorno,
           List<Parametro> registros,
           java.lang.String retorno) {
           this.descRetorno = descRetorno;
           this.registros = registros;
           this.retorno = retorno;
    }


    /**
     * Gets the descRetorno value for this Respuesta.
     * 
     * @return descRetorno
     */
    public java.lang.String getDescRetorno() {
        return descRetorno;
    }


    /**
     * Sets the descRetorno value for this Respuesta.
     * 
     * @param descRetorno
     */
    public void setDescRetorno(java.lang.String descRetorno) {
        this.descRetorno = descRetorno;
    }


    /**
     * Gets the registros value for this Respuesta.
     * 
     * @return registros
     */
    //public List<Parametro>[]  getRegistros() {
    public List<Parametro> getRegistros() {
        return registros;
    }

    /**
     * Sets the registros value for this Respuesta.
     * 
     * @param registros
     */
    public void setRegistros(List<Parametro>  registros) {
        this.registros = registros;
    }

    /**
     * Gets the retorno value for this Respuesta.
     * 
     * @return retorno
     */
    public java.lang.String getRetorno() {
        return retorno;
    }


    /**
     * Sets the retorno value for this Respuesta.
     * 
     * @param retorno
     */
    public void setRetorno(java.lang.String retorno) {
        this.retorno = retorno;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Respuesta)) return false;
        Respuesta other = (Respuesta) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.descRetorno==null && other.getDescRetorno()==null) || 
             (this.descRetorno!=null &&
              this.descRetorno.equals(other.getDescRetorno()))) &&
            ((this.registros==null && other.getRegistros()==null) || 
             (this.registros!=null &&
              this.registros.equals(other.getRegistros()))) &&
            ((this.retorno==null && other.getRetorno()==null) || 
             (this.retorno!=null &&
              this.retorno.equals(other.getRetorno())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getDescRetorno() != null) {
            _hashCode += getDescRetorno().hashCode();
        }
        if (getRegistros() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegistros());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegistros(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRetorno() != null) {
            _hashCode += getRetorno().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Respuesta.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "Respuesta"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descRetorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "descRetorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registros");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "registros"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "Registro"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "retorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
