/**
 * ExecuteXml.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tecnocom.mediosdepago.sat.webservice;

public class ExecuteXml  implements java.io.Serializable {
    private com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion msgEnvio;

    public ExecuteXml() {
    }

    public ExecuteXml(
           com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion msgEnvio) {
           this.msgEnvio = msgEnvio;
    }


    /**
     * Gets the msgEnvio value for this ExecuteXml.
     * 
     * @return msgEnvio
     */
    public com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion getMsgEnvio() {
        return msgEnvio;
    }


    /**
     * Sets the msgEnvio value for this ExecuteXml.
     * 
     * @param msgEnvio
     */
    public void setMsgEnvio(com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion msgEnvio) {
        this.msgEnvio = msgEnvio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExecuteXml)) return false;
        ExecuteXml other = (ExecuteXml) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.msgEnvio==null && other.getMsgEnvio()==null) || 
             (this.msgEnvio!=null &&
              this.msgEnvio.equals(other.getMsgEnvio())));
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
        if (getMsgEnvio() != null) {
            _hashCode += getMsgEnvio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExecuteXml.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com", ">executeXml"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgEnvio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com", "msgEnvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com/xsd", "Peticion"));
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
