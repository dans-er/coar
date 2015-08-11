package nl.knaw.dans.coar.zeasy;

import nl.knaw.dans.coar.rdb.JPAUtil;
import nl.knaw.dans.coar.tika.TikaProfileStore;

public class Zeasy
{
    
    private static TikaProfileStore tikaProfileStore;
    
    public static TikaProfileStore getTikaProfileStore()
    {
        if (tikaProfileStore == null) {
            tikaProfileStore = new TikaProfileStore(JPAUtil.getEntityManager());
        }
        return tikaProfileStore;
    }

}
