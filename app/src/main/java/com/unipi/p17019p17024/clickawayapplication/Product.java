package com.unipi.p17019p17024.clickawayapplication;

public class Product {

    public Product(){

    }

    public Product(String id, String name, double price, String type, String category, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.category = category;
        this.image = image;
    }

    private String id;
    private String name;
    private double price;
    private String type;
    private String category;
    //private File image;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //public File getImage() {
        //return image;
    //}

    //public void setImage(File image) {
        //this.image = image;
    //}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
