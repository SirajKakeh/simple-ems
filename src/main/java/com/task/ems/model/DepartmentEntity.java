package com.task.ems.model;

import javax.persistence.*;

@Entity
@Table(name = "department")
//@AttributeOverride(name="id", column=@Column(name="department_id"))
public class DepartmentEntity extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String departmentName;

    @Column(length = 10, unique = true, nullable = false)
    private String shortName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
