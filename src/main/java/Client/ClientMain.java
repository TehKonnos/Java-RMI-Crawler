/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Compute.Compute;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author TehKonnos
 * RUN AT CMD: start rmiregistry -J-classpath -JC:\JavaCrawler\target\JavaCrawler-1.0-SNAPSHOT.jar
 */
public class ClientMain {
    public static void main(String[] args){
        //Disabled because running local
       /* if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager()); 
    }*/
        try {
            String name = "Compute";
            String host = (args.length <1) ?null : args[0]; 
            Registry registry = LocateRegistry.getRegistry(host);
            
            Compute comp = (Compute) registry.lookup(name);
            
            BufferedReader reader;
            String result;
            try {
                reader = new BufferedReader(new FileReader("src/main/java/Client/urls.txt")); //To path allazei analoga ton ipologisti
                String line = reader.readLine();
                int i=0;
                while (line != null) {
                   result = comp.crawl(line);

                   System.out.println(result);
                    // read next line
                    line = reader.readLine();
                }
                System.out.println("Client stopped");
                reader.close();
            } catch (IOException e) {
                System.err.println("Client exception:"+ e.getMessage());
            }
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Client exception:"+ e.getMessage());
        }
    }
}
