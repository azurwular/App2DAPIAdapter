/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app2dapi.drawpolygon2dapp;

import app2dapi.geometry.G2D.Point2D;
import app2dapi.graphics.Canvas;
import app2dapi.graphics.Color;
import app2dapi.input.keyboard.KeyPressedEvent;
import app2dapi.input.keyboard.KeyReleasedEvent;

/**
 *
 * @author azurwular
 */
public interface DrawPolygon2DApp
{
    //Initialization
    public DrawPolygonInit initialize(DrawPolygonToolKit tk, double aspectRatio);
    
    //Mouse input
    public boolean showMouseCursor();
    public void onMouseMoved(Point2D mouseHUDPos, Point2D mouseWorldPos);
    public void onMousePressed(Point2D mouseHUDPos, Point2D mouseWorldPos);
    public void onMouseReleased(Point2D mouseHUDPos, Point2D mouseWorldPos);
    
    //Keyboard input
    public void onKeyPressed(KeyPressedEvent e);
    public void onKeyReleased(KeyReleasedEvent e);
    
    //Loop
    public boolean update(double time);
    public Color getBackgroundColor();
    public void drawWorld(Canvas canvas);
    public void drawHUD(Canvas canvas);
    
    //Cleanup
    public void destroy();
}
