package zsjr;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;



public class TradeMysql {
	private static Logger logger = Logger.getLogger(Main.class); 
	
	public static Connection connection;
	public static Statement statement;
	
	public void connect() {
		logger.info("�����������ݿ�...");
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("����com.mysql.jdbc.Driverʧ��!");
        }

        String config = "/zsjr/config/mysql.properties";
        Properties properties = new Properties();
        try {
        	properties.load(TradeMysql.class.getResourceAsStream(config));  
		} catch (IOException e) {
			logger.error(String.format("�������ݿ������ļ�(%s)ʧ�ܣ�δ�ҵ��ļ�!", config));
		}
    	String url = properties.getProperty("mysql.link").trim();
        String user = properties.getProperty("mysql.user").trim();
    	String passwd = properties.getProperty("mysql.passwd").trim();
    	
        try {
        	connection = DriverManager.getConnection(url, "root", "root");
        	statement = connection.createStatement();
        	ResultSet rs = statement.executeQuery("SELECT * FROM `cfm_users`");
            while(rs.next()){
                //Retrieve by column name
                int id  = rs.getInt("user_id");
                String first = rs.getString("username");

                //Display values
                System.out.print("ID: " + id);
                System.out.print(", First: " + first);
             }
            rs.close();
        	logger.info("���ݿ����ӳɹ�.");
        } catch(SQLException e) {
        	e.printStackTrace();
        	logger.error("���ݿ�����ʧ��!" + String.format("%s %s %s", url, user, passwd));
        }
	}
}
