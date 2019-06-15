package pt.lzgpom.bot;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.realtierlist.RealTierList;
import pt.lzgpom.bot.util.real.ImageRealTierList;

public class Test {

  public static void main(String[] args) {
    try {
      JAXBContext jc = JAXBContext.newInstance(RealTierList.class);
      Marshaller marshaller = jc.createMarshaller();
      RealTierList realTierList = new RealTierList("id");
      realTierList.addChallengerToTier("S", new Challenger("Sana", "29/12/1996"));
      realTierList.addChallengerToTier("S", new Challenger("Tzuyu", "15/06/1999"));
      realTierList.addChallengerToTier("S", new Challenger("Nayeon", "29/12/1996"));
      realTierList.addChallengerToTier("A", new Challenger("Dahyun", "29/12/1996"));
      realTierList.addChallengerToTier("A", new Challenger("Jeongyeon", "29/12/1996"));
      realTierList.addChallengerToTier("B", new Challenger("Momo", "29/12/1996"));
      realTierList.addChallengerToTier("D", new Challenger("Mina", "29/12/1996"));

      RealTierList realTierList2 = new RealTierList("id");
      realTierList2.addChallengerToTier("S", new Challenger("Tzuyu", "15/06/1999"));
      realTierList2.addChallengerToTier("S", new Challenger("Sana", "29/12/1996"));
      realTierList2.addChallengerToTier("S", new Challenger("Nayeon", "29/12/1996"));
      realTierList2.addChallengerToTier("A", new Challenger("Dahyun", "29/12/1996"));
      realTierList2.addChallengerToTier("A", new Challenger("Jeongyeon", "29/12/1996"));
      realTierList2.addChallengerToTier("B", new Challenger("Momo", "29/12/1996"));
      realTierList2.addChallengerToTier("D", new Challenger("Mina", "29/12/1996"));


      List<RealTierList> lists = new ArrayList<>();
      lists.add(realTierList);
      lists.add(realTierList2);

      System.out.println(RealTierList.join("new", lists));

      // Use a label to display the image
      JFrame frame = new JFrame();

      JLabel lblimage = new JLabel(new ImageIcon(ImageRealTierList.createImage(RealTierList.join("new", lists))));
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(lblimage);
// add more components here
      frame.add(mainPanel);
      frame.setVisible(true);

      //marshaller.marshal(RealTierList.join("new", lists), new File("test.xml"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
