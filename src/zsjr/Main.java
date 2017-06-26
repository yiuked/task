package zsjr;

import java.io.IOException;
import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class); 

	public static void main(String[] args) {
		TradeMysql mysql = new TradeMysql();
		mysql.connect();
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
