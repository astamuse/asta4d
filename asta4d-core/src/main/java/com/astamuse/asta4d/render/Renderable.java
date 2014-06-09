package com.astamuse.asta4d.render;

/**
 * A callback interface that allows delay the rendering logic until the real rendering is required
 * 
 * @author e-ryu
 * 
 */
public interface Renderable {
    public Renderer render();
}
