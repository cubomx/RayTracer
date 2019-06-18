package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.Object3D;


public class Intersection {
    private double distance;
    private Vector3D normal;
    private Vector3D position;
    private Object3D object;
    private Object3D group;
    
    public Intersection(Vector3D position, double distance, Vector3D normal, Object3D object, Object3D group){
        setPosition(position);
        setDistance(distance);
        setNormal(normal);
        setObject(object);
        setGroup(group);
    }
    

    public Object3D getGroup() {
		return group;
	}


	public void setGroup(Object3D group) {
		this.group = group;
	}


	public Object3D getObject() {
        return object;
    }

    private void setObject(Object3D object) {
        this.object = object;
    }
    
    public Vector3D getPosition() {
        return position;
    }

    private void setPosition(Vector3D position) {
        this.position = position;
    }

    public double getDistance() {
        return distance;
    }

    private void setDistance(double distance) {
        this.distance = distance;
    }

    public Vector3D getNormal() {
        return normal;
    }

    private void setNormal(Vector3D normal) {
        this.normal = normal;
    }
}
