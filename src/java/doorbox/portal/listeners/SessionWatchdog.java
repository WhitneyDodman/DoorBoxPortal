/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.listeners;

import doorbox.portal.beans.ApplicationBean;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 *
 * @author torinw
 */
public class SessionWatchdog implements HttpSessionListener {

  private static int totalActiveSessions;

  public static int getTotalActiveSession(){
	return totalActiveSessions;
  }

  @Override
  public void sessionCreated(HttpSessionEvent arg0) {
	totalActiveSessions++;        
	System.out.println("sessionCreated. Users now online " + totalActiveSessions);
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent arg0) {
	totalActiveSessions--;
	System.out.println("sessionDestroyed. Users still online " + totalActiveSessions);
  }
}
