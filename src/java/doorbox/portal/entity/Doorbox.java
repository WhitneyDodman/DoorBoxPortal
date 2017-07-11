/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "doorbox")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doorbox.findAll", query = "SELECT d FROM Doorbox d"),
    @NamedQuery(name = "Doorbox.count", query = "SELECT count(d) FROM Doorbox d"),
    @NamedQuery(name = "Doorbox.findByDoorboxId", query = "SELECT d FROM Doorbox d WHERE d.doorboxId = :doorboxId"),
    @NamedQuery(name = "Doorbox.findByAccountId", query = "SELECT d FROM Doorbox d WHERE d.accountId = :accountId"),
    @NamedQuery(name = "Doorbox.findSimpleListByAccountId", query = "SELECT d.doorboxId,d.uniqueId FROM Doorbox d WHERE d.accountId = :accountId"),
    @NamedQuery(name = "Doorbox.findByUniqueId", query = "SELECT d FROM Doorbox d WHERE d.uniqueId = :uniqueId"),
    @NamedQuery(name = "Doorbox.findByLatitude", query = "SELECT d FROM Doorbox d WHERE d.latitude = :latitude"),
    @NamedQuery(name = "Doorbox.findByLongitude", query = "SELECT d FROM Doorbox d WHERE d.longitude = :longitude"),
    @NamedQuery(name = "Doorbox.findByDoorboxModel", query = "SELECT d FROM Doorbox d WHERE d.modelId = :modelId"),
    @NamedQuery(name = "Doorbox.findByMasterCode", query = "SELECT d FROM Doorbox d WHERE d.masterCode = :masterCode"),
    @NamedQuery(name = "Doorbox.findBySubMasterCode", query = "SELECT d FROM Doorbox d WHERE d.subMasterCode = :subMasterCode"),
    @NamedQuery(name = "Doorbox.findByTechnicianCode", query = "SELECT d FROM Doorbox d WHERE d.technicianCode = :technicianCode"),
    @NamedQuery(name = "Doorbox.findByLocationDescription", query = "SELECT d FROM Doorbox d WHERE d.locationDescription = :locationDescription"),
    @NamedQuery(name = "Doorbox.findByAddress1", query = "SELECT d FROM Doorbox d WHERE d.address1 = :address1"),
    @NamedQuery(name = "Doorbox.findByAddress2", query = "SELECT d FROM Doorbox d WHERE d.address2 = :address2"),
    @NamedQuery(name = "Doorbox.findByCity", query = "SELECT d FROM Doorbox d WHERE d.city = :city"),
    @NamedQuery(name = "Doorbox.findByStateProv", query = "SELECT d FROM Doorbox d WHERE d.stateProv = :stateProv"),
    @NamedQuery(name = "Doorbox.findByCountry", query = "SELECT d FROM Doorbox d WHERE d.country = :country"),
    @NamedQuery(name = "Doorbox.findByPostalCode", query = "SELECT d FROM Doorbox d WHERE d.postalCode = :postalCode"),
    @NamedQuery(name = "Doorbox.findByPhotoUrl", query = "SELECT d FROM Doorbox d WHERE d.photoUrl = :photoUrl"),
    @NamedQuery(name = "Doorbox.deleteDoorboxByDoorboxId", query = "DELETE FROM Doorbox d WHERE d.doorboxId = :doorboxId")
})
public class Doorbox implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator="doorbox_id_gen")
    @TableGenerator(name="doorbox_id_gen", pkColumnValue="doorbox_id_seq", initialValue=1000, table="sequence", pkColumnName="seq_name", valueColumnName="seq_value")
    @Column(name = "doorbox_id", nullable = false)
    private Integer doorboxId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "account_id", nullable = false)
    private Integer accountId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "UNIQUE_ID", nullable = false)
    private String uniqueId;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "MODEL_ID", nullable = false)
    private Integer modelId;
    
    @Size(max = 20)
    @Column(name = "LATITUDE")
    private String latitude;
    
    @Size(max = 20)
    @Column(name = "LONGITUDE")
    private String longitude;
    
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "init_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initDate;

    @Basic(optional = false)
    @NotNull
    @Column(name = "seed")
    private String seed;

    @Size(max = 20)
    @Column(name = "MASTER_CODE")
    private String masterCode;
    
    @Size(max = 20)
    @Column(name = "SUB_MASTER_CODE")
    private String subMasterCode;
    
    @Size(max = 20)
    @Column(name = "TECHNICIAN_CODE")
    private String technicianCode;
    
    @Size(max = 256)
    @Column(name = "LOCATION_DESCRIPTION")
    private String locationDescription;
    
    @Size(max = 64)
    @Column(name = "ADDRESS_1")
    private String address1;
    
    @Size(max = 64)
    @Column(name = "ADDRESS_2")
    private String address2;
    
    @Size(max = 32)
    @Column(name = "CITY")
    private String city;
    
    @Size(max = 2)
    @Column(name = "STATE_PROV")
    private String stateProv;
    
    @Size(max = 2)
    @Column(name = "COUNTRY")
    private String country;
    
    @Size(max = 20)
    @Column(name = "POSTAL_CODE")
    private String postalCode;
    
    @Size(max = 128)
    @Column(name = "PHOTO_URL")
    private String photoUrl;
    
    public Integer getDoorboxId() {
        return this.doorboxId;
    }

    public void setDoorboxId(Integer doorboxId) {
        this.doorboxId = doorboxId;
    }
    
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public void setMasterCode(String masterCode) {
        this.masterCode = masterCode;
    }

    public String getSubMasterCode() {
        return subMasterCode;
    }

    public void setSubMasterCode(String subMasterCode) {
        this.subMasterCode = subMasterCode;
    }

    public String getTechnicianCode() {
        return technicianCode;
    }

    public void setTechnicianCode(String technicianCode) {
        this.technicianCode = technicianCode;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProv() {
        return stateProv;
    }

    public void setStateProv(String stateProv) {
        this.stateProv = stateProv;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (doorboxId != null ? doorboxId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doorbox)) {
            return false;
        }
        Doorbox other = (Doorbox) object;
        if ((this.doorboxId == null && other.doorboxId != null) || (this.doorboxId != null && !this.doorboxId.equals(other.doorboxId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "doorbox.portal.entity.Doorbox[ doorboxId=" + doorboxId + " ]";
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

    /**
     * @return the doorboxModel
     */
    public Integer getModelId() {
        return modelId;
    }

    /**
     * @param doorboxModel the doorboxModel to set
     */
    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateUpdated
     */
    public Date getDateUpdated() {
        return dateUpdated;
    }

    /**
     * @param dateUpdated the dateUpdated to set
     */
    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    /**
     * @return the initDate
     */
    public Date getInitDate() {
        return initDate;
    }

    /**
     * @param initDate the initDate to set
     */
    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    /**
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }
    
}
