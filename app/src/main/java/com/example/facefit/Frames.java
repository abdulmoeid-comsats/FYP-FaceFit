package com.example.facefit;

public class Frames {
    public String name;
    public String model;
    public String price;
    public String weight;
    public String color;
    public String material;
    public String type;
    public String renderID;
    public String brand;
    public String shape;
    public String imgUrl;

    public Frames(){

    }

    public Frames(String name, String model, String price, String weight, String color, String material, String type, String renderID, String brand, String shape, String imgUrl) {
         this.name=name;
         this.model=model;
         this.price=price;
         this.weight=weight;
         this.color=color;
         this.material=material;
         this.type=type;
         this.renderID=renderID;
         this.brand=brand;
         this.shape=shape;
         this.imgUrl=imgUrl;
    }
}
