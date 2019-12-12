/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wva.vtiger;
import java.util.List;
 
/**
 *
 * @author Wouter Vandorpe
 */
public class clUpdateCRM implements Cloneable{
    String meClassName;
    String meID;
    List<String> meProp = null;
    List<String> meValue = null;
    public clUpdateCRM(){}
    public clUpdateCRM clone() throws CloneNotSupportedException {
            clUpdateCRM loUpdateCRM = (clUpdateCRM) super.clone();
            loUpdateCRM.meClassName = new String(this.meClassName);
            loUpdateCRM.meID = new String(this.meID);
            loUpdateCRM.meProp = this.meProp;
            loUpdateCRM.meValue = this.meValue;     
            return loUpdateCRM;
    } 
    public clUpdateCRM copy(clUpdateCRM other ) {
            clUpdateCRM loUpdateCRM = new clUpdateCRM();
            loUpdateCRM.meClassName = new String(this.meClassName);
            loUpdateCRM.meID = new String(this.meID);
            loUpdateCRM.meProp = this.meProp;
            loUpdateCRM.meValue = this.meValue; 
            return loUpdateCRM;
    }        
}
