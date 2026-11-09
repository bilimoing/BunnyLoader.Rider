import dev.bunnyloader.rider.ModForm;
import javax.swing.*;
import java.lang.reflect.*;
import java.awt.image.BufferedImage;
public class PreviewCheck {
 static Object get(Object o,String name)throws Exception {Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(o);}
 public static void main(String[] args)throws Exception {SwingUtilities.invokeAndWait(()->{try{
  ModForm f=new ModForm(null);
  ((JTextField)get(f,"displayName")).setText("<html>测试 Mod");
  ((JTextField)get(f,"author")).setText("末泠");
  ((JTextField)get(f,"version")).setText("2.0.0");
  JLabel name=(JLabel)get(f,"previewName"),details=(JLabel)get(f,"previewDetails"),icon=(JLabel)get(f,"previewIcon");
  if(!name.getText().equals("<html>测试 Mod")||!Boolean.TRUE.equals(name.getClientProperty("html.disable"))||!details.getText().equals("末泠 · v2.0.0"))throw new AssertionError("metadata preview");
  Field image=f.getClass().getDeclaredField("previewImage");image.setAccessible(true);image.set(f,new BufferedImage(240,80,BufferedImage.TYPE_INT_ARGB));
  Method update=f.getClass().getDeclaredMethod("updatePreview");update.setAccessible(true);update.invoke(f);
  if(icon.getIcon()==null||icon.getIcon().getIconWidth()>64||icon.getIcon().getIconHeight()>64)throw new AssertionError("image scaling");
  ((JButton)get(f,"removeIcon")).setEnabled(true);((JButton)get(f,"removeIcon")).doClick();
  if(icon.getIcon()!=null||!icon.getText().equals("BL"))throw new AssertionError("remove image");
  System.out.println("PASS: live name/author/version, literal text, icon scaling/removal");
 }catch(Exception e){throw new RuntimeException(e);}});}
}
