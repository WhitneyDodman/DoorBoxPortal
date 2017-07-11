package doorbox.portal.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-05-17T12:15:44")
@StaticMetamodel(Log.class)
public class Log_ { 

    public static volatile SingularAttribute<Log, Integer> accountId;
    public static volatile SingularAttribute<Log, Integer> logId;
    public static volatile SingularAttribute<Log, String> event;
    public static volatile SingularAttribute<Log, String> params;
    public static volatile SingularAttribute<Log, Date> timestamp;

}