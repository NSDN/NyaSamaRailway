package net.minecraftforge.client.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract interface IModelCustom
{
  public abstract String getType();
  
  @SideOnly(Side.CLIENT)
  public abstract void renderAll();
  
  @SideOnly(Side.CLIENT)
  public abstract void renderOnly(String... paramVarArgs);
  
  @SideOnly(Side.CLIENT)
  public abstract void renderPart(String paramString);
  
  @SideOnly(Side.CLIENT)
  public abstract void renderAllExcept(String... paramVarArgs);
}


/* Location:              D:\Users\drzzm32\Downloads\forge-1.7.10-10.13.4.1614-1.7.10-universal.jar!\net\minecraftforge\client\model\IModelCustom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */