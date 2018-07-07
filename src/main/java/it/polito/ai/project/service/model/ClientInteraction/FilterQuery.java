package it.polito.ai.project.service.model.ClientInteraction;

import org.wololo.geojson.Polygon;

import java.util.List;

/**
 * This class is related to the FilterQuery.
 */
public class FilterQuery {
    private Polygon geoFilter;
    private List<String> usersFilter;

    public FilterQuery() {
    }
    /**
     * This method allows to set parameters for the filter.
     * @param geoFilter
     * @param usersFilter
     */
    public FilterQuery(Polygon geoFilter, List<String> usersFilter) {
        this.geoFilter = geoFilter;
        this.usersFilter = usersFilter;
    }
    /**
     * This method allows to get the Polygon of the filter.
     */
    public Polygon getGeoFilter() {
        return geoFilter;
    }
    /**
     * This method allows to set the Polygon of the filter.
     * @param geoFilter
     */
    public void setGeoFilter(Polygon geoFilter) {
        this.geoFilter = geoFilter;
    }
    /**
     * This method allows to get the Users list of the filter.
     */
    public List<String> getUsersFilter() {
        return usersFilter;
    }
    /**
     * This method allows to set the Users list of the filter.
     * @param usersFilter
     */
    public void setUsersFilter(List<String> usersFilter) {
        this.usersFilter = usersFilter;
    }
}
