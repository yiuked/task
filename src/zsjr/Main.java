package zsjr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class); 

	public static void main(String[] args) {
		Mysql mysql = new Mysql();
		mysql.connect();
		mysql.execut("update {users} set password=md5('as616766')");
		//List<Map<String, Object>> list = mysql.queryAll("SELECT * FROM {users}");
		//System.out.println(list);
//		try{
//			logger.info("System is booting...");
//			TaskListen taskListen = new TaskListen(10501);
//			taskListen.start();
//			logger.info("TaskListen is started.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
}
