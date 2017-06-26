package zsjr;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;



public class Mysql {
	private static Logger logger = Logger.getLogger(Main.class); 
	
	public static Connection connection = null;
	public static Statement statement = null;
	

	public void connect() {
		if (connection == null) {
			logger.info("�����������ݿ�...");
	        try {
	        	Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            logger.error("����com.mysql.jdbc.Driverʧ��!");
	        }

	        String config = "/zsjr/config/mysql.properties";
	        Properties properties = new Properties();
	        try {
	        	properties.load(Mysql.class.getResourceAsStream(config));  
			} catch (IOException e) {
				logger.error(String.format("�������ݿ������ļ�(%s)ʧ�ܣ�δ�ҵ��ļ�!", config));
			}
	    	String url = properties.getProperty("mysql.link").trim();
	        String user = properties.getProperty("mysql.user").trim();
	    	String passwd = properties.getProperty("mysql.passwd").trim();
	    	
	        try {
	        	connection = DriverManager.getConnection(url, "root", "");
	        	statement = connection.createStatement();
	        } catch(SQLException e) {
	        	e.printStackTrace();
	        	logger.error("���ݿ�����ʧ��!" + String.format("%s %s %s", url, user, passwd));
	        }
		}
	}

	public List<Map<String, Object>> queryAll(String sql) {  
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	    try {  
	        ResultSet rs = statement.executeQuery(sql);
	        ResultSetMetaData md = rs.getMetaData(); //��ý�����ṹ��Ϣ,Ԫ����  
	        int columnCount = md.getColumnCount();   //�������   
	        while (rs.next()) {  
	            Map<String,Object> rowData = new HashMap<String,Object>();
	            for (int i = 1; i <= columnCount; i++) {  
	                rowData.put(md.getColumnName(i), rs.getObject(i));  
	            }  
	            list.add(rowData);  
	        }
	    } catch (SQLException e) {  
	        e.printStackTrace();  
	    }
		return list;  
	}
	
	public String PSql(String sql) {
		sql.replaceAll("/{([a-zA-Z0-9_-]+)}/")
        while (preg_match('/{([a-zA-Z0-9_-]+)}/', $sql, $regs)) {
            $found = $regs[1];
            $sql = preg_replace(('/\\{' . $found) . '\\}/', $this->db_prefix . $found, $sql);
        }
		return sql;
	}
}
