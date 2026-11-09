package editor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Eigenes Logo des Editors – komplett mit Java2D gezeichnet (keine Bilddatei).
 *
 * Das Logo wird in einem gedachten 100 x 100 Koordinatensystem gezeichnet und
 * danach auf die echte Größe skaliert. Dadurch ist es beliebig skalierbar und
 * bleibt in jeder Größe scharf (16 px Fenstersymbol bis 128 px im "Über"-Dialog).
 *
 * Motiv: Blatt Papier mit Textzeilen, der roten 60-Spalten-Linie und einem Stift.
 */
public class LogoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public LogoPanel(int size) {
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
        setToolTipText("WeDoText – We-Do-IT GmbH");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g.create();
        drawLogo(g2, (getWidth() - size) / 2, (getHeight() - size) / 2, size);
        g2.dispose();
    }

    /** Zeichnet das Logo an Position (x, y) mit der Kantenlänge size. */
    public static void drawLogo(Graphics2D g, int x, int y, int size) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        g.scale(size / 100.0, size / 100.0); // ab hier: Koordinaten 0..100

        // Hintergrund: abgerundetes Quadrat mit Farbverlauf
        g.setPaint(new GradientPaint(0, 0, new Color(0x1E3A8A), 100, 100, new Color(0x0EA5E9)));
        g.fill(new RoundRectangle2D.Double(0, 0, 100, 100, 26, 26));

        // Weißes Blatt mit umgeknickter Ecke
        Path2D page = new Path2D.Double();
        page.moveTo(22, 14);
        page.lineTo(62, 14);
        page.lineTo(76, 28);
        page.lineTo(76, 86);
        page.lineTo(22, 86);
        page.closePath();
        g.setColor(Color.WHITE);
        g.fill(page);

        Path2D corner = new Path2D.Double();
        corner.moveTo(62, 14);
        corner.lineTo(62, 28);
        corner.lineTo(76, 28);
        corner.closePath();
        g.setColor(new Color(0xBFDBFE));
        g.fill(corner);

        // Textzeilen
        g.setColor(new Color(0x64748B));
        g.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double[] lineEnds = {66, 56, 68, 46};
        for (int i = 0; i < lineEnds.length; i++) {
            double ly = 38 + i * 11;
            g.draw(new Line2D.Double(30, ly, lineEnds[i], ly));
        }

        // Rote Linie (wie die 60-Spalten-Markierung im Editor)
        g.setColor(new Color(0xEF4444));
        g.setStroke(new BasicStroke(3f));
        g.draw(new Line2D.Double(60, 31, 60, 80));

        // Stift (schräg gestellt)
        Graphics2D pen = (Graphics2D) g.create();
        pen.translate(72, 80);
        pen.rotate(Math.toRadians(-45));
        pen.setColor(new Color(0xF59E0B));                     // Schaft
        pen.fill(new Rectangle2D.Double(0, -5, 26, 10));
        pen.setColor(new Color(0xF472B6));                     // Radiergummi
        pen.fill(new Rectangle2D.Double(26, -5, 7, 10));
        Path2D tip = new Path2D.Double();                      // Holzspitze
        tip.moveTo(0, -5);
        tip.lineTo(-10, 0);
        tip.lineTo(0, 5);
        tip.closePath();
        pen.setColor(new Color(0xFDE68A));
        pen.fill(tip);
        Path2D lead = new Path2D.Double();                     // Mine
        lead.moveTo(-6, -2);
        lead.lineTo(-10, 0);
        lead.lineTo(-6, 2);
        lead.closePath();
        pen.setColor(new Color(0x1F2937));
        pen.fill(lead);
        pen.dispose();
    }

    /** Erzeugt das Logo als Bild in der gewünschten Größe. */
    public static BufferedImage createImage(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        drawLogo(g, 0, 0, size);
        g.dispose();
        return image;
    }

    /** Mehrere Größen, damit Windows für Titelleiste und Taskleiste jeweils ein scharfes Symbol wählt. */
    public static List<Image> createIcons() {
        List<Image> icons = new ArrayList<>();
        for (int size : new int[] {16, 24, 32, 48, 64, 128}) {
            icons.add(createImage(size));
        }
        return icons;
    }
}
