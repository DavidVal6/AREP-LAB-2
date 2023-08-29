package edu.eci.arep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class httpServerTest {
    
    @Test
    public void shouldMakeJPGtoHTML(){
        HTTPServer httpServer = new HTTPServer();
        File file = new File("project2\\\\src\\\\main\\\\resource\\\\imagenPrueba.jpg");
        String result;
        try {
            result = httpServer.toImage(file, "jpg");
            assertTrue(result.contains("Content-Type: text/jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void shouldMakeHTMLtoHTML(){
        HTTPServer httpServer = new HTTPServer();
        File file = new File("project2\\\\src\\\\main\\\\resource\\\\home.html");
        String result;
        try{
            result = httpServer.toHTML(file);
            assertTrue(result.contains("Content-Type: text/html"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void shouldMakeJStoHTML(){
        HTTPServer httpServer = new HTTPServer();
        File file = new File("project2\\\\src\\\\main\\\\resource\\\\pruebaJs.js");
        String result;
        try{
            result = httpServer.toJs(file);
            assertTrue(result.contains("application/javascript"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void shouldMakeCSStoHTML(){
        HTTPServer httpServer = new HTTPServer();
        File file = new File("project2\\\\src\\\\main\\\\resource\\\\pruebaCSS.css");
        String result;
        try{
            result = httpServer.toJs(file);
            assertTrue(result.contains("Content-Type: text/css"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
