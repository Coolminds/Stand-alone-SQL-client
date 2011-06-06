import java.applet.Applet;
import java.net.URL;

import net.sf.json.JSONObject;
public class AppletProgramming extends Applet{

	private static final long serialVersionUID = 1L;
	private HandlePermission handlePermission = new HandlePermission();
	/**
	 * replaceDocument
	 * @param dataString
	 */
	public void replaceDataOfDocument(String data){
		URL location = this.getClass().getResource("Query.html");
		//HandlePermission handlePermission = new HandlePermission();
		handlePermission.replaceDataOfDocument_HandlePermission(data,location);
		System.out.println("After Completing the save process");
	}
	
	/**
	 * getResult
	 * @param querytxt
	 */
	public JSONObject getResult(String query, String databaseType, String connectionURL, String userName, String password) {
		JSONObject jsonObject = null;
		try {
			//String connectionUrl = "jdbc:mysql://coolminds.homeip.net:3306/Emulator?useOldAliasMetadataBehavior=true";
			//HandlePermission handlePermission = new HandlePermission();
			jsonObject = handlePermission.getResult_HandlePermission(query, databaseType, connectionURL, userName, password);
			System.out.println("After fetching result");
			/*JSObject window = JSObject.getWindow(this);
			window.eval("getResult_FromApplet(\'" + jsonObject.toString() + "\',\'" + resultDiv +"\')");*/
		  } catch (Exception e) {
				// TODO Auto-generated catch block
			    e.printStackTrace();
		  }
		return jsonObject;
	}
	
	public String testConnection(String databaseType, String connectionURL, String userName, String password){
		//HandlePermission handlePermission = new HandlePermission();
		String status = handlePermission.testConnection_HandlePermission(databaseType, connectionURL, userName, password);
		//JSObject window = JSObject.getWindow(this);
		//window.eval("testConnection_FromApplet(\'" + status + "\')");
		System.out.println("After Completing the test");
		return status;
	}
	
}