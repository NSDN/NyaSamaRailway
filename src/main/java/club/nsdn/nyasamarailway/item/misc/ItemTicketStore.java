package club.nsdn.nyasamarailway.item.misc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class ItemTicketStore extends ItemTicketBase {

    public String alias;

    public ItemTicketStore(String name, String id) {
        super(name, id);
        this.alias = id;
    }

    public ItemTicketStore(String name, String id, String alias) {
        super(name, id);
        this.alias = alias;
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

}
