package edu.up.isgc.raytracer.objects;


import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Raytracer;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;
import java.util.ArrayList;

public class Polygon extends Object3D{
    private ArrayList<Object3D> triangles;

    public Polygon() {
        super(new Vector3D(0, 0, 0), Color.BLUE);
        this.triangles = new ArrayList<>();
    }

    public ArrayList<Object3D> getTriangles() {
        return triangles;
    }

    public void setTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }

    @Override
    public Intersection getIntersection(Ray ray, Camera cam) {
        return Raytracer.raycast(ray, getTriangles(), null, cam);
    }
}
