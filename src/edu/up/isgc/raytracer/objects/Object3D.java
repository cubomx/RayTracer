package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.tools.Material;

public abstract class Object3D {  
    private Vector3D position;

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    private Material mat;

    public Object3D(Vector3D position, Material mat){
        setPosition(position);
        setMat(mat);
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    public abstract Intersection getIntersection(Ray ray);
    
}
