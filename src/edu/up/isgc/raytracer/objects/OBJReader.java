package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;
import java.util.ArrayList;

public abstract class OBJReader {
    public static Polygon createShape(ArrayList <String> raw){
        Polygon poly = new Polygon();
        ArrayList <Vector3D> vertices = new ArrayList<>();
        for (int index = 0; index < raw.size(); index++){


            String [] line = raw.get(index).split(" ");
            if (line[0].equals("v")){
                ArrayList <Double> point = new ArrayList<>();
                String val = raw.get(index);
                String info = "";
                Boolean still = true;
                for(int i = 0; i < val.length(); i++){
                    char c = val.charAt(i);
                    if(c != ' ' && c != 'v'){
                        info += val.charAt(i);
                        if (still)
                            still = false;
                    }
                    else if (!still){
                        point.add(Double.valueOf(info));
                        info = "";
                        still = true;
                    }
                }
                vertices.add(new Vector3D(point.get(0), point.get(1), Double.valueOf(info)));
            }
        }
        for (int index = 0; index < raw.size(); index++){
            String [] line = raw.get(index).split(" ");
            ArrayList <Integer> vertexUnion = new ArrayList<>();
            if (line[0].equals("f")){
                for (int i = 0; i < line.length; i++){
                    String aux = raw.get(index);
                    aux = aux.substring(2, raw.get(index).length());
                    String [] points = aux.split(" ");

                    for(int v = 0; v < points.length; v++){
                        String value = "";
                        for(int charT = 0; charT < points[v].length(); charT++){
                            char c = points[v].charAt(charT);
                            if (c != '/'){
                                value+= c;
                            }
                            else{
                                vertexUnion.add(Integer.valueOf(value));
                                break;
                            }
                        }

                    }
                    poly.setTriangle(new Triangle(vertices.get(vertexUnion.get(0) - 1),
                            vertices.get(vertexUnion.get(1) - 1),
                            vertices.get(vertexUnion.get(2) - 1),
                            Color.LIGHT_GRAY
                    ));
                    if (vertexUnion.size() == 4){
                        poly.setTriangle(new Triangle(vertices.get(vertexUnion.get(2) - 1),
                                vertices.get(vertexUnion.get(3) - 1),
                                vertices.get(vertexUnion.get(0) - 1),
                                Color.LIGHT_GRAY
                        ));
                    }

                }
            }
        }
        return poly;
    }



}
