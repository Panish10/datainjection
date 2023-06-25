package com.ebaotech.datainjection.dev.holiday.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Holiday")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "hdate", unique = true)
    private Date date;

    @Column(name = "hday")
    private String day;

    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", date=" + date +
                ", day='" + day + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
