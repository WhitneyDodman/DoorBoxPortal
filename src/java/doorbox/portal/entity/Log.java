/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author torinw
 */
@Entity
@Table(name = "log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findByLogId", query = "SELECT l FROM Log l WHERE l.logId = :logId"),
    @NamedQuery(name = "Log.findByTimestamp", query = "SELECT l FROM Log l WHERE l.timestamp = :timestamp"),
    @NamedQuery(name = "Log.findByAccountId", query = "SELECT l FROM Log l WHERE l.accountId = :accountId"),
    @NamedQuery(name = "Log.findByAccountId_sortByTimestampDesc", query = "SELECT l FROM Log l WHERE l.accountId = :accountId ORDER BY l.timestamp DESC "),
    @NamedQuery(name = "Log.findByEvent", query = "SELECT l FROM Log l WHERE l.event = :event"),
    @NamedQuery(name = "Log.findByParams", query = "SELECT l FROM Log l WHERE l.params = :params")})
public class Log implements Serializable {

    public static enum Event {
        CREATE_ACCOUNT,
        CONFIRM_REGISTRATION,
        READ_ACCOUNT,
        UPDATE_ACCOUNT,
        DELETE_ACCOUNT,
        RESET_PASSWORD,
        UPDATE_PASSWORD,
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        SEND_EMAIL,
        SEND_SMS,
        CREATE_DOORBOX,
        READ_DOORBOX,
        UPDATE_DOORBOX,
        DELETE_DOORBOX,
        UPDATE_MASTER_CODE,
        UPDATE_SUBMASTER_CODE,
        UPDATE_TECHNICIAN_CODE,
        CREATE_EPHEMERAL_CODE,
        UPDATE_EPHEMERAL_CODE,
        DELETE_EPHEMERAL_CODE,
        SEND_EPHEMERAL_CODE
    }
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(generator="log_id_gen")
    @TableGenerator(name="log_id_gen", pkColumnValue="log_id_seq", initialValue=1000, table="sequence", pkColumnName="seq_name", valueColumnName="seq_value")
    @Column(name = "LOG_ID")    
    private Integer logId;
    
    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    @Column(name = "ACCOUNT_ID")
    private Integer accountId;
    
    @Size(max = 64)
    @Column(name = "EVENT")
    private String event;
    
    @Size(max = 256)
    @Column(name = "PARAMS")
    private String params;

    public Log() {
    }

    public Log(Integer doorboxLogId) {
        this.logId = doorboxLogId;
    }

    public Log(Integer accountId, Date timestamp, Event event, String... params) {
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.event = event.toString();
        setParams(params);
    }
    
    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer doorboxLogId) {
        this.logId = doorboxLogId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Event getEvent() {
        return Event.valueOf(event);
    }

    public void setEvent(Event event) {
        this.event = event.toString();
    }

    public String getParams() {
        return params;
    }

    public final void setParams(String params) {
        this.params = params;
    }
    
    public final void setParams(String... params) {
        StringBuilder p = new StringBuilder();
        for(String param: params) {
            if (p.length() > 0) {
                p.append(",");
            }
            p.append(param);
        }
        this.params = p.toString();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logId != null ? logId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.logId == null && other.logId != null) || (this.logId != null && !this.logId.equals(other.logId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "doorbox.portal.entity.Log[ doorboxLogId=" + logId + " ]";
    }
    
}
