/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author torinw
 */
@Embeddable
public class DoorboxPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "DOORBOX_ID")
    private int doorboxId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACCOUNT_ID")
    private int accountId;
            
    public DoorboxPK() {
    }

    public DoorboxPK(int doorboxId, int accountId) {
        this.doorboxId = doorboxId;
        this.accountId = accountId;
    }

    public int getDoorboxId() {
        return doorboxId;
    }

    public void setDoorboxId(int doorboxId) {
        this.doorboxId = doorboxId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) doorboxId;
        hash += (int) accountId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoorboxPK)) {
            return false;
        }
        DoorboxPK other = (DoorboxPK) object;
        if (this.doorboxId != other.doorboxId) {
            return false;
        }
        if (this.accountId != other.accountId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "doorbox.portal.entity.DoorboxPK[ doorboxId=" + doorboxId + ", accountId=" + accountId + " ]";
    }
    
}
