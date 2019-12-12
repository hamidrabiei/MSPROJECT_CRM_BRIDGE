/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wva.vtiger;

import java.util.List;
import java.io.Serializable;
/**
 *
 * @author Wouter Vandorpe
 */
public class clUpdateValue implements Cloneable{
    
     String meClassName;
    
     String meIdName;
     String meIdTag;
     String meIdContent;
     String meLevelName;
     String meLevelTag;
     String meLevelContent;
     List<String> mePropertyName = null;
     List<String> mePropertyTag = null;
     List<String> mePropertyContent = null;
    
    public clUpdateValue()
    {
        
    }  
    public clUpdateValue clone() throws CloneNotSupportedException {
            clUpdateValue loUpdateValue = (clUpdateValue) super.clone();
            loUpdateValue.meClassName = new String(this.meClassName);
            loUpdateValue.meIdName = new String(this.meIdName);
            loUpdateValue.meIdTag = new String(this.meIdTag);
            loUpdateValue.meIdContent = new String(this.meIdContent);
            loUpdateValue.meLevelName = new String(this.meLevelName);
            loUpdateValue.meLevelTag = new String(this.meLevelTag);
            loUpdateValue.meLevelContent = new String(this.meLevelContent);
            loUpdateValue.mePropertyName= this.mePropertyName;
            loUpdateValue.mePropertyTag= this.mePropertyTag;
            loUpdateValue.mePropertyContent = this.mePropertyContent;      
            return loUpdateValue;
    } 
    public clUpdateValue copy(clUpdateValue other ) {
            clUpdateValue loUpdateValue = new clUpdateValue();
            loUpdateValue.meClassName = new String(other.meClassName);
            loUpdateValue.meIdName = new String(other.meIdName);
            loUpdateValue.meIdTag = new String(other.meIdTag);
            loUpdateValue.meIdContent = new String(other.meIdContent);
            loUpdateValue.meLevelName = new String(other.meLevelName);
            loUpdateValue.meLevelTag = new String(other.meLevelTag);
            loUpdateValue.meLevelContent = new String(other.meLevelContent);
            loUpdateValue.mePropertyName= other.mePropertyName;
            loUpdateValue.mePropertyTag= other.mePropertyTag;
            loUpdateValue.mePropertyContent = other.mePropertyContent;
            return loUpdateValue;
    }    
}
