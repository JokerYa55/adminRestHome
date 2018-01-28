/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.adminrest.beans;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author vasil
 */
@NamedQueries({
    @NamedQuery(name = "getAttributeByName", query = "select u from UserAttribute u where u.name = :name"),
    @NamedQuery(name = "getAttributeById", query = "select u from UserAttribute u where u.id = :id"),    
    @NamedQuery(name = "getAllAttributesByUsers", query = "select u from UserAttribute u where u.userId=:userId"),
    @NamedQuery(name = "findAttributeByUserName", query = "select u from UserAttribute u where u.userId = :userId and u.name = :name")})

@Entity
@Table(name = "t_user_attribute", indexes = {
    @Index(name = "t_user_attribute_name_idx", columnList = "name")
    ,
    @Index(name = "t_user_attribut_user_id_idx", columnList = "user_id")})
public class UserAttribute implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_user_attribute_id_seq")
    @SequenceGenerator(name = "t_user_attribute_id_seq", sequenceName = "t_user_attribute_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "name", unique = false, nullable = false)
    private String name;
    @Column(name = "value", unique = false, nullable = false)
    private String value;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserEntity userId;
    @Column(name = "visible_flag", unique = false, nullable = false, columnDefinition = "boolean DEFAULT true")
    private boolean visible_flag;

    public UserAttribute() {
    }

    public UserAttribute(String name, String value, UserEntity userId, boolean visible_flag) {
        this.name = name;
        this.value = value;
        this.userId = userId;
        this.visible_flag = visible_flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public boolean isVisible_flag() {
        return visible_flag;
    }

    public void setVisible_flag(boolean visible_flag) {
        this.visible_flag = visible_flag;
    }

    @Override
    public String toString() {
        return "UserAttribute{" + "id=" + id + ", name=" + name + ", value=" + value + '}';
    }

}
