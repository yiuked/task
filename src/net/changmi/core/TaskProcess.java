package net.changmi.core;

import java.util.Map;

import org.apache.log4j.Logger;

public class TaskProcess extends Thread{
	   private Thread t;
	   private int waitSec = 10;
	   private String threadName = "Task Process";
	   
	   private static Logger logger = Logger.getLogger(Main.class); 

	   public void run()
	   {
	      while(true)
	      {
	    	
	  		Map<String, Object> map = Mysql.instance().queryOne("SELECT * FROM {task_queue} WHERE `status`=0");
			if (!map.isEmpty()) {
				logger.info(String.format("当前列队为空，%d秒后重试.", waitSec));
				Mysql.instance().execut(String.format("UPDATE {task_queue} SET `status`=1 WHERE id_task=%d", map.get("id_task")));
				System.out.println(map);
			} else {
		    	try {
		    		logger.info(String.format("当前列队为空，%d秒后重试.", waitSec));
					Thread.sleep(waitSec * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	      }
	   }
	   
	   public void start () {
		  logger.info("Task process starting ...");
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start();
	      }
	   }
}
