/**
 * SAT.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tecnocom.mediosdepago.sat.webservice;

public interface SAT extends javax.xml.rpc.Service {
    public java.lang.String getSATHttpSoap11EndpointAddress();

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException;

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getSATHttpSoap12EndpointAddress();

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException;

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
