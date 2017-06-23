package zsjr;

import java.net.*;
import java.io.*;
import org.apache.log4j.Logger;

public class TaskListen extends Thread
{
   private Thread t;
   private String threadName = "Task listen";
   private ServerSocket serverSocket;
   
   private static Logger logger = Logger.getLogger(Main.class); 
   public TaskListen(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(10000);
   }
 
   public void run()
   {
      while(true)
      {
         try
         {
              Socket client = serverSocket.accept();
              String clientAdress = client.getInetAddress().toString() + ":" + client.getPort();
              logger.info("Client[" + clientAdress + "] is connected.");

              BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
              String  m = reader.readLine();
              logger.debug("Client[" + clientAdress + "] request data:" + m);
              
              DataOutputStream out = new DataOutputStream(client.getOutputStream()); 
              out.writeUTF("SUCCESS");   
              out.close();
              
              client.close();
              logger.info("Client[" + clientAdress + "] is close.");
         }catch(SocketTimeoutException s)
         {
        	 logger.error("TaskListen Socket timed out!");
         }catch(IOException e)
         {
            e.printStackTrace();
         }
      }
   }
   
   public void start () {
	  logger.info("TaskListen starting ...");
      if (t == null) {
         t = new Thread (this, threadName);
         t.start();
      }
   }
}
