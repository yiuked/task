package zsjr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class); 

	public static void main(String[] args) {
		try{
			logger.info("System is booting...");
			TaskProcess taskProcess = new TaskProcess();
			TaskListen taskListen = new TaskListen(10501);
			taskProcess.start();
			taskListen.start();
			logger.info("TaskListen is started.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
