package br.ufba.dcc.wiser.fot.semantic;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    static BundleContext bc;


    public void start(BundleContext bc) throws Exception {
        System.out.println("Starting the bundle FoT Semantic Data");
    }

    public void stop(BundleContext bc) throws Exception {
        System.out.println("Stopping the bundle FoT Semantic Data");
    }

}