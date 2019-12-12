/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wva.vtiger;
import org.xml.sax.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.nio.file.*;
import com.vtiger.vtwsclib.WSClient;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.commons.lang3.SerializationUtils;
import javax.xml.xpath.*;
/**
 * @author Wouter Vandorpe
 */
/**
 *
 * @author Wouter Vandorpe
 */
public class WVAVtiger {
        static String vtigerURL = "";
	static String vtigerUSR = "";
	static String vtigerUSRKEY = "";
        static String vtigerAccountsQuery = "";
        static String vtigerUsersQuery = "";
        static String vtigerProjectsQueryExtension = "";
        static String vtigerProjectsTasksQueryExtension = "";
        
        
        
        static String meAssignments = "";
        static int meAssignmentID = 0;
        static int meTaskID = 0;
        
        
        static List<clUpdateValue> meUpdateValues = null;
        static List<clUpdateCRM> meUpdateCRM = null;
       
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
      // TODO code application logic here
                System.out.println("Main start");
                read_Config();
		WSClient client = new WSClient(vtigerURL);
                System.out.println("Created the class");
		if (Test_doLogin(client)) {
                    
                        System.out.println(Integer.valueOf(args.length));
                        if (args != null)
                        {
                            if (args[0].equalsIgnoreCase("import"))
                            {
                                set_Projects(client,args[1]);
                            }
                            if (args[0].equalsIgnoreCase("export"))
                            {
                                get_Projects(client);                                
                            }

                        }
                        
                            
			//Test_doListTypes(client);
			//Test_doDescribe(client);
			//Test_doCreate(client);
                        //Test_doRetrieve(client, "2x178");
			//Test_doQuery(client);
			//Test_doInvoke(client);
                        
                        
                }
                System.out.println("Main end");
    }
    /**
     * @param client the command line arguments
     * @return
     */      
    public static void read_Config()
    {
                try 
                {
                   String data = readFileAsString("templates\\config.xml");
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(data));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                   
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("url"))
                                {
                                    vtigerURL = node.getTextContent();
                                }
                                else if (node.getNodeName().equalsIgnoreCase("user"))
                                {
                                    vtigerUSR = node.getTextContent();
                                }
                                else if (node.getNodeName().equalsIgnoreCase("key"))
                                {
                                    vtigerUSRKEY = node.getTextContent();
                                }                                
                                else if (node.getNodeName().equalsIgnoreCase("AccountsQuery"))
                                {
                                    vtigerAccountsQuery = node.getTextContent();
                                }                   
                                else if (node.getNodeName().equalsIgnoreCase("UsersQuery"))
                                {
                                    vtigerUsersQuery = node.getTextContent();
                                }                   
                                else if (node.getNodeName().equalsIgnoreCase("ProjectsQueryExtension"))
                                {
                                    vtigerProjectsQueryExtension = node.getTextContent();
                                }                   
                                else if (node.getNodeName().equalsIgnoreCase("TasksQueryExtension"))
                                {
                                    vtigerProjectsTasksQueryExtension = node.getTextContent();
                                }
                                else if (node.getNodeName().equalsIgnoreCase("Update"))
                                {
                                                                //Create XPath
                                        XPathFactory xpathfactory = XPathFactory.newInstance();
                                        XPath xpath = xpathfactory.newXPath();

                            
                                        XPathExpression expr1 = xpath.compile("Class");
                                        Object result1 = expr1.evaluate(node, XPathConstants.NODESET);
                                        NodeList nodesClass = (NodeList) result1;
                                        //List <byte[]> SerializedData = new ArrayList <byte[]>();
                                        meUpdateValues = new ArrayList<clUpdateValue>(); 
                                        for (int ii = 0; ii < nodesClass.getLength(); ii++) 
                                        {
                                                
                                                clUpdateValue loUpdateValue = new clUpdateValue();
                                                
                                                NamedNodeMap attrs = nodesClass.item(ii).getAttributes();
                                                loUpdateValue.meClassName = attrs.getNamedItem("Name").getNodeValue();
                                                
                                                NodeList nodesProps = nodesClass.item(ii).getChildNodes();

                                                for (int jj = 0; jj < nodesProps.getLength(); jj++)
                                                {
                                                    Node nodesChild = nodesProps.item(jj);
                                                    if (nodesProps.item(jj).getNodeName().equalsIgnoreCase("levelIdentifier"))
                                                    {
                                                        loUpdateValue.meLevelName = ((Attr) nodesProps.item(jj).getAttributes().getNamedItem("Name")).getValue();
                                                        loUpdateValue.meLevelTag = ((Attr) nodesProps.item(jj).getAttributes().getNamedItem("tag")).getValue();
                                                        System.out.println("Tag Level:" + loUpdateValue.meLevelTag );
                                                        loUpdateValue.meLevelContent = nodesProps.item(jj).getTextContent();
                                                    }                            
                                                    if (nodesProps.item(jj).getNodeName().equalsIgnoreCase("ID"))
                                                    { 
                                                        loUpdateValue.meIdName = ((Attr) nodesChild.getAttributes().getNamedItem("Name")).getValue(); 
                                                        loUpdateValue.meIdTag = ((Attr) nodesChild.getAttributes().getNamedItem("tag")).getValue();
                                                        System.out.println("Tag ID:" + loUpdateValue.meIdTag );
                                                        loUpdateValue.meIdContent = nodesProps.item(jj).getTextContent();
                                                    }                            
                                                    if (nodesProps.item(jj).getNodeName().equalsIgnoreCase("Properties"))
                                                    {
                                               
                                                        //Create XPath
                                                        XPathFactory xpathfactory1 = XPathFactory.newInstance();
                                                        XPath xpath1 = xpathfactory1.newXPath();

                                                        XPathExpression expr = xpath1.compile("Prop");
                                                        Object result = expr.evaluate(nodesProps.item(jj), XPathConstants.NODESET);
                                                        NodeList nodesPropertys = (NodeList) result;                                                        
                                    
                                                        loUpdateValue.mePropertyName = new ArrayList<String>();
                                                        loUpdateValue.mePropertyTag = new ArrayList<String>();
                                                        loUpdateValue.mePropertyContent = new ArrayList<String>();
                                                    
                                                        for (int z = 0; z < nodesPropertys.getLength(); z++)
                                                        {
                                                            loUpdateValue.mePropertyName.add(((Attr) nodesPropertys.item(z).getAttributes().getNamedItem("Name")).getValue());
                                                            loUpdateValue.mePropertyTag.add(((Attr) nodesPropertys.item(z).getAttributes().getNamedItem("tag")).getValue()); 
                                                            loUpdateValue.mePropertyContent.add(nodesPropertys.item(z).getTextContent());
                                                        }
                                                    }                    
                                                }
 
                                                //clUpdateValue loData = (clUpdateValue) SerializationUtils.clone(loUpdateValue);
                                                //System.out.println("Insert the following id:" + Integer.valueOf(ii).toString());
                                                meUpdateValues.add(loUpdateValue.copy(loUpdateValue));                                             
                                        }                                                                    
                                }                            
                              }
                        }
                    }
                }
                catch (Exception e) {
                   System.out.println(e.toString());
                } 
    }
       /**
     * @param client the command line arguments
     * @return
     */         
	public static boolean Test_doLogin(WSClient client) {
		if (!client.doLogin(vtigerUSR, vtigerUSRKEY)) {
			System.out.println(client.lastError());
			return false;
		}
		return true;
	}
    /**
     * @param client the command line arguments
     * @return value yes or no
     */           
	public static boolean Test_doQuery(WSClient client) {
		JSONArray result = client.doQuery("SELECT * FROM Accounts");
		if (result == null)
			return false;

		System.out.println("# Result Rows " + result.size());

		System.out.println("# " + client.getResultColumns(result));

		Iterator resultIterator = result.iterator();
		while (resultIterator.hasNext()) {
			JSONObject row = (JSONObject) resultIterator.next();
			Iterator rowIterator = row.keySet().iterator();

			System.out.println("---");
			while (rowIterator.hasNext()) {
				Object key = rowIterator.next();
				Object val = row.get(key);
				System.out.println(" " + key + " : " + val);
			}
		}

		return true;
	}
            /**
     * @param client the command line arguments
     */           
	public static void Test_doListTypes(WSClient client) {
		Map result = client.doListTypes();
		if (client.hasError(result)) {
			System.out.println(client.lastError());
		}
		System.out.println(result);
	}
    /**
     * @param client the command line arguments
     */           
	public static void Test_doDescribe(WSClient client) {
		JSONObject result = client.doDescribe("Leads");
		if (client.hasError(result)) {
			System.out.println(client.lastError());
		}
		System.out.println(result);
	}
    /**
     * @param client the command line arguments
     * @return object value
     */           
	public static Object Test_doCreate(WSClient client) {

		Map valueMap = new HashMap();
		valueMap.put("lastname", "Test JLead");
		valueMap.put("company", "Test JCompany");

		JSONObject result = client.doCreate("Leads", valueMap);
                
		if (result == null) {
			System.out.println(client.lastError());
		}
		return result.get("id");
	}
    /**
     * @param client the command line arguments
     * @param record
     */           
	public static void Test_doRetrieve(WSClient client, Object record) {

		JSONObject result = client.doRetrieve(record);
		if (result == null) {
			System.out.println(client.lastError());
		}
		System.out.println(result);
	}
        /**
         * @param client the command line arguments
         */           
	public static void Test_doInvoke(WSClient client) {
		Map params = new HashMap();
		params.put("query", "SELECT * FROM Project;");

		Object result = client.doInvoke("query", params);
		if(client.hasError(result)) {
			System.out.println(client.lastError());
		} else {
			System.out.println(result);
		}
	}
        
        
        public static void set_Projects(WSClient client,String paFile)
        {
            try
            {   
                System.out.println("The file: " + paFile);
                readProjectFile(paFile);
                updateProjectsCRM(client);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();            
            }
        }
    /**
     * @param client the command line arguments
     */      
    static void updateProjectsCRM(WSClient client)
    {
        try
        {
            for(int i = 0;i < meUpdateCRM.size();i++)
            {
                for(int j = 0;j < meUpdateCRM.get(i).meProp.size(); j++)
                {
                        //****************************
                       ArrayList <String> loOrgName = new ArrayList <String>();
                       ArrayList <String> loOrgId = new ArrayList <String>(); 


                        JSONArray resultOrg = client.doQuery("SELECT * FROM " + meUpdateCRM.get(i).meClassName + " WHERE id='" + meUpdateCRM.get(i).meID + "';");
                        if(client.hasError(resultOrg)) {
                                System.out.println(client.lastError());
                        } else {
                                System.out.println(resultOrg);
                        }
                        Iterator resultIteratorOrg = resultOrg.iterator();
                        while (resultIteratorOrg.hasNext()) {
                                JSONObject rowOrg = (JSONObject) resultIteratorOrg.next();
                                Iterator rowIteratorOrg = rowOrg.keySet().iterator();
                                
                                Map valueMap = new HashMap();
                                while (rowIteratorOrg.hasNext()) {

                                        Object key2 = rowIteratorOrg.next();
                                        Object val2 = rowOrg.get(key2);

                                        if (key2.toString().equalsIgnoreCase(meUpdateCRM.get(i).meProp.get(j)))
                                        {   
                                            System.out.println("Value will be updated " + meUpdateCRM.get(i).meProp.get(j) + ": " + meUpdateCRM.get(i).meValue.get(j));
                                            val2 = meUpdateCRM.get(i).meValue.get(j);
                                        }
                                        valueMap.put(key2.toString(), val2);
                                }
                                
                                JSONObject result = client.doUpdate(meUpdateCRM.get(i).meClassName, valueMap);

                                if (result == null) {
                                        System.out.println(client.lastError());
                                }
                                else
                                {
                                    System.out.println("update done");
                                
                                }
                                
                        }                    
                    
                    
                      //****************************
                      //* Get projects for this organisation
                      //****************************
                      /*
                    		Map valueMap = new HashMap();
                                valueMap.put("id", meUpdateCRM.get(i).meID);
                                valueMap.put(meUpdateCRM.get(i).meProp.get(j), meUpdateCRM.get(i).meValue.get(j));
                                JSONObject result = client.doUpdate(meUpdateCRM.get(i).meClassName, valueMap);

                                if (result == null) {
                                        System.out.println(client.lastError());
                                }
                                else
                                {
                                    System.out.println("update done");
                                
                                }
                    */            
                               
                    
                    
                    
                    /*
                      String loUpdateQuery = "UPDATE " +  meUpdateCRM.get(i).meClassName  + " SET " + meUpdateCRM.get(i).meProp.get(j) + "='" + meUpdateCRM.get(i).meValue.get(j) + "' WHERE id='" + meUpdateCRM.get(i).meID + "';";
                      System.out.println(loUpdateQuery);
                      JSONArray result = client.doQuery(loUpdateQuery);
                      if(client.hasError(result)) {
                              System.out.println(client.lastError());
                      } else {
                              System.out.println(result);
                      }
                    */
                }
            }//getProjectDataFromXML();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }    
               
    /**
     * @param client the command line arguments
     */      
        public static void readProjectFile(String paFile)
        {   
            try
            {
                if (meUpdateValues != null)
                {
                    
                    meUpdateCRM = new ArrayList<clUpdateCRM>();
                    System.out.println("Amount of classes: " + Integer.valueOf(meUpdateValues.size()).toString());
                    for (int j = 0; j < meUpdateValues.size(); j++)
                    {
                            clUpdateValue loUpdateValue = (clUpdateValue) meUpdateValues.get(j);


                            String data = readFileAsString(paFile);
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            InputSource is = new InputSource(new StringReader(data));
                            Document doc = dBuilder.parse(is);
                            doc.getDocumentElement().normalize();
                            NodeList rootNode = doc.getChildNodes();
                        
                            
                            
    
                            
                            
                            
                            XPathFactory xpathfactory = XPathFactory.newInstance();
                            
                            XPath xpath = xpathfactory.newXPath();

                            XPathExpression expr = xpath.compile(loUpdateValue.meLevelTag);
                            Object result = expr.evaluate(doc, XPathConstants.NODESET);
                            NodeList nodes = (NodeList) result;
                            System.out.println("Amount of levels for tag " + loUpdateValue.meLevelTag + ": " + Integer.valueOf(nodes.getLength()));
                            for (int i = 0; i < nodes.getLength(); i++) 
                            {
                                clUpdateCRM loUpdateCRM = new clUpdateCRM();
                                Node loTaskNode = nodes.item(i);
                                
                                
                                XPathExpression exprID = xpath.compile(loUpdateValue.meIdTag);
                                Object resultID = exprID.evaluate(loTaskNode, XPathConstants.NODESET);
                                NodeList nodesID = (NodeList) resultID;
                                
                                loUpdateCRM.meID = nodesID.item(0).getTextContent();
                                loUpdateCRM.meClassName = loUpdateValue.meClassName;
                                
                                loUpdateCRM.meProp = new ArrayList<String>();
                                loUpdateCRM.meValue = new ArrayList<String>();
                                System.out.println("Reading the properties for id: " + loUpdateCRM.meID + " from class name: " + loUpdateCRM.meClassName);
                                for (int z = 0; z < loUpdateValue.mePropertyName.size(); z++)
                                {   
                                    System.out.println("Property: " + Integer.valueOf(z).toString() + " with property tag:" + loUpdateValue.mePropertyTag.get(z));
                                    XPathExpression expr1 = xpath.compile(loUpdateValue.mePropertyTag.get(z));
                                    Object result1 = expr1.evaluate(loTaskNode, XPathConstants.NODESET);
                                    NodeList nodesProps = (NodeList) result1;
                                    
                                    System.out.println("Amount of nodesProps:" + Integer.valueOf(nodesProps.getLength()).toString());
                                    System.out.println("Storing property name:" + loUpdateValue.mePropertyName.get(z));                                    
                                    loUpdateCRM.meProp.add(loUpdateValue.mePropertyName.get(z));
                                    System.out.println("After storing property name:" + loUpdateValue.mePropertyName.get(z));                                    
                                    ScriptEngineManager manager = new ScriptEngineManager();                                   
                                    ScriptEngine engine = manager.getEngineByName("javascript");
                                    System.out.println("Value before: " + nodesProps.item(0).getTextContent());
                                    System.out.println("Property script: " + loUpdateValue.mePropertyContent.get(z));
                                    engine.put("value", nodesProps.item(0).getTextContent());
                                    engine.eval(loUpdateValue.mePropertyContent.get(z));
                                    String loValue = (String) engine.get("value");
                                    System.out.println("Value after: " + loValue);    
                                    loUpdateCRM.meValue.add(new String(loValue));
                                }
                                meUpdateCRM.add(loUpdateCRM.copy(loUpdateCRM));
                            }
                 
                    }
                }     
            }
            catch(Exception ex)
            {
                System.out.println(ex.toString());
            }
        }
    /**
     * @param client the command line arguments
     */           
	public static void get_Projects(WSClient client) {
            
            try
            {
                
                //****************************
                //* Get projects for this organisation
                //****************************
		//JSONArray result = client.doQuery("SELECT * FROM Invoice;");
		//if(client.hasError(result)) {
		//	System.out.println(client.lastError());
		//} else {
		//	System.out.println(result);
		//}                
                
                
                //****************************
                //* Store users
                //****************************
                String loUsers = "";
                loUsers = doGet_Users(client);
                loUsers = "<root>" + loUsers + "</root>";
                
                
                //****************************
                //* Getting the projects
                //****************************
                String loProject = "";
                loProject = doGet_Projects(client,loUsers);
                loProject = "<root>" + loProject + "</root>"; 
                
                
                //****************************
                //* Setting the assignments
                //****************************                
                meAssignments = "<root>" + meAssignments + "</root>"; 
                
                
                
                //****************************
                //* Getting the projects
                //****************************
                String loProjects = doGetFinalXML(client,loUsers,loProject);
                
                //****************************
                //* Write out the file 
                //****************************                        
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                LocalDateTime now = LocalDateTime.now();
                AppendStringUsingBufferedWritter(loProjects,"vtiger-export_" + dtf.format(now) + ".xml");
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
	}
   
        public static String doGetFinalXML(WSClient client, String paUsers, String paProjects)
        {
            try
            {   
                String data = readFileAsString("templates\\Projects.xml");
                data = addTasksToProject(paProjects,data);                
                data = addUsersToProject(data, paUsers);
                data = addAssignmentsToProject(data,meAssignments);
                return data;
            }
            catch(Exception ex)
            {
                return "";
            }
        }
        
     /**
     * @param client the command line arguments
     * @param loUsers
     */           
	public static String doGet_Projects(WSClient client,String paUsers) 
        {
            //****************************
            //* Get the organizations
            //****************************
           ArrayList <String> loOrgName = new ArrayList <String>();
           ArrayList <String> loOrgId = new ArrayList <String>(); 
           

            JSONArray resultOrg = client.doQuery(vtigerAccountsQuery);
            if(client.hasError(resultOrg)) {
                    System.out.println(client.lastError());
            } else {
                    System.out.println(resultOrg);
            }
            Iterator resultIteratorOrg = resultOrg.iterator();
            while (resultIteratorOrg.hasNext()) {
                    JSONObject rowOrg = (JSONObject) resultIteratorOrg.next();
                    Iterator rowIteratorOrg = rowOrg.keySet().iterator();

                    while (rowIteratorOrg.hasNext()) {

                            Object key2 = rowIteratorOrg.next();
                            Object val2 = rowOrg.get(key2);
                            
                            if (key2.toString().equalsIgnoreCase("accountname"))
                            {                                
                                loOrgName.add(val2.toString());
                            }
                            else if (key2.toString().equalsIgnoreCase("id"))
                            {
                                loOrgId.add(val2.toString());
                            }
                    }
            }




            String loProjects = "";
            for (int l=0; l < loOrgId.size();l++)
            {
                //****************************
                //* Get projects for this organisation
                //****************************
		JSONArray result = client.doQuery("SELECT * FROM Project WHERE linktoaccountscontacts='" + loOrgId.get(l) + "'" + vtigerProjectsQueryExtension + ";");
		if(client.hasError(result)) {
			System.out.println(client.lastError());
		} else {
			System.out.println(result);
		}
                
                
                if (result != null){

                    System.out.println("# Result Rows " + result.size());

                    System.out.println("# " + client.getResultColumns(result));

                    //For each project
                    Iterator resultIterator = result.iterator();
                    int counterProject = 0;
                    

                    if (result.size() > 0){
                        
                            //***********************************
                            //* Small intervention to set the properties
                            //***************************************
                            
                            String loProject = do_CreateOrganisation(loOrgId.get(l),loOrgName.get(l),l);
                            
                            JSONArray resultOrgTemp = client.doQuery("SELECT * FROM Accounts WHERE id='" + loOrgId.get(l) + "'");
                            if(client.hasError(resultOrg)) {
                                    System.out.println(client.lastError());
                            } else {
                                    System.out.println(resultOrg);
                            }
                            Iterator resultIteratorOrgTemp = resultOrgTemp.iterator();
                            while (resultIteratorOrgTemp.hasNext()) {
                                    JSONObject rowOrgTemp = (JSONObject) resultIteratorOrgTemp.next();
                                    Iterator rowIteratorOrgTemp = rowOrgTemp.keySet().iterator();

                                    while (rowIteratorOrgTemp.hasNext()) {

                                            Object keyTemp = rowIteratorOrgTemp.next();
                                            Object valTemp = rowOrgTemp.get(keyTemp);
                                            loProject = loProject.replace("$$" + keyTemp.toString() + "$$",valTemp.toString());
                                    }
                            }                        
                        
                        
                        
                        
                        loProjects = loProjects + loProject;
                    }


                    while (resultIterator.hasNext()) {
                        String loProject = do_CreateProject((JSONObject) resultIterator.next(),client, counterProject,l,loOrgId.get(l),loOrgName.get(l),paUsers);
                        loProjects = loProjects + loProject;
                        counterProject = counterProject + 1;
                    }
                }
                
            }
            return loProjects;
        }
     /**
     * @param client the command line arguments
     */           
	public static String do_CreateOrganisation(String paOrgId, String paOrgName, int paOrgNumber) {
            try
            {   
                String data = readFileAsString("templates\\Account.xml");

                data = data.replace("$$" + "accountname" + "$$",paOrgName);
                data = data.replace("$$" + "id" + "$$",paOrgId);
                        
                data = setWBSAccount(data,paOrgNumber);                
                data = setGUI_ID(data, paOrgNumber + 1);
                meTaskID = meTaskID + 1;
                return data;
            }
            catch(Exception ex)
            {
                return "";
            }
	}        
        
     /**
     * @param client the command line arguments
     */           
	public static String doGet_Users(WSClient client) {
                //****************************
                //* Store users
                //****************************
                String loUsers = "";
                
		JSONArray result2 = client.doQuery(vtigerUsersQuery);
		if(client.hasError(result2)) {
			System.out.println(client.lastError());
		} else {
			System.out.println(result2);
		}
                
                System.out.println("# Result Rows " + result2.size());

		System.out.println("# " + client.getResultColumns(result2));

		Iterator resultIterator2 = result2.iterator();
                int counterUser = 0;
                
		while (resultIterator2.hasNext()) {
			JSONObject row2 = (JSONObject) resultIterator2.next();
                        //Get user data
			String user_data = do_CreateUsers(row2,counterUser);
                        //Set GUID and ID of user data
                        user_data = setGUI_ID_USER(user_data,counterUser + 1);
                        counterUser = counterUser + 1;
                        //Adding userdata to all user data
                        loUsers = loUsers + user_data;       
		}
                return loUsers;
	}
           
    /**
     * @param paObject the command line arguments
     * @param client
     * @param paCounter
     * @param paOrgNumber
     * @param paOrganisationVtigerId
     * @param paOrgName
     * @return
     */         
        public static String do_CreateProject(JSONObject paObject, WSClient client, int paCounter, int paOrgNumber, String paOrganisationVtigerId, String paOrgName, String paUsers)
        {
            try
            {
                
                
                Iterator rowIterator = paObject.keySet().iterator();
                
                
                String data = readFileAsString("templates\\Project.xml");


                Object loProjectId = null;
                
                //System.out.println("---");
                while (rowIterator.hasNext()) {
                        Object key = rowIterator.next();
                        Object val = paObject.get(key);
                        //System.out.println(" " + key + " : " + val);
                        //Replace the data in the string
                        data = data.replace("$$" + key.toString() + "$$",val.toString());
                        
                        if  (key.toString().equalsIgnoreCase("id"))
                        {
                            loProjectId = val;
                        }
                }
                data = setWBSProject(data,paCounter,paOrgNumber);                
                data = setGUI_ID(data,Integer.parseInt(Integer.toString(paOrgNumber + 1) + Integer.toString(paCounter + 1)));
                meTaskID = meTaskID + 1;
                String Task_data = do_CreateTask(loProjectId,client,paCounter,paOrgNumber, paUsers);
                data = data + Task_data;
                
                return data;
            }
            catch(Exception ex)
            {
                return "";
            }
                        
        }
     /**
     * @param paProjectId the command line arguments
     * @param client
     * @param paProjectCounter
     * @return 
     */ 
        public static String do_CreateTask(Object paProjectId, WSClient client, int paProjectCounter, int paOrgNumber, String paUsers)
        {       
                
            try
            {    
                String loReturnTasks = "";
                String loReturnAssignments = "";
                
                //System.out.println("SELECT * FROM ProjectTask WHERE projectid='" + paProjectId.toString() + "';");
                JSONArray result2 = client.doQuery("SELECT * FROM ProjectTask WHERE projectid='" + paProjectId.toString() + "'" + vtigerProjectsTasksQueryExtension + ";");
		if(client.hasError(result2)) {
			System.out.println(client.lastError());
		} else {
			System.out.println(result2);
		}
          
                System.out.println("#Result Rows " + result2.size());

		System.out.println("# " + client.getResultColumns(result2));

		Iterator resultIterator2 = result2.iterator();
                int counterTask = 0;
		while (resultIterator2.hasNext()) {
			JSONObject row2 = (JSONObject) resultIterator2.next();
			Iterator rowIterator2 = row2.keySet().iterator();
                        String loTask_data = readFileAsString("templates\\ProjectTask.xml");
			
                        System.out.println("---");
                        
                        String loUserId = "";
                        
			while (rowIterator2.hasNext()) {
                                
				Object key2 = rowIterator2.next();
				Object val2 = row2.get(key2);
				//System.out.println(" " + key2 + " : " + val2);
                                
                                loTask_data = loTask_data.replace("$$" + key2.toString() + "$$",val2.toString());
                                
                                if (key2.toString().equalsIgnoreCase("assigned_user_id"))
                                {
                                    loUserId = val2.toString();
                                }
			}
                        
                        //Set the task
                        loTask_data = setWBSTask(loTask_data, counterTask, paProjectCounter, paOrgNumber);
                        loTask_data = setGUI_ID(loTask_data, Integer.parseInt(Integer.toString(paOrgNumber + 1) + Integer.toString(paProjectCounter + 1) + Integer.toString(counterTask + 1)));
                        meTaskID = meTaskID + 1;
                        loReturnTasks = loReturnTasks + loTask_data;
                        
                        //Create the assignment
                        String loUserUID = getUserUIDfromXML(loUserId,paUsers);
                        String loAssignment = do_CreateAssignment(loUserUID,Integer.toString(paOrgNumber + 1) + Integer.toString(paProjectCounter + 1) + Integer.toString(counterTask + 1));
                        loAssignment = setGUI_ID_ASSIGNMENT(loAssignment, Integer.parseInt(Integer.toString(paOrgNumber + 1) + Integer.toString(paProjectCounter + 1) + Integer.toString(counterTask + 1)));
                        meAssignmentID = meAssignmentID + 1;
                        loReturnAssignments = loReturnAssignments + loAssignment;
                        
                        counterTask = counterTask + 1;
                        
		}
                meAssignments = meAssignments + loReturnAssignments;
                return loReturnTasks;
            }
            catch(IOException ex)
            {
                System.out.println(ex.toString());
                return "";
            }
            catch(Exception ex)
            {
                return "";
            }
            
        }

        public static String do_CreateAssignment(String paUserUID, String paTaskUID)
        {
                try 
                {
                   String loAssignment_data = readFileAsString("templates\\Assignment.xml");
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(loAssignment_data));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("TaskUID"))
                                {   
                                    node.setTextContent(paTaskUID);
                                }
                                else if (node.getNodeName().equalsIgnoreCase("ResourceUID"))
                                {
                                    node.setTextContent(paUserUID);
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                    
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }  
        }                
        
        public static String getUserUIDfromXML(String paUserID, String paSource)
        {       
            try{
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paSource));
                   Document doc = dBuilder.parse(is);                
                   doc.getDocumentElement().normalize();
                   NodeList nodesA = doc.getChildNodes();                
                   NodeList nodes = nodesA.item(0).getChildNodes();                

                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        String loUID = "";
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("UID"))
                                {
                                  loUID = node.getTextContent();
                                }
                                if (node.getNodeName().equalsIgnoreCase("ExtendedAttribute"))
                                {
                                    NodeList nodesVtigerID = node.getChildNodes();
                                    for(int k = 0; k < nodesVtigerID.getLength();k++)
                                    {
                                        if (nodesVtigerID.item(k).getNodeName().equalsIgnoreCase("Value"))
                                        {
                                            if (nodesVtigerID.item(k).getTextContent().equalsIgnoreCase(paUserID))
                                            {
                                                System.out.println("Returning UID:" + loUID + " for vtigerid:" + paUserID);
                                                return loUID;
                                            }
                                        }
                                    }
                                }                                
                              }
                        }
                    }
                    
                    return "";                  
                

            }
            catch (Exception e) {
               System.out.println(e.toString());
               return "";
            }   
        }
        
        public static String do_CreateUsers(JSONObject paObject, int index)
        {       
            try{
                String loResource_data = readFileAsString("templates\\Resource.xml");
        
                Iterator rowIterator2 = paObject.keySet().iterator();

                //System.out.println("---");
                while (rowIterator2.hasNext()) {

                        Object key2 = rowIterator2.next();
                        Object val2 = paObject.get(key2);

                        loResource_data = loResource_data.replace("$$" + key2.toString() + "$$",val2.toString());
                }

                return loResource_data;
            }
            catch (Exception ex) 
            {
              return "";
            } 
        }
        

        public static String setWBSTask(String data,int paID, int paProjectID, int paOrgNumber)
        {
                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(data));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("WBS"))
                                {
                                    node.setTextContent(Integer.toString(paOrgNumber + 1) + "." + Integer.toString(paProjectID + 1) + "." + Integer.toString(paID + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineNumber"))
                                {
                                    node.setTextContent(Integer.toString(paOrgNumber + 1) + "." +Integer.toString(paProjectID + 1) + "." + Integer.toString(paID + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineLevel"))
                                {
                                    node.setTextContent(Integer.toString(3));
                                }
                              }
                        }
                    }
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }         
        }        
        
        public static String setWBSProject(String data,int paProjectId, int paOrgId)
        {
                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(data));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("WBS"))
                                {
                                    node.setTextContent(Integer.toString(paOrgId + 1) + "." + Integer.toString(paProjectId + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineNumber"))
                                {
                                    node.setTextContent(Integer.toString(paOrgId + 1) + "." + Integer.toString(paProjectId + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineLevel"))
                                {
                                    node.setTextContent(Integer.toString(2));
                                }
                              }
                        }
                    }
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }         
        }

        public static String setWBSAccount(String data,int paID)
        {
                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(data));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("WBS"))
                                {
                                    node.setTextContent(Integer.toString(paID + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineNumber"))
                                {
                                    node.setTextContent(Integer.toString(paID + 1));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("OutlineLevel"))
                                {
                                    node.setTextContent(Integer.toString(1));
                                }
                              }
                        }
                    }
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }         
        }        
        /**
        * @param 
        * @param
        * @return
        */         
        public static String addTasksToProject(String paTasks, String paProject)
        {

            try
            {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paProject));
                   Document doc = dBuilder.parse(is);                
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();                
                
                
                   DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
                   InputSource is1 = new InputSource(new StringReader(paTasks));
                   Document doc1 = dBuilder1.parse(is1);
                   doc1.getDocumentElement().normalize();
                   NodeList nodesA = doc1.getChildNodes();
                   NodeList nodes1 = nodesA.item(0).getChildNodes();                   
               
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("Tasks"))
                                {
                                    //Adding the tasks
                                    for (int k = 0; k < nodes1.getLength();k++)
                                    {
                                        Node copyedNode = doc.importNode(nodes1.item(k), true);
                                        node.appendChild(copyedNode);
                                    }
                                }
                              }
                        }
                    }                    
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();                  
                

            }
            catch (Exception e) {
               System.out.println(e.toString());
               return "";
            }    
            
        }
        
        /**
        * @param 
        * @param 
        * @return
        */         
        
        public static String addUsersToProject(String paProject, String paUsers)
        {    
            try
            {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paProject));
                   Document doc = dBuilder.parse(is);                
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();                
                
                
                   DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
                   InputSource is1 = new InputSource(new StringReader(paUsers));
                   Document doc1 = dBuilder1.parse(is1);
                   doc1.getDocumentElement().normalize();
                   NodeList nodesA = doc1.getChildNodes();
                   NodeList nodes1 = nodesA.item(0).getChildNodes();
                   
                   
                   
                   
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("Resources"))
                                {
                                    //Adding the tasks
                                    for (int k = 0; k < nodes1.getLength();k++)
                                    {
                                        Node copyedNode = doc.importNode(nodes1.item(k), true);
                                        node.appendChild(copyedNode);
                                    }
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();                  
                

            }
            catch (Exception e) {
               System.out.println(e.toString());
               return "";
            }        
        }
        public static String addAssignmentsToProject(String paProject, String paAssignments)
        {    
            try
            {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paProject));
                   Document doc = dBuilder.parse(is);                
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();                
                
                
                   DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
                   InputSource is1 = new InputSource(new StringReader(paAssignments));
                   Document doc1 = dBuilder1.parse(is1);
                   doc1.getDocumentElement().normalize();
                   NodeList nodesA = doc1.getChildNodes();
                   NodeList nodes1 = nodesA.item(0).getChildNodes();
                   
                   
                   
                   
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("Assignments"))
                                {
                                    //Adding the tasks
                                    for (int k = 0; k < nodes1.getLength();k++)
                                    {
                                        Node copyedNode = doc.importNode(nodes1.item(k), true);
                                        node.appendChild(copyedNode);
                                    }
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();                  
                

            }
            catch (Exception e) {
               System.out.println(e.toString());
               return "";
            }        
        }


        
        /**
        * @param 
        * @return
        */         
        public static String readFileAsString(String fileName) throws Exception 
          { 
            String data = ""; 
            data = new String(Files.readAllBytes(Paths.get(fileName))); 
            return data; 
          } 
        
public static String setGUI_ID_ASSIGNMENT(String paText,int paID)
        {

                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paText));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("UID"))
                                {
                                    node.setTextContent(Integer.toString(paID));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("ID"))
                                {
                                    node.setTextContent(Integer.toString(meAssignmentID + 1));
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                    
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }            
       
    }                
        public static String setGUI_ID_USER(String paText,int paID)
        {

                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paText));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("UID"))
                                {
                                    node.setTextContent(Integer.toString(paID));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("ID"))
                                {
                                    node.setTextContent(Integer.toString(paID));
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                    
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }            
       
    }        
        public static String setGUI_ID(String paText,int paID)
        {

                try 
                {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   InputSource is = new InputSource(new StringReader(paText));
                   Document doc = dBuilder.parse(is);
                   doc.getDocumentElement().normalize();
                   NodeList nodes = doc.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        NodeList nodesChilds = nodes.item(i).getChildNodes();
                        for(int j = 0;j < nodesChilds.getLength();j++)
                        {
                            
                            Node node = nodesChilds.item(j);           
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equalsIgnoreCase("UID"))
                                {
                                    node.setTextContent(Integer.toString(paID));
                                }
                                else if (node.getNodeName().equalsIgnoreCase("ID"))
                                {
                                    node.setTextContent(Integer.toString(meTaskID + 1));
                                }
                              }
                        }
                    }
                    
                    return documentToString(doc).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();  
                    
                } 
                catch (Exception e) {
                   System.out.println(e.toString());
                   return "";
                }            
       
    }
        /**
        * @param document
        * @return 
        */    
    public static String documentToString(Document document) {
        try 
        {
          TransformerFactory tf = TransformerFactory.newInstance();
          Transformer trans = tf.newTransformer();
          StringWriter sw = new StringWriter();
          trans.transform(new DOMSource(document), new StreamResult(sw));
          return sw.toString();
        } catch (TransformerException tEx) {
          System.out.println(tEx.toString());
        }
        return null;
    }   
    /**
     * @param str
     * @param fileName
     * @throws IOException 
     */
    static public void AppendStringUsingBufferedWritter(String str, String fileName) throws IOException 
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append(str);
        writer.close();
    }
}
