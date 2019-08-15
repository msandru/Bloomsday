package com.example.bloomsday.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    //Implement ClusterItem to represent a marker on the map. The cluster item returns the position of the marker as a LatLng object, and an optional title or snippet.

    private LatLng position; // required field
    private String title; // required field
    private String snippet; // required field
    private int iconPicture;

    public ClusterMarker(LatLng position, String title, int iconPicture, String snippet) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
    }


    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }


    public void setPosition(LatLng position) {
        this.position = position;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }
}
