package net.changmi.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

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
              RequestObject request = JSON.parseObject(m, RequestObject.class, Feature.SupportNonPublicField);
              Date currentTime = new Date();
              SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              String dateString = formatter.format(currentTime);
              
              Mysql.instance().insert(
            		  String.format("INSERT INTO {task_queue} SET service='%s',`status`=0,created_at='%s',updated_at='%s'", 
            				  request.getServcie(),
            				  dateString,
            				  dateString)
            		  );
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
