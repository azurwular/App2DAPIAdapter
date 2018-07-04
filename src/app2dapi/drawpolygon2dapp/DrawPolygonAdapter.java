/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app2dapi.drawpolygon2dapp;

import app2dapi.App2D;
import app2dapi.Device;
import app2dapi.drawpolygon2dapp.models.Point2DImpl;
import app2dapi.geometry.G2D;
import app2dapi.geometry.G2D.Dimension2D;
import app2dapi.geometry.G2D.Point2D;
import app2dapi.geometry.G2D.Transformation2D;
import app2dapi.graphics.Canvas;
import app2dapi.graphics.ColorFactory;
import app2dapi.input.keyboard.Key;
import app2dapi.input.keyboard.KeyPressedEvent;
import app2dapi.input.keyboard.KeyReleasedEvent;
import app2dapi.input.keyboard.KeyboardListener;
import app2dapi.input.mouse.MouseButton;
import app2dapi.input.mouse.MouseEvent;
import app2dapi.input.mouse.MouseListener;
import app2dapi.input.mouse.MouseMovedEvent;
import app2dapi.input.mouse.MousePressedEvent;
import app2dapi.input.mouse.MouseReleasedEvent;
import app2dapi.input.mouse.MouseWheelEvent;
import app2dapi.viewwindow.ViewWindow;
import app2dapi.viewwindow.impl.ViewWindowImpl;
import java.util.ArrayList;



/**
 *
 * @author azurwular
 */
public class DrawPolygonAdapter implements App2D, DrawPolygonToolKit, MouseListener, KeyboardListener
{
    private final DrawPolygon2DApp app;
    private DrawPolygonInit init;
    private G2D g2d;
    private ColorFactory cf;
    private ViewWindow view;
    private Canvas canvas;
    private Transformation2D fromHUDToScreen;
    private Transformation2D fromScreenToHUD;
    private Point2D mouseScreenPos;
    private Point2D mouseHUDPos;
    private Point2D mouseWorldPos;
    
    private ArrayList<Point2D> polygonPoints;
    

    public DrawPolygonAdapter(DrawPolygon2DApp app)
    {
        this.app = app;
    }
    
    @Override
    public boolean initialize(Device device)
    {
        this.g2d = device.getGeometry2D();
        this.cf = device.getScreen().getColorFactory();
        double screenX = device.getScreen().getPixelWidth();
        double screenY = device.getScreen().getPixelHeight();
        double aspectRatio = screenX / screenY;
        init = app.initialize(this, aspectRatio);
        Dimension2D sizeHUD = g2d.newDimension2D(init.getHUDMax().x() - init.getHUDMin().x(),
                                                 init.getHUDMax().y() - init.getHUDMin().y());
        Point2D centerHUD = g2d.center(init.getHUDMin(), init.getHUDMax());
        double scale = screenX / sizeHUD.width();
        this.fromHUDToScreen = g2d.combine(g2d.scale(scale, scale), g2d.translatePointToOrigo(centerHUD));
        this.fromScreenToHUD = g2d.inverse(fromHUDToScreen);
        view = new ViewWindowImpl(g2d, init.getHUDMin(), init.getHUDMax(), init.getViewStartWidth(), init.getWorldStartPos());
        
        device.getMouse().addMouseListener(this);
        device.getKeyboard().addKeyboardListener(this);
        return true;
    }
      
    @Override
    public boolean showMouseCursor()
    {
        return app.showMouseCursor();
    }

    @Override
    public boolean update(double time)
    {
        return app.update(time);
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.clear(app.getBackgroundColor());
        canvas.setTransformation(g2d.combine(fromHUDToScreen, view.getWorldToHUD()));
        app.drawWorld(canvas);
        canvas.setTransformation(fromHUDToScreen);
        app.drawHUD(canvas);
    }

    @Override
    public void destroy()
    {
        app.destroy();
    }

    @Override
    public G2D g2d()
    {
        return g2d;
    }

    @Override
    public ColorFactory cf()
    {
        return cf;
    }
    
    @Override
    public void onMousePressed(MousePressedEvent e)
    {
        updateMousePositions(e);
        if(e.getButton() == MouseButton.LEFT && inHUD(mouseWorldPos))
        {
            if(this.polygonPoints == null || this.polygonPoints.isEmpty())
            {
                this.polygonPoints = new ArrayList<>();
            }
            
            // new point for the polygon
            Point2D newPolygonPoint = new Point2DImpl(mouseHUDPos.x(), mouseHUDPos.y());
            this.polygonPoints.add(newPolygonPoint);
            
            this.canvas.drawPoint(newPolygonPoint, 1);
            
            if (this.polygonPoints.size() > 1) {
                // draw a vector from the last point to the new one
                Point2D lastPolygonPoint = this.polygonPoints.get(this.polygonPoints.size() - 2);
                this.canvas.drawLine(lastPolygonPoint, newPolygonPoint);
            }
        }
        else if(e.getButton() == MouseButton.RIGHT)
        {
            app.onMousePressed(mouseHUDPos, mouseWorldPos);
        }
    }

    @Override
    public void onMouseMoved(MouseMovedEvent e)
    {
        updateMousePositions(e);
        app.onMouseMoved(mouseHUDPos, mouseWorldPos);
    }
    
    @Override
    public void onMouseReleased(MouseReleasedEvent e)
    {
        updateMousePositions(e);
        app.onMouseReleased(mouseHUDPos, mouseWorldPos);
    }


    @Override
    public void onMouseWheel(MouseWheelEvent e)
    {
        updateMousePositions(e);
        view.setWorldMatchPoint(mouseWorldPos);
        view.setHUDMatchPoint(mouseHUDPos);
        
    }

    @Override
    public void onKeyPressed(KeyPressedEvent e)
    {
        if (e.getKey() == Key.VK_ENTER) {
            // close the polygon by connecting its first and last points
            this.canvas.drawLine(this.polygonPoints.get(0), this.polygonPoints.get(this.polygonPoints.size() - 1));
            
            // cleanup
            this.polygonPoints = null;
        }
        app.onKeyPressed(e);
    }
    
    @Override
    public void onKeyReleased(KeyReleasedEvent e) {
        app.onKeyReleased(e);
    }
    
    private void updateMousePositions(MouseEvent e)
    {
        mouseScreenPos = g2d.newPoint2D(e.getX(), e.getY());
        mouseHUDPos = fromScreenToHUD.transform(mouseScreenPos);
        mouseWorldPos = view.fromHUDToWorld(mouseHUDPos);
    }

    private boolean inHUD(Point2D mouseWorldPos)
    {
        return mouseWorldPos.x() >= init.getHUDMin().x() && mouseWorldPos.y() >= init.getHUDMin().y() &&
               mouseWorldPos.x() <= init.getHUDMax().x() && mouseWorldPos.y() <= init.getWorldMax().y();
    }
}
