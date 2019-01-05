package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class ItemTicketStore extends ItemTicketBase {

    public String id;

    public ItemTicketStore(String name, String icon) {
        super(name, icon);
        this.id = icon;
    }

    public ItemTicketStore(String name, String id, String icon) {
        super(name, icon);
        this.id = id;
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

}
