package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.tools.Material;


public class Triangle extends Object3D {

    public static final double EPSILON = 0.000000001;

    private Vector3D[] vertices;
    private Vector3D[] normals;

    public Triangle(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3, Material mat) {
        super(null, mat);
        setVertices(vertex1, vertex2, vertex3);
        setNormal(null);
    }


    public Triangle(Vector3D[] vertices, Vector3D[] normal, Material mat) {
        super(null, mat);
        setVertices(vertices[0], vertices[1], vertices[2]);
        setNormal(normal);
    }

    public Vector3D[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3) {
        Vector3D[] vertices = new Vector3D[]{vertex1, vertex2, vertex3};
        this.vertices = vertices;
    }
    
    /**
     * Make a average of the normal of each vertex 
     * @param point
     * @return Vector3D
     */
    
    public Vector3D getNormal(Vector3D point) {
        Vector3D normal = Vector3D.ZERO();
        
        Vector3D[] normals = this.normals;
        if (normals == null) {
            Vector3D[] vertices = getVertices();
            Vector3D v = Vector3D.substract(vertices[1], vertices[0]);
            Vector3D w = Vector3D.substract(vertices[2], vertices[0]);

            normal = Vector3D.scalarMultiplication(Vector3D.normalize(Vector3D.crossProduct(v, w)), -1.0);
        } else {
            for(int i = 0; i < normals.length; i++){
                normal.setX(normal.getX() + normals[i].getX());
                normal.setY(normal.getY() + normals[i].getY());
                normal.setZ(normal.getZ() + normals[i].getZ());
            }
            normal.setX(normal.getX() / normals.length);
            normal.setY(normal.getY() / normals.length);
            normal.setZ(normal.getZ() / normals.length);
        }
        
        return normal;
    }
    
    public Vector3D getNormal() {
        Vector3D normal = Vector3D.ZERO();
        
        Vector3D[] normals = this.normals;
        if (normals == null) {
            Vector3D[] vertices = getVertices();
            Vector3D v = Vector3D.substract(vertices[1], vertices[0]);
            Vector3D w = Vector3D.substract(vertices[2], vertices[0]);

            normal = Vector3D.scalarMultiplication(Vector3D.normalize(Vector3D.crossProduct(v, w)), -1.0);
        } else {
        	for(int i = 0; i < normals.length; i++){
                normal.setX(normal.getX() + normals[i].getX());
                normal.setY(normal.getY() + normals[i].getY());
                normal.setZ(normal.getZ() + normals[i].getZ());
            }
            normal.setX(normal.getX() / normals.length);
            normal.setY(normal.getY() / normals.length);
            normal.setZ(normal.getZ() / normals.length);
        }
        
        return normal;
    }

    
    /** Using the Moller-Trumbore algorithm obtain if the triangle is in the
     * way of the ray
     * @param Ray ray
     * return Intersection
     */
    public Intersection getIntersection(Ray ray) {
        //Moller-Trumbore algorithm
        Vector3D[] vertices = getVertices();
        Vector3D v2v0 = Vector3D.substract(vertices[2], vertices[0]);
        Vector3D v1v0 = Vector3D.substract(vertices[1], vertices[0]);
        Vector3D vectorP = Vector3D.crossProduct(ray.getDirection(), v1v0);
        double determinant = Vector3D.dotProduct(v2v0, vectorP);
        //if (determinant < EPSILON) return -1;

        double invertedDeterminant = 1.0 / determinant;

        Vector3D vectorT = Vector3D.substract(ray.getOrigin(), vertices[0]);
        double u = Vector3D.dotProduct(vectorT, vectorP) * invertedDeterminant;
        if (u < 0 || u > 1) {
            return null;
        }

        Vector3D vectorQ = Vector3D.crossProduct(vectorT, v2v0);
        double v = Vector3D.dotProduct(ray.getDirection(), vectorQ) * invertedDeterminant;
        if (v < 0 || (u + v) > (1.0 + EPSILON)) {
            return null;
        }

        double t = Vector3D.dotProduct(vectorQ, v1v0) * invertedDeterminant;

        return new Intersection(null, t, null, this, null);
    }

    public void setNormal(Vector3D[] normals) {
        this.normals = normals;
    }
    
    public Vector3D[] getNormals() {
    	return this.normals;
    }

}
