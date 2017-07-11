/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.validators;

import doorbox.portal.beans.BaseBean;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author torinw
 */
@RequestScoped
@FacesValidator
@ManagedBean
public class EmailValidator extends BaseBean implements Validator {
    @PersistenceContext(name = "persistence/LogicalName", unitName = "DoorBoxPU")
    public EntityManager em;

    @Resource
    public javax.transaction.UserTransaction utx;
    
    @Override
    public void validate(final FacesContext context, final UIComponent comp, final Object value) throws ValidatorException {
      boolean isValid = false;
      if (value == null) return;
      
      try {
        String email = value.toString();
        // if email !matches regex, throw invalid format message
        System.out.println("Validing whether email '" + email + "' is unique");
        System.out.println("em is " + (em == null ? "null" : em.toString()));
        Query q = em.createNamedQuery("Account.findByEmail");
        q.setParameter("email", email);
        q.getSingleResult();
      } catch (NoResultException e) {
          isValid = true; // no result means validation proved unique
      }

      if (!isValid) {
          FacesMessage msg = new FacesMessage("Email already exists");
          msg.setSeverity(FacesMessage.SEVERITY_ERROR);
          context.addMessage(comp.getClientId(context), msg);
          throw new ValidatorException(msg);
      }
    }
}