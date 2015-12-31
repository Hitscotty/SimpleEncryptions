package PonyExpress;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class AppletNLO extends Applet
{

  //-------------------------------------------------------------------------//

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public AppletNLO()
   {
      setLayout(null);
   }

  //-------------------------------------------------------------------------//

   public void add(Component c, int x, int y, int w, int h)
   {
      c.setBounds(x, y, w, h);

      add(c);
   }

  //-------------------------------------------------------------------------//

   public void add(Component c, int x, int y)
   {
      add(c, x, y, 100, 20);
   }

  //-------------------------------------------------------------------------//

   public void add(Button b, int x, int y, int w, int h, ActionListener al)
   {
       add(b, x, y, w, h);

       b.addActionListener(al);
   }

  //-------------------------------------------------------------------------//

   public void add(TextField tf, int x, int y, int w, int h, ActionListener al)
   {
       add(tf, x, y, w, h);

       tf.addActionListener(al);
   }

  //-------------------------------------------------------------------------//

   public void add(Button b, int x, int y, ActionListener al)
   {
      add(b, x, y, 100, 20, al);
   }

  //-------------------------------------------------------------------------//

}