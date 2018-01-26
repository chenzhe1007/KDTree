/**
 * Created by Luke on 1/25/18.
 */
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;

import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> set;
    public PointSET() {
        set = new TreeSet<Point2D>();
    }

    public boolean isEmpty() {
        return set.size() == 0;
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        set.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return set.contains(p);
    }

    public void draw() {
        for (Point2D point : set) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Queue<Point2D> queue = new Queue<Point2D>();
        for (Point2D point : set) {
            if (point.x() <= rect.xmax() && point.x() >= rect.xmin() && point.y() <= rect.ymax() && point.y() >= rect.ymin()) {
                queue.enqueue(point);
            }
        }
        return queue;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        double minDis = Double.MAX_VALUE;
        Point2D ans = null;
        for (Point2D point : set) {
            double dis = point.distanceTo(p);
            if (dis < minDis) {
                minDis = dis;
                ans = point;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        //KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
    }
}
