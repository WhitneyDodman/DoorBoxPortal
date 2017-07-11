package doorbox.portal.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-05-17T12:15:44")
@StaticMetamodel(PPSubscription.class)
public class PPSubscription_ { 

    public static volatile SingularAttribute<PPSubscription, Integer> accountId;
    public static volatile SingularAttribute<PPSubscription, String> txItemNumber;
    public static volatile SingularAttribute<PPSubscription, String> txCurrency;
    public static volatile SingularAttribute<PPSubscription, String> txCM;
    public static volatile SingularAttribute<PPSubscription, String> txId;
    public static volatile SingularAttribute<PPSubscription, String> txState;
    public static volatile SingularAttribute<PPSubscription, Double> txAmt;
    public static volatile SingularAttribute<PPSubscription, Date> txDate;

}