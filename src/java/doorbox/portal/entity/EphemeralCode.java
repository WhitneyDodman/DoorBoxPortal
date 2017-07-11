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
@Table(name = "ephemeral_code")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EphemeralCode.findAll", query = "SELECT e FROM EphemeralCode e"),
    @NamedQuery(name = "EphemeralCode.findByEphemeralCodeId", query = "SELECT e FROM EphemeralCode e WHERE e.ephemeralCodeId = :ephemeralCodeId"),
    @NamedQuery(name = "EphemeralCode.findByEphemeralCodeType", query = "SELECT e FROM EphemeralCode e WHERE e.ephemeralCodeType = :ephemeralCodeType"),
    @NamedQuery(name = "EphemeralCode.findByAccountId", query = "SELECT e FROM EphemeralCode e WHERE e.accountId = :accountId"),
    @NamedQuery(name = "EphemeralCode.findByDoorboxId", query = "SELECT e FROM EphemeralCode e WHERE e.doorboxId = :doorboxId"),
    @NamedQuery(name = "EphemeralCode.findByEphemeralCode", query = "SELECT e FROM EphemeralCode e WHERE e.ephemeralCode = :ephemeralCode"),
    @NamedQuery(name = "EphemeralCode.findByMessge", query = "SELECT e FROM EphemeralCode e WHERE e.message = :message"),
    @NamedQuery(name = "EphemeralCode.findByStartDate", query = "SELECT e FROM EphemeralCode e WHERE e.startDate = :startDate"),
    @NamedQuery(name = "EphemeralCode.findByDurationId", query = "SELECT e FROM EphemeralCode e WHERE e.durationId = :durationId"),
    @NamedQuery(name = "EphemeralCode.findByRecipientName", query = "SELECT e FROM EphemeralCode e WHERE e.recipientName = :recipientName"),
    @NamedQuery(name = "EphemeralCode.findByRecipientEmail", query = "SELECT e FROM EphemeralCode e WHERE e.recipientEmail = :recipientEmail"),
    @NamedQuery(name = "EphemeralCode.findByRecipientPhone", query = "SELECT e FROM EphemeralCode e WHERE e.recipientPhone = :recipientPhone")})
public class EphemeralCode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(generator="ephemeral_code_id_gen")
    @TableGenerator(name="ephemeral_code_id_gen", pkColumnValue="ephemeral_code_id_seq", initialValue=1000, table="sequence", pkColumnName="seq_name", valueColumnName="seq_value")    
    @Column(name = "EPHEMERAL_CODE_ID", nullable = false)
    private Integer ephemeralCodeId;
    
    @NotNull
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Integer accountId;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "EPHEMERAL_CODE_TYPE")
    private int ephemeralCodeType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DOORBOX_ID")
    private int doorboxId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "EPHEMERAL_CODE")
    private String ephemeralCode;
    
    @Size(max = 1024)
    @Column(name = "MESSAGE")
    private String message;
    
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "DURATION_ID")
    private int durationId;
    
    @Size(max = 32)
    @Column(name = "RECIPIENT_NAME")
    private String recipientName;
    
    @Size(max = 64)
    @Column(name = "RECIPIENT_EMAIL")
    private String recipientEmail;
    
    @Size(max = 32)
    @Column(name = "RECIPIENT_PHONE")
    private String recipientPhone;

    public EphemeralCode() {
    }

    public EphemeralCode(Integer ephemeralCodeId) {
        this.ephemeralCodeId = ephemeralCodeId;
    }

    public EphemeralCode(Integer ephemeralCodeId, int ephemeralCodeType, int doorboxId, String ephemeralCode) {
        this.ephemeralCodeId = ephemeralCodeId;
        this.ephemeralCodeType = ephemeralCodeType;
        this.doorboxId = doorboxId;
        this.ephemeralCode = ephemeralCode;
    }

    public Integer getEphemeralCodeId() {
        return ephemeralCodeId;
    }

    public void setEphemeralCodeId(Integer ephemeralCodeId) {
        this.ephemeralCodeId = ephemeralCodeId;
    }

    public int getEphemeralCodeType() {
        return ephemeralCodeType;
    }

    public void setEphemeralCodeType(int ephemeralCodeType) {
        this.ephemeralCodeType = ephemeralCodeType;
    }

    public int getDoorboxId() {
        return doorboxId;
    }

    public void setDoorboxId(int doorboxId) {
        this.doorboxId = doorboxId;
    }

    public String getEphemeralCode() {
        return ephemeralCode;
    }

    public void setEphemeralCode(String ephemeralCode) {
        this.ephemeralCode = ephemeralCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDurationId() {
        return durationId;
    }

    public void setDurationId(int durationId) {
        this.durationId = durationId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ephemeralCodeId != null ? ephemeralCodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EphemeralCode)) {
            return false;
        }
        EphemeralCode other = (EphemeralCode) object;
        if ((this.ephemeralCodeId == null && other.ephemeralCodeId != null) || (this.ephemeralCodeId != null && !this.ephemeralCodeId.equals(other.ephemeralCodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "doorbox.portal.entity.EphemeralCode[ ephemeralCodeId=" + ephemeralCodeId + " ]";
    }

    /**
     * @return the accountId
     */
    public Integer getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    
}
