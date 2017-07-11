/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author torinw
 */
@Entity(name = "DoorboxModel")
@Table(name = "doorbox_model")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DoorboxModel.findAll", query = "SELECT d FROM DoorboxModel d"),
    @NamedQuery(name = "DoorboxModel.findByModelId", query = "SELECT d FROM DoorboxModel d WHERE d.modelId = :modelId"),
    @NamedQuery(name = "DoorboxModel.findByModelNumber", query = "SELECT d FROM DoorboxModel d WHERE d.modelNumber = :modelNumber")})
public class DoorboxModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "model_id", nullable = false)
    private Integer modelId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "model_number", nullable = false)
    private String modelNumber;
    
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "dimensions")
    private String dimensions;

    @Lob
    @Column(name = "custom")
    private byte[] custom;
    @Lob
    @Column(name = "networked")
    private byte[] networked;
    @Lob
    @Column(name = "insulated")
    private byte[] insulated;
    @Size(max = 128)
    @Column(name = "features")
    private String features;
    @Size(max = 128)
    @Column(name = "photo_url")
    private String photoUrl;

    public DoorboxModel() {
    }

    public DoorboxModel(Integer modelId) {
        this.modelId = modelId;
    }

    public DoorboxModel(Integer modelId, String modelNumber) {
        this.modelId = modelId;
        this.modelNumber = modelNumber;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public byte[] getCustom() {
        return custom;
    }

    public void setCustom(byte[] custom) {
        this.custom = custom;
    }

    public byte[] getNetworked() {
        return networked;
    }

    public void setNetworked(byte[] networked) {
        this.networked = networked;
    }

    public byte[] getInsulated() {
        return insulated;
    }

    public void setInsulated(byte[] insulated) {
        this.insulated = insulated;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
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
        hash += (modelId != null ? modelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DoorboxModel)) {
            return false;
        }
        DoorboxModel other = (DoorboxModel) object;
        if ((this.modelId == null && other.modelId != null) || (this.modelId != null && !this.modelId.equals(other.modelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "doorbox.portal.entity.DoorboxModel[ modelId=" + modelId + " ]";
        //return "Model " + modelNumber;
    }

    /**
     * @return the dimensions
     */
    public String getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    
}
