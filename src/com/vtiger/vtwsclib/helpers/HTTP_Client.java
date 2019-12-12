/*+**********************************************************************************
 * The contents of this file are subject to the vtiger CRM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License
 * The Original Code is:  vtiger CRM Open Source
 * The Initial Developer of the Original Code is vtiger.
 * Portions created by vtiger are Copyright (C) vtiger.
 * All Rights Reserved.
 ************************************************************************************/
package com.vtiger.vtwsclib.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;

/**
 * HTTP Client library wrapper.
 * @author prasad
 *
 */
public class HTTP_Client extends DefaultHttpClient {
	private String _serviceurl;

	/**
	 * Constructor
	 * @param url
	 */
	public HTTP_Client(String url) {
		_serviceurl = url;		
	}
	
	/**
	 * Destructor
	 */
	protected void finalize() throws Throwable {
		this.getConnectionManager().shutdown();
		super.finalize();
	}
	
	/**
	 * Perform HTTP GET operation
	 * @param data
	 * @return
	 */
	public Object doGet(Object data) {
		return doGet(data, false);
	}
	
	/**
	 * Perform HTTP GET operation
	 * @param data
	 * @param convertToJSON
	 * @return
	 */
	public Object doGet(Object data, boolean convertToJSON) {				
		try {
			String uri = _serviceurl;
			
			if(data != null) {				
				if(!uri.endsWith("?")) {
					uri += "?";
				}				
				if(data instanceof String) {
					uri += data;
				} else if(data instanceof Map) {
					List params = new ArrayList();
					Map dataMap = (Map)data;
					Iterator iterator = dataMap.keySet().iterator();
					while(iterator.hasNext()) {
						Object key = iterator.next();
						params.add(new BasicNameValuePair(key.toString(), (String)dataMap.get(key)));
					}
					uri += URLEncodedUtils.format(params, "UTF-8");
				}
			}

			HttpGet httpGet = new HttpGet(uri);
			
			HttpResponse httpResponse = this.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			String response = EntityUtils.toString(httpEntity);
			
			if(convertToJSON) {
				return __jsondecode(response);
			}
			
			return response;
			
		} catch(Exception ex) {
			return ex;
		}		
	}
	
	/**
	 * Perform HTTP POST operation
	 * @param data
	 * @return
	 */
	public Object doPost(Object data) {
		return doPost(data, false);
	}
	
	/**
	 * Perform HTTP POST operation
	 * @param data
	 * @param convertToJSON
	 * @return
	 */
	public Object doPost(Object data, boolean convertToJSON) {
		try {
			String uri = _serviceurl;
			
			HttpPost httpPost = new HttpPost(uri);
			List params = new ArrayList();
			
			if(data instanceof Map) {
				Map dataMap = (Map)data;
				Iterator iterator = dataMap.keySet().iterator();
				while(iterator.hasNext()) {
					Object key = iterator.next();
					params.add(new BasicNameValuePair(key.toString(), (String)dataMap.get(key)));
				}
			}
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = this.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String response = EntityUtils.toString(httpEntity);
			
			if(convertToJSON) {
				return __jsondecode(response);
			}
			
			return response;
			
		} catch(Exception ex) {
			return ex;
		}
	}
	
	/**
	 * Decode String to JSON
	 * @param input
	 * @return
	 */
	public Object __jsondecode(String input) {
		return JSONValue.parse(input);
	}
	
	/**
	 * Encode Object to JSON String
	 * @param input
	 * @return
	 */
	public String __jsonencode(Object input) {
		return JSONValue.toJSONString(input);
	}
	
}
