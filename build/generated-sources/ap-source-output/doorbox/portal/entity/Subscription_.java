package doorbox.portal.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-05-17T12:15:44")
@StaticMetamodel(Subscription.class)
public class Subscription_ { 

    public static volatile SingularAttribute<Subscription, Integer> accountId;
    public static volatile SingularAttribute<Subscription, Date> endDate;
    public static volatile SingularAttribute<Subscription, Integer> subscriptionType;
    public static volatile SingularAttribute<Subscription, Integer> subscriptionState;
    public static volatile SingularAttribute<Subscription, Integer> paymentPeriod;
    public static volatile SingularAttribute<Subscription, Integer> subscriptionId;
    public static volatile SingularAttribute<Subscription, Date> startDate;
    public static volatile SingularAttribute<Subscription, Integer> paymentType;

}