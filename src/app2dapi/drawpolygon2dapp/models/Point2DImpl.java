/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app2dapi.drawpolygon2dapp.models;

import app2dapi.geometry.G2D.Point2D;

/**
 *
 * @author azurwular
 */
public class Point2DImpl implements Point2D {

    private double x;
    private double y;
    
    public Point2DImpl(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }
    
}
