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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;



public class Mysql {
	private static Logger logger = Logger.getLogger(Main.class); 
	
	public static Connection connection = null;
	public static Statement statement = null;
	public static Properties config;
	
	public Mysql() {
        String configFile = "/zsjr/config/mysql.properties";
        config = new Properties();
        try {
        	config.load(Mysql.class.getResourceAsStream(configFile));  
		} catch (IOException e) {
			logger.error(String.format("加载数据库配置文件(%s)失败，未找到文件!", config));
		}
	}

	public void connect() {
		if (connection == null) {
			logger.info("正在连接数据库...");
	        try {
	        	Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            logger.error("加载com.mysql.jdbc.Driver失败!");
	        }

	    	String url = config.getProperty("mysql.link").trim();
	        String user = config.getProperty("mysql.user").trim();
	    	String passwd = config.getProperty("mysql.passwd").trim();
	    	
	        try {
	        	connection = DriverManager.getConnection(url, user, passwd);
	        	statement = connection.createStatement();
	        } catch(SQLException e) {
	        	e.printStackTrace();
	        	logger.error("数据库连接失败!" + String.format("%s %s %s", url, user, passwd));
	        }
		}
	}

	public List<Map<String, Object>> queryAll(String sql) {  
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	    try {  
	        ResultSet rs = statement.executeQuery(PSql(sql));
	        ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据  
	        int columnCount = md.getColumnCount();   //获得列数   
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
	
	public boolean execut(String sql) {
		try {
			return statement.execute(PSql(sql));
		} catch (SQLException e) {
			e.printStackTrace();  
		}
		return false;
	}
	
	public String PSql(String sql) {
	
	    //生成匹配模式的正则表达式
	    String patternString = "\\{([a-zA-Z0-9_-]+)\\}";

	    Pattern pattern = Pattern.compile(patternString);
	    Matcher matcher = pattern.matcher(sql);
	   
	    //两个方法：appendReplacement, appendTail
	    StringBuffer sb = new StringBuffer();
	    while(matcher.find()) {
	        matcher.appendReplacement(sb, config.getProperty("mysql.prefix") + matcher.group(1));
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}
}
