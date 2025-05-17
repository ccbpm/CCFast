package WebServiceListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
//import javax.xml.ws.Endpoint;

import WebServiceImp.CCFormAPI;
import WebServiceImp.LocalWS;


public class WebServicePublishListener implements ServletContextListener {
     @Override
     public void contextDestroyed(ServletContextEvent sce) {
 
     }
 
     @Override
     public void contextInitialized(ServletContextEvent sce) {
         //WebService的发布地址
         String address = "http://localhost:8098//jflow-web/WebService/CCFormAPIService";
        //发布WebService，WebServiceImpl类是WebServie接口的具体实现类
         //Endpoint.publish(address , new CCFormAPI());
         
         //WebService的发布地址
        //address = "http://localhost:8098/WebService/LocalWService";
        //发布WebService，WebServiceImpl类是WebServie接口的具体实现类
         //Endpoint.publish(address , new LocalWS());
         
         
         System.out.println("使用WebServicePublishListener发布webservice成功!");
     }

}
