package it.polito.ai.project.service.model;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.wololo.geojson.Point;

public class Position {
    @Id
    private String id;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    public Point point;

    public Position(){}

    public Position(double lat, double lng) {
        double array[] = {lng, lat};
        this.point = new Point(array);
    }
    public void trimPrecsion(){
        this.point.getCoordinates()[0]=round(this.point.getCoordinates()[0]);
        this.point.getCoordinates()[1]=round(this.point.getCoordinates()[1]);
    }
    private static double round(double d) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double retriveLat() {
        return point.getCoordinates()[1];
    }

    public double retrieveLng() {
        return  point.getCoordinates()[0];
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.retriveLat(), this.retriveLat()) == 0 &&
                Double.compare(position.retrieveLng(), this.retrieveLng()) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(retriveLat(), retrieveLng());
    }
}
