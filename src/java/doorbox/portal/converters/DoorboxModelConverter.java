/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.converters;

import doorbox.portal.beans.MyDoorboxBean;
 import doorbox.portal.entity.DoorboxModel;
import java.util.List;
import javax.annotation.Resource;

 import javax.faces.application.FacesMessage;
 import javax.faces.component.UIComponent;
 import javax.faces.context.FacesContext;
 import javax.faces.convert.Converter;
 import javax.faces.convert.ConverterException;
 import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author torinw
 */
@FacesConverter(forClass = doorbox.portal.entity.DoorboxModel.class,value="doorboxModelConverter")
public class DoorboxModelConverter implements Converter {
    @PersistenceContext(name = "persistence/LogicalName", unitName = "DoorBoxPU")
    public EntityManager em;

    @Resource
    public javax.transaction.UserTransaction utx;
    
    public static List<DoorboxModel> modelList;
    
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        System.out.println("converter submitted value " + submittedValue);
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                if (modelList == null) {
                    MyDoorboxBean bean = facesContext.getCurrentInstance().getApplication().evaluateExpressionGet(facesContext, "#{myDoorboxBean}", MyDoorboxBean.class);                    
                    Query query = bean.em.createNamedQuery("DoorboxModel.findAll");
                    modelList = query.getResultList();
                }
                
                for (DoorboxModel m : modelList) {
                    if (m.getModelNumber().equals(submittedValue)) {
                        return m;
                    }
                }

            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            System.out.println("converter object value " + value);

            if (modelList == null) {
                MyDoorboxBean bean = facesContext.getCurrentInstance().getApplication().evaluateExpressionGet(facesContext, "#{myDoorboxBean}", MyDoorboxBean.class);                    
                Query query = bean.em.createNamedQuery("DoorboxModel.findAll");
                modelList = query.getResultList();
            }
            
            for (DoorboxModel m : modelList) {
                if (m.getModelId().toString().equals(value.toString())) {
                    return m.getModelId().toString();
                }
            }
            return "[model not found]";
        }
    }
}