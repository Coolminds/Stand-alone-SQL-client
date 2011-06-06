import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class HandlePermission {
	
	private JSONObject jsonObject;
	private String status;
	private Map<String, Connection> map = new HashMap<String, Connection>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void replaceDataOfDocument_HandlePermission(final String data,final URL location) {
		System.out.println("Starting the save process");
		AccessController.doPrivileged(new PrivilegedAction() { 
			public Object run() { 
				// perform the security-sensitive operation here 
				try {
					FileInputStream fileInputStream =  new FileInputStream(location.getPath());
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
					String fromFile;
					StringBuffer stringBuffer = new StringBuffer();
					while((fromFile = bufferedReader.readLine()) != null) {
						if(fromFile.contains("var data = ")) {
							fromFile = "var data = " + data;
						}
						stringBuffer.append(fromFile+"\n");
					}
					bufferedReader.close();
					fileInputStream.close();
					FileOutputStream fileOutputStream = new FileOutputStream(location.getFile());
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
					bufferedWriter.write(stringBuffer.toString(),0,stringBuffer.length());
					bufferedWriter.close();
					fileOutputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			} 
		});
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject getResult_HandlePermission(final String query, final String databaseType, final String connectionURL, final String userName, final String password){
		System.out.println("Starting to fetch result");
		AccessController.doPrivileged(new PrivilegedAction() { 
			public Object run() { 
			// perform the security-sensitive operation here 
				try{
					Connection connection = getConnection(databaseType, connectionURL, userName, password);
					/*if("mysql".equals(databaseType)){
						Class.forName("com.mysql.jdbc.Driver").newInstance();
					} else if("sql".equals(databaseType)) {
						Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
					}
					Connection connection = DriverManager.getConnection(connectionUrl, userName , password);*/
					if(connection != null){
					    Statement statement = connection.createStatement();
					    jsonObject = new JSONObject();
					    ResultSet resultSet = statement.executeQuery(query);
					    JSONArray jsonArray = new JSONArray();
					    for(int i=1 ; i<=resultSet.getMetaData().getColumnCount(); i++) {
					    	JSONObject object = new JSONObject();
					    	object.put("name", resultSet.getMetaData().getColumnName(i));
					    	object.put("type", resultSet.getMetaData().getColumnTypeName(i));
					    	jsonArray.add(object);
					    }
					    JSONArray _jsonArray = new JSONArray();
					    while(resultSet.next()) {
					    	JSONArray array = new JSONArray();
					    	for(int i=1 ; i<=resultSet.getMetaData().getColumnCount(); i++) {
					    		array.add(resultSet.getString(i));
					    	}
					    	_jsonArray.add(array);
					    }
					    jsonObject.put("columns", jsonArray);
					    jsonObject.put("rows", _jsonArray);
					    //System.out.println("jsonObject: "+jsonObject);
					    resultSet.close();
					    statement.close();
					    /*if(!connection.isClosed()){
					    	connection.close();
					    }*/
					}
				}catch(Exception e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null; 
			} 
			});
		return jsonObject;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String testConnection_HandlePermission(final String databaseType,final String connectionURL, final String userName, final String password) {
		System.out.println("Started the test");
		status = "failure";
		AccessController.doPrivileged(new PrivilegedAction() {
				public Object run(){
					try {
						Connection connection = getConnection(databaseType, connectionURL, userName, password);
						/*if("mysql".equals(databaseType)){
							Class.forName("com.mysql.jdbc.Driver").newInstance();
						} else if("sql".equals(databaseType)) {
							Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
						}
						Connection connection = DriverManager.getConnection(connectionURL, userName, password);*/
						if(connection != null){
							Statement statement = connection.createStatement();					
							statement.close();
						    /*if(!connection.isClosed()){
						    	connection.close();
						    }*/
						    status = "success";
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
		});
		return status;
	}
	
	public Connection getConnection(String databaseType,String connectionURL, String userName, String password) {
		Connection connection = null;
		if(map.containsKey(connectionURL)) {
			connection = map.get(connectionURL);
			try {
				System.out.println("1");
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(1);
				System.out.println("BF...");
				ResultSet resultSet = statement.executeQuery("select * from T_TEST_ACTION where response_message like '%abc%'");
				System.out.println(resultSet.toString());
				while(resultSet.next()) {
					System.out.println(resultSet.getString(1));
				}
				System.out.println("2");
				statement.close();
				System.out.println("3");
				System.out.println("Used old connection of: " + connectionURL);
				return connection;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("5");
				connection = getNewConnection(databaseType, connectionURL, userName, password);
				e.printStackTrace();
				System.out.println("New Connection");
			}
		} else {
			connection = getNewConnection(databaseType, connectionURL, userName, password);
			System.out.println("New Connection");
		}
		return connection;
	}
	
	public Connection getNewConnection(String databaseType,String connectionURL, String userName, String password) {
		Connection connection = null;
		try {
			if("mysql".equals(databaseType)){
					Class.forName("com.mysql.jdbc.Driver").newInstance();
			} else if("sql".equals(databaseType)) {
					Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			} 
			connection = DriverManager.getConnection(connectionURL, userName, password);
			if(map.containsKey(connectionURL)) {
				map.remove(connectionURL);
			}
			map.put(connectionURL, connection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection connection = DriverManager.getConnection("jdbc:mysql://coolminds.homeip.net:3306/Emulator?useOldAliasMetadataBehavior=true", "root", "root1234");
			//connection.close();
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(1);
			long bf = System.currentTimeMillis();
			System.out.println("sleep");
			Thread.sleep(10000);
			System.out.println(bf);
			ResultSet resultSet = statement.executeQuery("select * from T_TEST_ACTION where response_message like '%abc%'");
			System.out.println(System.currentTimeMillis() - bf);
			while(resultSet.next()) {
				System.out.println(resultSet.getString(1));
			}
			connection.close();
			//System.out.println("Is valid? " + connection.getWarnings()+"++++++"+System.currentTimeMillis());//isValid(40));
			//System.out.println("Is valid? " + connection.isClosed());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
