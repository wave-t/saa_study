package com.wave.vo.milvus;

//构建milvus数据结构的实体，用于数据保存
public class DemoMil {

    private Integer id;
    private float[] vector;
    private String color;

    public DemoMil() {
    }
    public DemoMil(Integer id, float[] vector, String color) {
        this.id = id;
        this.vector = vector;
        this.color = color;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
