/**
 * SATPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tecnocom.mediosdepago.sat.webservice;

public interface SATPortType extends java.rmi.Remote {
    public com.tecnocom.mediosdepago.sat.webservice.xsd.Respuesta execute(com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion msgEnvio) throws java.rmi.RemoteException, com.tecnocom.mediosdepago.sat.webservice.ExceptionType0;
    public java.lang.String executeXml(com.tecnocom.mediosdepago.sat.webservice.xsd.Peticion msgEnvio) throws java.rmi.RemoteException, com.tecnocom.mediosdepago.sat.webservice.ExceptionType0;
}
