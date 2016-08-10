/**
 * SATLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tecnocom.mediosdepago.sat.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class SATLocator extends org.apache.axis.client.Service implements com.tecnocom.mediosdepago.sat.webservice.SAT {

    // Use to get a proxy class for SATHttpSoap11Endpoint
    /* Endereço DES, implementacao default */
    // private java.lang.String SATHttpSoap11Endpoint_address = "http://10.192.228.181:22080/axis2/services/SAT.SATHttpSoap11Endpoint/";
    private java.lang.String SATHttpSoap11Endpoint_address;
    
    // Use to get a proxy class for SATHttpSoap12Endpoint
    /* Endereço DES */
    //private java.lang.String SATHttpSoap12Endpoint_address = "http://10.192.228.181:22080/axis2/services/SAT.SATHttpSoap12Endpoint/";
    private java.lang.String SATHttpSoap12Endpoint_address;
    
    /**
     * Cria novo gerenciador de servicos do webservice
     * Prepara as URLs dos dos protocolos disponiveis para acesso aos servicos
     * @param wsdlEase
     */
    public SATLocator( String wsdlEase ) throws java.lang.Exception {
        try {
            // Valida a URL informada
            new URL( wsdlEase );
            
            
            System.out.println(wsdlEase);
            // Prepara protocolos de acesso aos servicos
            /**
             *  Exemplo: 
             *  wsdl =       http://10.192.228.181:22080/axis2/services/SAT?wsdl
             *  urlService = http://10.192.228.181:22080/axis2/services/SAT.SATHttpSoap11Endpoint/
             */
            String urlService = wsdlEase.split( "\\?" )[0];
            SATHttpSoap11Endpoint_address = urlService +".SATHttpSoap11Endpoint/";
            SATHttpSoap12Endpoint_address = urlService +".SATHttpSoap12Endpoint/";
        } catch ( MalformedURLException mfue ) {
            throw new java.lang.Exception("WSDL informado incorretamente, favor verificar parametro no Agente EASE. (EASE_WSDL)");
        }
    }

    public SATLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SATLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    public java.lang.String getSATHttpSoap11EndpointAddress() {
        return SATHttpSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SATHttpSoap11EndpointWSDDServiceName = "SATHttpSoap11Endpoint";

    public java.lang.String getSATHttpSoap11EndpointWSDDServiceName() {
        return SATHttpSoap11EndpointWSDDServiceName;
    }

    public void setSATHttpSoap11EndpointWSDDServiceName(java.lang.String name) {
        SATHttpSoap11EndpointWSDDServiceName = name;
    }

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SATHttpSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSATHttpSoap11Endpoint(endpoint);
    }

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.tecnocom.mediosdepago.sat.webservice.SATSoap11BindingStub _stub = new com.tecnocom.mediosdepago.sat.webservice.SATSoap11BindingStub(portAddress, this);
            _stub.setPortName(getSATHttpSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }


    public java.lang.String getSATHttpSoap12EndpointAddress() {
        return SATHttpSoap12Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SATHttpSoap12EndpointWSDDServiceName = "SATHttpSoap12Endpoint";

    public java.lang.String getSATHttpSoap12EndpointWSDDServiceName() {
        return SATHttpSoap12EndpointWSDDServiceName;
    }

    public void setSATHttpSoap12EndpointWSDDServiceName(java.lang.String name) {
        SATHttpSoap12EndpointWSDDServiceName = name;
    }

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SATHttpSoap12Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSATHttpSoap12Endpoint(endpoint);
    }

    public com.tecnocom.mediosdepago.sat.webservice.SATPortType getSATHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.tecnocom.mediosdepago.sat.webservice.SATSoap12BindingStub _stub = new com.tecnocom.mediosdepago.sat.webservice.SATSoap12BindingStub(portAddress, this);
            _stub.setPortName(getSATHttpSoap12EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.tecnocom.mediosdepago.sat.webservice.SATPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.tecnocom.mediosdepago.sat.webservice.SATSoap11BindingStub _stub = new com.tecnocom.mediosdepago.sat.webservice.SATSoap11BindingStub(new java.net.URL(SATHttpSoap11Endpoint_address), this);
                _stub.setPortName(getSATHttpSoap11EndpointWSDDServiceName());
                return _stub;
            }
            if (com.tecnocom.mediosdepago.sat.webservice.SATPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.tecnocom.mediosdepago.sat.webservice.SATSoap12BindingStub _stub = new com.tecnocom.mediosdepago.sat.webservice.SATSoap12BindingStub(new java.net.URL(SATHttpSoap12Endpoint_address), this);
                _stub.setPortName(getSATHttpSoap12EndpointWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SATHttpSoap11Endpoint".equals(inputPortName)) {
            return getSATHttpSoap11Endpoint();
        }
        else if ("SATHttpSoap12Endpoint".equals(inputPortName)) {
            return getSATHttpSoap12Endpoint();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com", "SAT");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com", "SATHttpSoap11Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://webservice.sat.mediosdepago.tecnocom.com", "SATHttpSoap12Endpoint"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SATHttpSoap11Endpoint".equals(portName)) {
//            setSATHttpSoap11EndpointEndpointAddress(address);
            SATHttpSoap11Endpoint_address = address;
        }
        else 
if ("SATHttpSoap12Endpoint".equals(portName)) {
//            setSATHttpSoap12EndpointEndpointAddress(address);
            SATHttpSoap12Endpoint_address = address;
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
