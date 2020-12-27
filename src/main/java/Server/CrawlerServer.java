/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Compute.Compute;
import Compute.Task;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import org.jsoup.Jsoup;

/**
 *
 * @author kzaxa
 */
public class CrawlerServer implements Compute{  

    private static Properties prop;
    //Get properties
    private static int cFilesNum=1;
    private static int cFilesBytes=0;
    private static int cFilesDocs=0;

    public CrawlerServer() {
    }
    
    public <T> T executeTask(Task<T> t) {
            return t.execute();}
    
   public static void main(String[] args) throws IOException {
     /*   if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
        }*/
        try {
                Compute engine = new CrawlerServer();
                var stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
                String name = "Compute";
                Registry registry = LocateRegistry.getRegistry();
                registry.rebind(name,stub);
                
                prop = new Properties();
                getFileStatus();
                
                System.out.println("Server started successfully");
        } catch (RemoteException e) {
                System.err.println("Crawler Server exception: "+e.getMessage());
        }
    }
   
   
   private static void setProperties(){
       try (final OutputStream output = new FileOutputStream("src/main/java/Server/config.properties")) {

            // set the properties value
            prop.setProperty("cFilesNum", String.valueOf(cFilesNum));
            prop.setProperty("cFilesBytes", String.valueOf(cFilesBytes));
            prop.setProperty("cFilesDocs", String.valueOf(cFilesDocs));
            // save properties to project root folder
            prop.store(output,"");
            
            System.out.print("Saved Properties: ");
            System.out.println(prop);

        } catch (IOException io) {
           System.err.println(io.getMessage());
        }
   }
   
   
   private static void getFileStatus() throws IOException{
       try (final InputStream input = new FileInputStream("src/main/java/Server/config.properties")) {

            //load a properties file from class path
            prop.load(input);
            
            //Set value to variables
            cFilesNum=Integer.valueOf(prop.getProperty("cFilesNum"));
            cFilesBytes=Integer.valueOf(prop.getProperty("cFilesBytes"));
            cFilesDocs=Integer.valueOf(prop.getProperty("cFilesDocs"));

            System.out.print("Properties loaded successfully: ");
            //get the property value and print it out
            System.out.print(prop.getProperty("cFilesNum")+" ");
            System.out.print(prop.getProperty("cFilesBytes")+" ");
            System.out.println(prop.getProperty("cFilesDocs"));

        } catch (Exception ex) {
            System.err.println("getFileStatus Error: "+ex.getMessage());
            System.out.println("Creating new properties file...");
            setProperties(); //Create new properties in case of old own not found or not existing
        }
       
   }
  
    @Override
    public String crawl(String clientURL){
        
        int docnum = (cFilesNum-1)*10 + cFilesDocs +1; //Current Document
        int docStart = cFilesBytes;                    // Start point od current document
        int cBytes=0;                                 //Size of document. 1 letter = 1 byte
        int cFile=cFilesNum;                          //Current file
        
        System.out.println("URL to crawl: "+clientURL);
        
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/main/java/Server/Files/File"+String.valueOf(cFilesNum)+".txt", true)))) {
            String html = Jsoup.connect(clientURL).get().html(); //Save html page
            out.println(html);                                  //Write to file
            cBytes=html.length();                               //Save extra length
            out.close();                                       //Close PrintWriter
        }catch(Exception e){
            System.err.println("crawl1 error: "+e.getMessage());
        }
        cFilesDocs++;
        cFilesBytes+=cBytes;
        if(cFilesDocs==10){      //Check if documents of a file are on limit
            cFilesNum++;
            cFilesBytes=0;
            cFilesDocs=0;
        }
        setProperties();      //Set new properties
        
            return ("Doc"+String.valueOf(docnum)+": File No."+String.valueOf(cFile)+", Start: "+String.valueOf(docStart)); //Return values
        }  
}
