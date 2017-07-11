package doorbox.portal.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-05-17T12:15:44")
@StaticMetamodel(EphemeralCode.class)
public class EphemeralCode_ { 

    public static volatile SingularAttribute<EphemeralCode, Integer> accountId;
    public static volatile SingularAttribute<EphemeralCode, String> recipientPhone;
    public static volatile SingularAttribute<EphemeralCode, Integer> ephemeralCodeId;
    public static volatile SingularAttribute<EphemeralCode, Integer> ephemeralCodeType;
    public static volatile SingularAttribute<EphemeralCode, Integer> durationId;
    public static volatile SingularAttribute<EphemeralCode, String> recipientName;
    public static volatile SingularAttribute<EphemeralCode, Integer> doorboxId;
    public static volatile SingularAttribute<EphemeralCode, String> message;
    public static volatile SingularAttribute<EphemeralCode, String> recipientEmail;
    public static volatile SingularAttribute<EphemeralCode, String> ephemeralCode;
    public static volatile SingularAttribute<EphemeralCode, Date> startDate;

}