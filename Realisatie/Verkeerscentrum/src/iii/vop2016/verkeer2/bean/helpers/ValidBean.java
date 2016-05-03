/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.bean.helpers;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

@ManagedBean
public class ValidBean {

  private UIComponent myComponent;

  public UIComponent getMyComponent() {
    return myComponent;
  }

  public void setMyComponent(UIComponent myComponent) {
    this.myComponent = myComponent;
  }

  public String getErrorStyle() {
    FacesContext context = FacesContext
        .getCurrentInstance();
    String clientId = myComponent.getClientId(context);
    Iterator<FacesMessage> messages = context
        .getMessages(clientId);
    while (messages.hasNext()) {
      if (messages.next().getSeverity().compareTo(
          FacesMessage.SEVERITY_ERROR) >= 0) {
        return "background-color: red";
      }
    }
    return null;
  }
}