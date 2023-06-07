package de.seanbri.aquaplane.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ArgumentPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String arg1;
    private String arg2;
    private String claim;

    public ArgumentPair(int id, String arg1, String arg2, String claim) {
        this.id = id;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.claim = claim;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

}
