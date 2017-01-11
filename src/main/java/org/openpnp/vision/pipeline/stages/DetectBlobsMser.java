package org.openpnp.vision.pipeline.stages;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.features2d.MSER;
import org.openpnp.vision.pipeline.CvPipeline;
import org.openpnp.vision.pipeline.CvStage;
import org.openpnp.vision.pipeline.Property;
import org.simpleframework.xml.Attribute;

public class DetectBlobsMser extends CvStage {
    @Attribute
    @Property(description="it compares (sizei−sizei−delta)/sizei−delta")
    int delta = 5;

    @Attribute
    @Property(description="prune the area which smaller than minArea")
    int minArea = 60;
    
    @Attribute
    @Property(description="prune the area which bigger than maxArea")
    int maxArea = 14400;

    @Attribute
    @Property(description="prune the area have simliar size to its children")
    double maxVariation = 0.25;
    
    @Attribute
    @Property(description="for color image, trace back to cut off mser with diversity less than min_diversity")
    double minDiversity = 0.2;
    
    @Attribute
    @Property(description="for color image, the evolution steps")
    int maxEvolution = 200;
    
    @Attribute
    @Property(description="for color image, the area threshold to cause re-initialize")
    double areaThreshold = 1.01;
    
    @Attribute
    @Property(description="for color image, ignore too small margin")
    double minMargin = 0.003;
    
    @Attribute
    @Property(description="for color image, the aperture size for edge blur")
    int edgeBlurSize = 5;

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getMinArea() {
        return minArea;
    }

    public void setMinArea(int minArea) {
        this.minArea = minArea;
    }

    public int getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    public double getMaxVariation() {
        return maxVariation;
    }

    public void setMaxVariation(double maxVariation) {
        this.maxVariation = maxVariation;
    }

    public double getMinDiversity() {
        return minDiversity;
    }

    public void setMinDiversity(double minDiversity) {
        this.minDiversity = minDiversity;
    }

    public int getMaxEvolution() {
        return maxEvolution;
    }

    public void setMaxEvolution(int maxEvolution) {
        this.maxEvolution = maxEvolution;
    }

    public double getAreaThreshold() {
        return areaThreshold;
    }

    public void setAreaThreshold(double areaThreshold) {
        this.areaThreshold = areaThreshold;
    }

    public double getMinMargin() {
        return minMargin;
    }

    public void setMinMargin(double minMargin) {
        this.minMargin = minMargin;
    }

    public int getEdgeBlurSize() {
        return edgeBlurSize;
    }

    public void setEdgeBlurSize(int edgeBlurSize) {
        this.edgeBlurSize = edgeBlurSize;
    }

    @Override
    public Result process(CvPipeline pipeline) throws Exception {
        Mat mat = pipeline.getWorkingImage();
        MSER mser = MSER.create(delta, minArea, maxArea, maxVariation, minDiversity, maxEvolution, areaThreshold, minMargin, edgeBlurSize);
        List<MatOfPoint> msers = new ArrayList<>();
        MatOfRect bboxes = new MatOfRect();
        mser.detectRegions(mat, msers, bboxes);
        return new Result(mat, msers);
    }
}
