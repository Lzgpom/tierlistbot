package pt.lzgpom.bot.util;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import pt.lzgpom.bot.model.Bot;

public class SaveLoader {

  private static final String DEFAULT_LOCALE = "./save/save.xml";

  public static Bot readCentre() {
    try {
      JAXBContext jc = JAXBContext.newInstance(Bot.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      return (Bot) unmarshaller.unmarshal(new File(DEFAULT_LOCALE));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void saveCentre(Bot centre) {
    try {
      JAXBContext jc = JAXBContext.newInstance(Bot.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.marshal(centre, new File(DEFAULT_LOCALE));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
