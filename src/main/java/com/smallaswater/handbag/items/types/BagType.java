package com.smallaswater.handbag.items.types;

/**
 * @author SmallasWater
 */

public enum  BagType {
    /**
     * 小容量
     * */
    SMALL("small",27),
    /**
     * 小容量
     * */
    TO_SMALL("sosmall",5),
    /**
     * 大容量
     * */
    BIG("big",54);
    protected int size;

    protected String name;

    BagType(String name,int size){
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
