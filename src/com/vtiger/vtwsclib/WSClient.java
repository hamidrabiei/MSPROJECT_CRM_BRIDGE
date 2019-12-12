/*+**********************************************************************************
 * The contents of this file are subject to the vtiger CRM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License
 * The Original Code is:  vtiger CRM Open Source
 * The Initial Developer of the Original Code is vtiger.
 * Portions created by vtiger are Copyright (C) vtiger.
 * All Rights Reserved.
 ************************************************************************************/
package com.vtiger.vtwsclib;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vtiger.vtwsclib.helpers.HTTP_Client;

/**
 * Vtiger Webservice Client
 * @author prasad
 *
 */

public class WSClient {
	
	// Webservice file
	String _servicebase = "webservice.php";
	
	// HTTP Client instance
	HTTP_Client _client;
	// Service URL to which client connects to
	String _serviceurl;
	
	// Webservice User credentials
	String _serviceuser;
	String _servicekey;
	
	// Webservice login validity
	String _servertime;
	String _expiretime;
	String _servicetoken;
	
	// Webservice login credentials
	String _sessionid;
	Object _userid;
	
	// Last operation error information
	Object _lasterror;
	
	/**
	 * Constructor
	 */
	public WSClient(String url) {
		_serviceurl = getWebServiceURL(url);
		_client = new HTTP_Client(_serviceurl);
	}
	
	/**
	 * Reinitialize the client.
	 */
	public void reinitailize() {
		_client = new HTTP_Client(_serviceurl);
	}
	
	/**
	 * Get the URL for sending webservice request.
	 * @param url
	 * @return
	 */
	protected String getWebServiceURL(String url) {
		if(!url.endsWith("/")) {
			url = url + "/";
		}
		return url + _servicebase;
	}
	
	/**
	 * Get actual record id from the response id.
	 * @param id
	 * @return
	 */
	public Object getId(String id) {
		String[] splits = id.split("x");
		return splits[1];
	}
	
	/**
	 * Check if result has any error.
	 * @param result
	 * @return
	 */
	public boolean hasError(Object result) {
		boolean isError = false;

		try {
			if(result == null) {
				isError = true;
			} else if(result instanceof Exception) {
				_lasterror = ((Exception)result).getMessage();
				isError = true;
			} else if(result instanceof JSONObject) {
				JSONObject resultObject = (JSONObject)result;
				if(resultObject.get("success").toString() == "false") {
					_lasterror = resultObject.get("error");
					isError = true;
				}
			}
		} catch(Exception ex) {
			// TODO
		}
		return isError;
	}

	/**
	 * Get last operation error
	 * @return
	 */
	public Object lastError() {
		return _lasterror;
	}
	
	protected boolean __doChallenge(String username) {
		
		Map getdata = new HashMap();
		getdata.put("operation", "getchallenge");
		getdata.put("username", username);
		
		Object response = _client.doGet(getdata, true);
		if(hasError(response)) {
			return false;
		}
		
		JSONObject result = (JSONObject)((JSONObject)response).get("result");
				
		_servertime = result.get("serverTime").toString();
		_expiretime = result.get("expireTime").toString();
		_servicetoken = result.get("token").toString();
		
		return true;		
	}
	
	/**
	 * Check and perform login if required.
	 */
	protected void checkLogin() {
		// TODO
	}
	
	/**
	 * Generate MD5 (in hex)
	 * @param input
	 * @return
	 * @throws Exception
	 */
	protected String md5Hex(String input) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(input.getBytes());
		return String.format("%032x", new BigInteger(1, hash));
	}
	
	/**
	 * JSONify input data.
	 * @param input
	 * @return
	 */
	public Object toJSON(String input) {
		return _client.__jsondecode(input);
	}
	
	/**
	 * Convert input data to JSON String.
	 * @param input
	 * @return
	 */
	public String toJSONString(Object input) {
		return _client.__jsonencode(input);		
	}
	
	/**
	 * Do Login Operation
	 * @param username
	 * @param vtigerUserAccessKey
	 * @return
	 */
	public boolean doLogin(String username, String vtigerUserAccessKey) {
		if(!__doChallenge(username)) return false;
		
		try {
			Map postdata = new HashMap();
			postdata.put("operation", "login");
			postdata.put("username", username);
			postdata.put("accessKey", md5Hex(_servicetoken + vtigerUserAccessKey));
			
			Object response = _client.doPost(postdata, true);
			if(hasError(response)) {
				return false;
			}
			
			JSONObject result = (JSONObject)((JSONObject)response).get("result");
			
			_serviceuser = username;
			_servicekey  = vtigerUserAccessKey;
			
			_sessionid = result.get("sessionName").toString();
			_userid    = result.get("userId").toString();
			
			return true;
			
		} catch(Exception ex) {
			hasError(ex);
			return false;
		}
	}
	
	/**
	 * Do Query operation.
	 * @param query
	 * @return
	 */
	public JSONArray doQuery(String query) {
		// Perform re-login if required.
		checkLogin();
		
		if(query.trim().endsWith(";") == false) {
			query += ";";
		}
		
		Map getdata = new HashMap();
		getdata.put("operation", "query");
		getdata.put("sessionName", _sessionid);
		getdata.put("query", query);
		
		Object response = _client.doGet(getdata, true);
		if(hasError(response)) {
			return null;
		}
		
		JSONArray result = (JSONArray)((JSONObject)response).get("result");
		return result;
	}
	
	/**
	 * Get Result Column Names.
	 * @param result
	 * @return
	 */
	public List getResultColumns(JSONArray result) {
		List columns = new ArrayList();
		if(!result.isEmpty()) {
			JSONObject row = (JSONObject)result.get(0);
			Iterator iterator = row.keySet().iterator();
			while(iterator.hasNext()) {
				columns.add(iterator.next().toString());
			}
		}
		return columns;
	}
	
	/**
	 * List types of available Modules.
	 * @return
	 */
	public Map doListTypes() {
		// Perform re-login if required.
		checkLogin();
		
		Map getdata = new HashMap();
		getdata.put("operation", "listtypes");
		getdata.put("sessionName", _sessionid);
		
		Object response = _client.doGet(getdata, true);
		if(hasError(response)) {
			return null;
		}
		
		JSONObject result = (JSONObject)((JSONObject)response).get("result");
		
		JSONArray resultTypes = (JSONArray)result.get("types");
		
		Map returnvalue = new HashMap();
		Iterator iterator = resultTypes.iterator();
		while(iterator.hasNext()) {
			Object value = iterator.next();
			
			Map returnpart = new HashMap();
			returnpart.put("name", value.toString());
			returnvalue.put(value, returnpart);
		}
		
		return returnvalue;
	}
	
	/**
	 * Describe Module Fields.
	 * @param module
	 * @return
	 */
	public JSONObject doDescribe(String module) {
		// Perform re-login if required.
		checkLogin();
		
		Map getdata = new HashMap();
		getdata.put("operation", "describe");
		getdata.put("sessionName", _sessionid);
		getdata.put("elementType", module);
		
		Object response = _client.doGet(getdata, true);

		if(hasError(response)) {
			return null;
		}
		
		JSONObject result = (JSONObject)((JSONObject)response).get("result");
		
		return result;
	}
	
	/**
	 * Retrieve details of record.
	 * @param record
	 * @return
	 */
	public JSONObject doRetrieve(Object record) {
		// Perform re-login if required.
		checkLogin();
		
		Map getdata = new HashMap();
		getdata.put("operation", "retrieve");
		getdata.put("sessionName", _sessionid);
		getdata.put("id", record);
		
		Object response = _client.doGet(getdata, true);
		if(hasError(response)) {
			return null;
		}
		
		JSONObject result = (JSONObject)((JSONObject)response).get("result");
		
		return result;
	}
	
	/**
	 * Do Create Operation
	 * @param module
	 * @param valueMap
	 * @return
	 */
	public JSONObject doCreate(String module, Map valueMap) {
		// Perform re-login if required.
		checkLogin();
		
		// Assign record to logged in user if not specified
		if(!valueMap.containsKey("assigned_user_id")) {
			valueMap.put("assigned_user_id", _userid);
		}
		
		Map postdata = new HashMap();
		postdata.put("operation", "create");
		postdata.put("sessionName", _sessionid);
		postdata.put("elementType", module);
		postdata.put("element", toJSONString(valueMap));
		
		Object response = _client.doPost(postdata, true);
		if(hasError(response)) {
			return null;
		}
		JSONObject result = (JSONObject)((JSONObject)response).get("result");
		
		return result;
	}
	
	 public JSONObject doUpdate(String module, Map valueMap)
	    {
	        checkLogin();
	        if(!valueMap.containsKey("assigned_user_id"))
	            valueMap.put("assigned_user_id", _userid);
	        Map postdata = new HashMap();
	        postdata.put("operation", "update");
	        postdata.put("sessionName", _sessionid);
	        postdata.put("elementType", module);
	        postdata.put("element", toJSONString(valueMap));
	        Object response = _client.doPost(postdata, true);
	        if(hasError(response))
	        {
	            return null;
	        } else
	        {
	            JSONObject result = (JSONObject)((JSONObject)response).get("result");
	            return result;
	        }
	      }
	
	public Object doInvoke(String method, Object params) {
		return doInvoke(method, params, "GET");
	}
	
	/**
	 * Invoke custom operation
	 *
	 * @param method Name of the webservice to invoke
	 * @param params null or parameter values to method
	 * @param type POST/GET HTTP method to use
	 * @return
	 */
	public Object doInvoke(String method, Object params, String type) {
		// Perform re-login if required.
		checkLogin();
		
		Map senddata = new HashMap();
		senddata.put("operation", method);
		senddata.put("sessionName", _sessionid);
		
		if(params != null) {
			Map valueMap = (Map) params;
			if(!valueMap.isEmpty()) {
				Iterator iterator = valueMap.keySet().iterator();
				while(iterator.hasNext()) {
					Object key = iterator.next();
					if(!senddata.containsKey(key)) {
						senddata.put(key, valueMap.get(key));
					}
				}
			}
		}
		
		Object response = null;
		
		if(type.toUpperCase() == "POST") {
			response = _client.doPost(senddata, true);
		} else {
			response = _client.doGet(senddata, true);
		}
		
		if(hasError(response)) {
			return null;
		}
		
		Object result = ((JSONObject)response).get("result");
		
		return result;
	}
	
}
