/*    */ package net.minecraftforge.client.model.obj;
/*    */ 
/*    */ import bmh;
/*    */ import net.minecraftforge.fml.relauncher.Side;
/*    */ import net.minecraftforge.fml.relauncher.SideOnly;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ public class GroupObject
/*    */ {
/*    */   public String name;
/* 12 */   public ArrayList<Face> faces = new ArrayList();
/*    */   public int glDrawingMode;
/*    */   
/*    */   public GroupObject()
/*    */   {
/* 17 */     this("");
/*    */   }
/*    */   
/*    */   public GroupObject(String name)
/*    */   {
/* 22 */     this(name, -1);
/*    */   }
/*    */   
/*    */   public GroupObject(String name, int glDrawingMode)
/*    */   {
/* 27 */     this.name = name;
/* 28 */     this.glDrawingMode = glDrawingMode;
/*    */   }
/*    */   
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void render()
/*    */   {
/* 34 */     if (this.faces.size() > 0)
/*    */     {
/* 36 */       bmh tessellator = bmh.a;
/* 37 */       tessellator.a(this.glDrawingMode);
/* 38 */       render(tessellator);
/* 39 */       tessellator.a();
/*    */     }
/*    */   }
/*    */   
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void render(bmh tessellator)
/*    */   {
/* 46 */     if (this.faces.size() > 0)
/*    */     {
/* 48 */       for (Face face : this.faces)
/*    */       {
/* 50 */         face.addFaceForRender(tessellator);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Users\drzzm32\Downloads\forge-1.7.10-10.13.4.1614-1.7.10-universal.jar!\net\minecraftforge\client\model\obj\GroupObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */