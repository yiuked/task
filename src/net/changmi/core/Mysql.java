package net.changmi.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Mysql {
	private static Logger logger = Logger.getLogger(Main.class);
	private static Mysql _instance = null;
	public static Properties config;
	public Connection connection = null;

	public Mysql() {
		String configFile = "/zsjr/config/mysql.properties";
		config = new Properties();
		try {
			config.load(Mysql.class.getResourceAsStream(configFile));
		} catch (IOException e) {
			logger.error(String.format("加载数据库配置文�?(%s)失败，未找到文件!", config));
		}
	}
	
	public static Mysql instance() {
		if (_instance == null) {
			_instance = new Mysql();
			_instance.connect();
		}
		return _instance;
	}

	public void connect() {
		if (connection == null) {
			logger.info("正在连接数据�?...");
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
			} catch(SQLException e) {
				e.printStackTrace();
				logger.error("数据库连接失�?!" + String.format("%s %s %s", url, user, passwd));
			}
		}
	}

	public List<Map<String, Object>> queryAll(String sql) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			PreparedStatement preStat = connection.prepareStatement(PSql(sql));
			ResultSet rs = preStat.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信�?,元数�?
			int columnCount = md.getColumnCount();   //获得列数
			while (rs.next()) {
				Map<String,Object> rowData = new HashMap<String,Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);
			}
			rs.close();
			preStat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> queryOne(String sql) {
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			PreparedStatement preStat = connection.prepareStatement(PSql(sql));
			ResultSet rs = preStat.executeQuery();
			ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信�?,元数�?
			int columnCount = md.getColumnCount();   //获得列数
			if (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					map.put(md.getColumnName(i), rs.getObject(i));
				}
			}
			rs.close();
			preStat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public int execut(String sql) {
		try {
			PreparedStatement preStat = connection.prepareStatement(PSql(sql));
			return preStat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int insert(String sql) {
		int primaryKey = -1;
		try {
			PreparedStatement preStat = connection.prepareStatement(PSql(sql), Statement.RETURN_GENERATED_KEYS);
			preStat.executeUpdate();
			ResultSet rs = preStat.getGeneratedKeys();
			if (rs.next()) {
				primaryKey = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return primaryKey;
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
		logger.info(sb.toString());
		return sb.toString();
	}
}
