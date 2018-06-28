package it.polito.ai.project.service.model.ClientInteraction;

import org.wololo.geojson.Polygon;

import java.util.List;

public class FilterQuery {
    public Polygon geoFilter;
    public List<String> usersFilter;

    public FilterQuery() {
    }

    public FilterQuery(Polygon geoFilter, List<String> usersFilter) {
        this.geoFilter = geoFilter;
        this.usersFilter = usersFilter;
    }

    public Polygon getGeoFilter() {
        return geoFilter;
    }

    public void setGeoFilter(Polygon geoFilter) {
        this.geoFilter = geoFilter;
    }

    public List<String> getUsersFilter() {
        return usersFilter;
    }

    public void setUsersFilter(List<String> usersFilter) {
        this.usersFilter = usersFilter;
    }
}
