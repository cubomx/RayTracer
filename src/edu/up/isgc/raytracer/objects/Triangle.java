package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;

public class Triangle extends Object3D {
    private Vector3D leftVertex;
    private Vector3D rightVertex;
    private static double EPSILON =  0.000001;
    public Vector3D getLeftVertex() {
        return leftVertex;
    }

    public void setLeftVertex(Vector3D leftVertex) {
        this.leftVertex = leftVertex;
    }

    public Vector3D getRightVertex() {
        return rightVertex;
    }

    public void setRightVertex(Vector3D rightVertex) {
        this.rightVertex = rightVertex;
    }
    public Triangle(Vector3D top, Vector3D left, Vector3D right, Color color) {
        super(top, color);
        setLeftVertex(left);
        setRightVertex(right);
    }

    @Override

    public Intersection getIntersection(Ray ray, Camera cam) {
        double t;
        Vector3D normal = Vector3D.ZERO();
        Vector3D v2v0 = Vector3D.substract(getRightVertex(), getPosition());
        Vector3D v1v0 = Vector3D.substract(getLeftVertex(), getPosition());
        Vector3D p = Vector3D.crossProduct(ray.getDirection(), v1v0);
        double determinant = Vector3D.dotProduct(v2v0, p);
        double invDeterminant = 1.0 / determinant;
        Vector3D T = Vector3D.substract(ray.getOrigin(), getPosition());
        double u = invDeterminant * Vector3D.dotProduct(T, p);
        Vector3D Q = Vector3D.crossProduct(T, v2v0);
        double v = invDeterminant*Vector3D.dotProduct(ray.getDirection(), Q);
        t = invDeterminant * Vector3D.dotProduct(Q, v1v0);
        normal = Vector3D.normalize(Vector3D.crossProduct(v1v0, v2v0));

        if (u < 0 || u > 1)
            return null;
        if (v < 0 || (u+v) > (1.0 + EPSILON))
            return null;
        return new Intersection(new Vector3D(0,0, t), t, normal,this);
    }
}
