package com.example.emyeraky.nearlymap;


class Favorite {
    String name, id;
    double lat, lng;

    public Favorite() {
    }

    public Favorite(String name, String id, double lat, double lng) {
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
