package com.example.finalproject;

import java.io.Serializable;

public class Stock implements Serializable {
    private String symbol;
    private String name;

    public Stock(){
        symbol = "";
        name = "";
    }

    public Stock(String symbol, String name){
        this.symbol = symbol;
        this.name = name;
    }

    public String getName(){ return name; }
    public String getSymbol() { return symbol; }
}
