package com.webalexx.prj_mechanik.content.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents section as an object
 */
public class Section {

    @SerializedName(value = "ID")
    private long id;
    @SerializedName(value = "NAME")
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
