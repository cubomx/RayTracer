package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.tools.Barycentric;
import edu.up.isgc.raytracer.tools.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Polygon extends Object3D {

    public static final int AMOUNT_VERTICES = 3;

    private List<Triangle> triangles;

    public Polygon(Vector3D position, Triangle[] triangles, Material mat) {
        super(position, mat);
        setTriangles(triangles);
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public void setTriangles(Triangle[] triangles) {
        Vector3D position = getPosition();
        Set<Vector3D> uniqueVertices = new HashSet<Vector3D>();

        for (Triangle triangle : triangles) {
            uniqueVertices.addAll(Arrays.asList(triangle.getVertices()));
        }

        for (Vector3D vertex : uniqueVertices) {
            vertex.setX(vertex.getX() + position.getX());
            vertex.setY(vertex.getY() + position.getY());
            vertex.setZ(vertex.getZ() + position.getZ());
        }

        this.triangles = Arrays.asList(triangles);
    }

    public Intersection getIntersection(Ray ray) {
        double distance = -1;
        Vector3D normal = Vector3D.ZERO();
        Vector3D position = Vector3D.ZERO();
        Object3D obj = null;
        for (Triangle triangle : getTriangles()) {
        	Intersection intersection = triangle.getIntersection(ray);

            if (intersection != null && intersection.getDistance() > 0 && (intersection.getDistance() < distance || distance < 0)) {
            	normal = Vector3D.ZERO();
            	distance = intersection.getDistance();
                position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
                double[] uVw = Barycentric.CalculateBarycentricCoordinates(position, triangle);
                obj = intersection.getObject();
                Vector3D[] normals = triangle.getNormals();
                for(int i = 0; i < uVw.length; i++) {
                	normal = Vector3D.add(normal, Vector3D.scalarMultiplication(normals[i], uVw[i]));
                }
            }
        }

        if (distance == -1) {
            return null;
        }

        return new Intersection(position, distance, normal, obj, this);
    }

}
