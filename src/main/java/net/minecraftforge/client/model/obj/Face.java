/*    */ package net.minecraftforge.client.model.obj;
/*    */ 
/*    */ import azw;
/*    */ import bmh;
/*    */ import net.minecraftforge.fml.relauncher.Side;
/*    */ import net.minecraftforge.fml.relauncher.SideOnly;
/*    */ 
/*    */ public class Face
/*    */ {
/*    */   public Vertex[] vertices;
/*    */   public Vertex[] vertexNormals;
/*    */   public Vertex faceNormal;
/*    */   public TextureCoordinate[] textureCoordinates;
/*    */   
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void addFaceForRender(bmh tessellator)
/*    */   {
/* 18 */     addFaceForRender(tessellator, 5.0E-4F);
/*    */   }
/*    */   
/*    */   @SideOnly(Side.CLIENT)
/*    */   public void addFaceForRender(bmh tessellator, float textureOffset)
/*    */   {
/* 24 */     if (this.faceNormal == null)
/*    */     {
/* 26 */       this.faceNormal = calculateFaceNormal();
/*    */     }
/*    */     
/* 29 */     tessellator.c(this.faceNormal.x, this.faceNormal.y, this.faceNormal.z);
/*    */     
/* 31 */     float averageU = 0.0F;
/* 32 */     float averageV = 0.0F;
/*    */     
/* 34 */     if ((this.textureCoordinates != null) && (this.textureCoordinates.length > 0))
/*    */     {
/* 36 */       for (int i = 0; i < this.textureCoordinates.length; i++)
/*    */       {
/* 38 */         averageU += this.textureCoordinates[i].u;
/* 39 */         averageV += this.textureCoordinates[i].v;
/*    */       }
/*    */       
/* 42 */       averageU /= this.textureCoordinates.length;
/* 43 */       averageV /= this.textureCoordinates.length;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 48 */     for (int i = 0; i < this.vertices.length; i++)
/*    */     {
/*    */ 
/* 51 */       if ((this.textureCoordinates != null) && (this.textureCoordinates.length > 0))
/*    */       {
/* 53 */         float offsetU = textureOffset;
/* 54 */         float offsetV = textureOffset;
/*    */         
/* 56 */         if (this.textureCoordinates[i].u > averageU)
/*    */         {
/* 58 */           offsetU = -offsetU;
/*    */         }
/* 60 */         if (this.textureCoordinates[i].v > averageV)
/*    */         {
/* 62 */           offsetV = -offsetV;
/*    */         }
/*    */         
/* 65 */         tessellator.a(this.vertices[i].x, this.vertices[i].y, this.vertices[i].z, this.textureCoordinates[i].u + offsetU, this.textureCoordinates[i].v + offsetV);
/*    */       }
/*    */       else
/*    */       {
/* 69 */         tessellator.a(this.vertices[i].x, this.vertices[i].y, this.vertices[i].z);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public Vertex calculateFaceNormal()
/*    */   {
/* 76 */     azw v1 = azw.a(this.vertices[1].x - this.vertices[0].x, this.vertices[1].y - this.vertices[0].y, this.vertices[1].z - this.vertices[0].z);
/* 77 */     azw v2 = azw.a(this.vertices[2].x - this.vertices[0].x, this.vertices[2].y - this.vertices[0].y, this.vertices[2].z - this.vertices[0].z);
/* 78 */     azw normalVector = null;
/*    */     
/* 80 */     normalVector = v1.c(v2).a();
/*    */     
/* 82 */     return new Vertex((float)normalVector.a, (float)normalVector.b, (float)normalVector.c);
/*    */   }
/*    */ }


/* Location:              D:\Users\drzzm32\Downloads\forge-1.7.10-10.13.4.1614-1.7.10-universal.jar!\net\minecraftforge\client\model\obj\Face.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */