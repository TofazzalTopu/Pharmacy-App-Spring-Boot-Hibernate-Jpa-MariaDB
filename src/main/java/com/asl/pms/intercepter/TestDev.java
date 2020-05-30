package com.asl.pms.intercepter;

import java.net.URL;

public class TestDev {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		URL wsdlURL = new URL("http://180.211.137.27:8090/HES/services/DoCommandRequest?wsdl");
		
		System.out.println("Web service URL: "+wsdlURL);
	}

}
