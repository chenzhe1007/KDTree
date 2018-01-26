/**
 * Created by Luke on 1/25/18.
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;

public class KdTree {

    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        private int size;
        private int orientation;

        public Node(Point2D p) {
            this.p = p;
            lb = rt = null;
            this.size = 1;
        }

        public Node(Point2D p, RectHV rect, int orientation) {
            this.rect = rect;
            this.p = p;
            lb = rt = null;
            this.size = 1;
            this.orientation = orientation;
        }
    }
    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 0;
    private Node root;

    public KdTree() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        if (isEmpty()) {
            return 0;
        }
        return size(root);
    }

    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (contains(p)) {
            return;
        }
        root = insert(root, p, VERTICAL, 0.0, 0.0, 1.0, 1.0);
    }

    /**
     *
     * @param current the current node to compare
     * @param next new point
     * @param orientation 0 represents horizontal, 1 represents vertical
     * @return
     */
    private Node insert(Node current, Point2D next, int orientation, double minX, double minY, double maxX, double maxY) {
        if (current == null) {
            return new Node(next, new RectHV(minX, minY, maxX, maxY), orientation);
        }
        double cmp = 0.0;
        if (orientation == VERTICAL) {
            cmp = next.x() - current.p.x();
            if (cmp >= 0) {
                current.rt = insert(current.rt, next, HORIZONTAL, current.p.x(), current.rect.ymin(), current.rect.xmax(), current.rect.ymax());
            } else {
                current.lb = insert(current.lb, next, HORIZONTAL, current.rect.xmin(), current.rect.ymin(), current.p.x(), current.rect.ymax());
            }
        } else {
            cmp = next.y() - current.p.y();
            if (cmp >= 0) {
                current.rt = insert(current.rt, next, VERTICAL, current.rect.xmin(), current.p.y(), current.rect.xmax(), current.rect.ymax());
            } else {
                current.lb = insert(current.lb, next, VERTICAL, current.rect.xmin(), current.rect.ymin(), current.rect.xmax(), current.p.y());
            }
        }

        current.size = size(current.lb) + size(current.rt) + 1;
        return current;
    }



    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        Node cur = root;
        int i = 0;
        while (cur != null) {
            if (cur.p.x() == p.x() && cur.p.y() == p.y()) {
                return true;
            }
            if (i % 2 == 0) {
                if (p.x() >= cur.p.x()) {
                    cur = cur.rt;
                } else {
                    cur = cur.lb;
                }
            } else {
                if (p.y() >= cur.p.y()) {
                    cur = cur.rt;
                } else {
                    cur = cur.lb;
                }
            }
            i++;
        }
        return false;
    }

    public void draw() {
        Queue<Node> queue = new Queue<Node>();
        preOrder(queue, root);
        for (Node node : queue) {
            StdDraw.setPenRadius();
            if (node.orientation == VERTICAL) {
                StdDraw.setPenColor(StdDraw.RED);
                Point2D tr = new Point2D(node.p.x(), node.rect.ymax());
                tr.drawTo(new Point2D(node.p.x(), node.rect.ymin()));
            } else if (node.orientation == HORIZONTAL){
                StdDraw.setPenColor(StdDraw.BLUE);
                Point2D tr = new Point2D(node.rect.xmin(), node.p.y());
                tr.drawTo(new Point2D(node.rect.xmax(), node.p.y()));
            }
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            node.p.draw();

        }
    }

    private void preOrder(Queue<Node> queue, Node root) {
        if (root == null) return;

        queue.enqueue(root);
        preOrder(queue, root.lb);
        preOrder(queue, root.rt);
    }

    public Iterable<Point2D> range(RectHV rectHV) {
        if (rectHV == null) throw new IllegalArgumentException();
        Queue<Point2D> queue = new Queue<Point2D>();
        rangePreOrder(queue, root, rectHV);
        return queue;
    }

    private void rangePreOrder(Queue<Point2D> queue, Node root, RectHV rectHV) {
        if (root == null) return;

        if (!rectHV.intersects(root.rect)) {
            return;
        }

        if (rectHV.contains(root.p)) {
            queue.enqueue(root.p);
        }
        rangePreOrder(queue, root.lb, rectHV);
        rangePreOrder(queue, root.rt, rectHV);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return nearest(p, root);
    }

    private Point2D nearest(Point2D p, Node root) {
        if (root == null) {
            return null;
        }

        Point2D left = nearest(p, root.lb);
        Point2D right = nearest(p, root.rt);

        double disLeft = left == null ? Double.MAX_VALUE : left.distanceTo(p);
        double disRight = right == null ? Double.MAX_VALUE : right.distanceTo(p);
        double disRoot = root.p.distanceTo(p);

        if (disLeft <= disRight && disLeft <= disRoot) {
            return left;
        } else if (disRight <= disLeft && disRight <= disRoot) {
            return right;
        } else {
            return root.p;
        }

    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        //PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);

            kdtree.insert(p);
            //StdOut.println(kdtree.contains(p));

            //kdtree.contains(p);
            //StdOut.println(kdtree.size());
        }
        kdtree.draw();
        StdDraw.show();
    }
}
