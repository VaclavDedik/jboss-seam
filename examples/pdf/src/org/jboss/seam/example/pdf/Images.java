package org.jboss.seam.example.pdf;

import org.jboss.seam.annotations.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.net.URL;


import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.ui.*;
import org.jfree.data.general.*;


@Name("images")
public class Images {
    private PieDataset getData() {
        DefaultPieDataset data = new DefaultPieDataset();

        data.setValue("gen",   7);
        data.setValue("mail",  15);
        data.setValue("main",  387);
        data.setValue("pdf",   28);
        data.setValue("test",  22);
        data.setValue("ui",    54);
        return data;
    }

    public Image getChart() {
        JFreeChart chart = ChartFactory.createPieChart("Seam Classes by Module",
                                                       getData(),
                                                       true,
                                                       true,
                                                       false);

        return chart.createBufferedImage(400,300);
    }

}
