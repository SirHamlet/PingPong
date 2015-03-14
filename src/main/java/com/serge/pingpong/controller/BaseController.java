package com.serge.pingpong.controller;

/**
 * @author Serge Ivanov
 *         Base class to extend for all controllers
 */
public abstract class BaseController<K, V> {

    public abstract K handleRequest( V request );
}
