/*     */ package net.minecraftforge.client.model.obj;
/*     */ 
/*     */ import bao;
/*     */ import bmh;
/*     */ import bqw;
/*     */ import bqx;
/*     */ import bqy;
/*     */ import net.minecraftforge.fml.relauncher.Side;
/*     */ import net.minecraftforge.fml.relauncher.SideOnly;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import net.minecraftforge.client.model.IModelCustom;
/*     */ import net.minecraftforge.client.model.ModelFormatException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WavefrontObject
/*     */   implements IModelCustom
/*     */ {
/*  29 */   private static Pattern vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(v( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
/*  30 */   private static Pattern vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *\\n)|(vn( (\\-){0,1}\\d+(\\.\\d+)?){3,4} *$)");
/*  31 */   private static Pattern textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+(\\.\\d+)?){2,3} *$)");
/*  32 */   private static Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
/*  33 */   private static Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
/*  34 */   private static Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
/*  35 */   private static Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
/*  36 */   private static Pattern groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");
/*     */   private static Matcher vertexMatcher;
/*     */   private static Matcher vertexNormalMatcher;
/*     */   private static Matcher textureCoordinateMatcher;
/*     */   private static Matcher face_V_VT_VN_Matcher;
/*     */   private static Matcher face_V_VT_Matcher;
/*  42 */   private static Matcher face_V_VN_Matcher; private static Matcher face_V_Matcher; private static Matcher groupObjectMatcher; public ArrayList<Vertex> vertices = new ArrayList();
/*  43 */   public ArrayList<Vertex> vertexNormals = new ArrayList();
/*  44 */   public ArrayList<TextureCoordinate> textureCoordinates = new ArrayList();
/*  45 */   public ArrayList<GroupObject> groupObjects = new ArrayList();
/*     */   private GroupObject currentGroupObject;
/*     */   private String fileName;
/*     */   
/*     */   public WavefrontObject(bqx resource) throws ModelFormatException
/*     */   {
/*  51 */     this.fileName = resource.toString();
/*     */     
/*     */     try
/*     */     {
/*  55 */       bqw res = bao.B().Q().a(resource);
/*  56 */       loadObjModel(res.b());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  60 */       throw new ModelFormatException("IO Exception reading model format", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public WavefrontObject(String filename, InputStream inputStream) throws ModelFormatException
/*     */   {
/*  66 */     this.fileName = filename;
/*  67 */     loadObjModel(inputStream);
/*     */   }
/*     */   
/*     */   private void loadObjModel(InputStream inputStream) throws ModelFormatException
/*     */   {
/*  72 */     BufferedReader reader = null;
/*     */     
/*  74 */     String currentLine = null;
/*  75 */     int lineCount = 0;
/*     */     
/*     */     try
/*     */     {
/*  79 */       reader = new BufferedReader(new InputStreamReader(inputStream));
/*     */       
/*  81 */       while ((currentLine = reader.readLine()) != null)
/*     */       {
/*  83 */         lineCount++;
/*  84 */         currentLine = currentLine.replaceAll("\\s+", " ").trim();
/*     */         
/*  86 */         if ((!currentLine.startsWith("#")) && (currentLine.length() != 0))
/*     */         {
/*     */ 
/*     */ 
/*  90 */           if (currentLine.startsWith("v "))
/*     */           {
/*  92 */             Vertex vertex = parseVertex(currentLine, lineCount);
/*  93 */             if (vertex != null)
/*     */             {
/*  95 */               this.vertices.add(vertex);
/*     */             }
/*     */           }
/*  98 */           else if (currentLine.startsWith("vn "))
/*     */           {
/* 100 */             Vertex vertex = parseVertexNormal(currentLine, lineCount);
/* 101 */             if (vertex != null)
/*     */             {
/* 103 */               this.vertexNormals.add(vertex);
/*     */             }
/*     */           }
/* 106 */           else if (currentLine.startsWith("vt "))
/*     */           {
/* 108 */             TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
/* 109 */             if (textureCoordinate != null)
/*     */             {
/* 111 */               this.textureCoordinates.add(textureCoordinate);
/*     */             }
/*     */           }
/* 114 */           else if (currentLine.startsWith("f "))
/*     */           {
/*     */ 
/* 117 */             if (this.currentGroupObject == null)
/*     */             {
/* 119 */               this.currentGroupObject = new GroupObject("Default");
/*     */             }
/*     */             
/* 122 */             Face face = parseFace(currentLine, lineCount);
/*     */             
/* 124 */             if (face != null)
/*     */             {
/* 126 */               this.currentGroupObject.faces.add(face);
/*     */             }
/*     */           }
/* 129 */           else if ((currentLine.startsWith("g ") | currentLine.startsWith("o ")))
/*     */           {
/* 131 */             GroupObject group = parseGroupObject(currentLine, lineCount);
/*     */             
/* 133 */             if (group != null)
/*     */             {
/* 135 */               if (this.currentGroupObject != null)
/*     */               {
/* 137 */                 this.groupObjects.add(this.currentGroupObject);
/*     */               }
/*     */             }
/*     */             
/* 141 */             this.currentGroupObject = group;
/*     */           }
/*     */         }
/*     */       }
/* 145 */       this.groupObjects.add(this.currentGroupObject); return;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 149 */       throw new ModelFormatException("IO Exception reading model format", e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 155 */         reader.close();
/*     */       }
/*     */       catch (IOException localIOException3) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 164 */         inputStream.close();
/*     */       }
/*     */       catch (IOException localIOException4) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void renderAll()
/*     */   {
/* 177 */     bmh tessellator = bmh.a;
/*     */     
/* 179 */     if (this.currentGroupObject != null)
/*     */     {
/* 181 */       tessellator.a(this.currentGroupObject.glDrawingMode);
/*     */     }
/*     */     else
/*     */     {
/* 185 */       tessellator.a(4);
/*     */     }
/* 187 */     tessellateAll(tessellator);
/*     */     
/* 189 */     tessellator.a();
/*     */   }
/*     */   
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void tessellateAll(bmh tessellator)
/*     */   {
/* 195 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 197 */       groupObject.render(tessellator);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void renderOnly(String... groupNames)
/*     */   {
/* 205 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 207 */       for (String groupName : groupNames)
/*     */       {
/* 209 */         if (groupName.equalsIgnoreCase(groupObject.name))
/*     */         {
/* 211 */           groupObject.render();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void tessellateOnly(bmh tessellator, String... groupNames) {
/* 219 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 221 */       for (String groupName : groupNames)
/*     */       {
/* 223 */         if (groupName.equalsIgnoreCase(groupObject.name))
/*     */         {
/* 225 */           groupObject.render(tessellator);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void renderPart(String partName)
/*     */   {
/* 235 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 237 */       if (partName.equalsIgnoreCase(groupObject.name))
/*     */       {
/* 239 */         groupObject.render();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void tessellatePart(bmh tessellator, String partName) {
/* 246 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 248 */       if (partName.equalsIgnoreCase(groupObject.name))
/*     */       {
/* 250 */         groupObject.render(tessellator);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void renderAllExcept(String... excludedGroupNames)
/*     */   {
/* 259 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 261 */       boolean skipPart = false;
/* 262 */       for (String excludedGroupName : excludedGroupNames)
/*     */       {
/* 264 */         if (excludedGroupName.equalsIgnoreCase(groupObject.name))
/*     */         {
/* 266 */           skipPart = true;
/*     */         }
/*     */       }
/* 269 */       if (!skipPart)
/*     */       {
/* 271 */         groupObject.render();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @SideOnly(Side.CLIENT)
/*     */   public void tessellateAllExcept(bmh tessellator, String... excludedGroupNames)
/*     */   {
/* 280 */     for (GroupObject groupObject : this.groupObjects)
/*     */     {
/* 282 */       boolean exclude = false;
/* 283 */       for (String excludedGroupName : excludedGroupNames)
/*     */       {
/* 285 */         if (excludedGroupName.equalsIgnoreCase(groupObject.name))
/*     */         {
/* 287 */           exclude = true;
/*     */         }
/*     */       }
/* 290 */       if (!exclude)
/*     */       {
/* 292 */         groupObject.render(tessellator);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Vertex parseVertex(String line, int lineCount) throws ModelFormatException
/*     */   {
/* 299 */     Vertex vertex = null;
/*     */     
/* 301 */     if (isValidVertexLine(line))
/*     */     {
/* 303 */       line = line.substring(line.indexOf(" ") + 1);
/* 304 */       String[] tokens = line.split(" ");
/*     */       
/*     */       try
/*     */       {
/* 308 */         if (tokens.length == 2)
/*     */         {
/* 310 */           return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
/*     */         }
/* 312 */         if (tokens.length == 3)
/*     */         {
/* 314 */           return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException e)
/*     */       {
/* 319 */         throw new ModelFormatException(String.format("Number formatting error at line %d", new Object[] { Integer.valueOf(lineCount) }), e);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 324 */       throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */     }
/*     */     
/* 327 */     return vertex;
/*     */   }
/*     */   
/*     */   private Vertex parseVertexNormal(String line, int lineCount) throws ModelFormatException
/*     */   {
/* 332 */     Vertex vertexNormal = null;
/*     */     
/* 334 */     if (isValidVertexNormalLine(line))
/*     */     {
/* 336 */       line = line.substring(line.indexOf(" ") + 1);
/* 337 */       String[] tokens = line.split(" ");
/*     */       
/*     */       try
/*     */       {
/* 341 */         if (tokens.length == 3) {
/* 342 */           return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 346 */         throw new ModelFormatException(String.format("Number formatting error at line %d", new Object[] { Integer.valueOf(lineCount) }), e);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 351 */       throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */     }
/*     */     
/* 354 */     return vertexNormal;
/*     */   }
/*     */   
/*     */   private TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException
/*     */   {
/* 359 */     TextureCoordinate textureCoordinate = null;
/*     */     
/* 361 */     if (isValidTextureCoordinateLine(line))
/*     */     {
/* 363 */       line = line.substring(line.indexOf(" ") + 1);
/* 364 */       String[] tokens = line.split(" ");
/*     */       
/*     */       try
/*     */       {
/* 368 */         if (tokens.length == 2)
/* 369 */           return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0F - Float.parseFloat(tokens[1]));
/* 370 */         if (tokens.length == 3) {
/* 371 */           return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0F - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 375 */         throw new ModelFormatException(String.format("Number formatting error at line %d", new Object[] { Integer.valueOf(lineCount) }), e);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 380 */       throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */     }
/*     */     
/* 383 */     return textureCoordinate;
/*     */   }
/*     */   
/*     */   private Face parseFace(String line, int lineCount) throws ModelFormatException
/*     */   {
/* 388 */     Face face = null;
/*     */     
/* 390 */     if (isValidFaceLine(line))
/*     */     {
/* 392 */       face = new Face();
/*     */       
/* 394 */       String trimmedLine = line.substring(line.indexOf(" ") + 1);
/* 395 */       String[] tokens = trimmedLine.split(" ");
/* 396 */       String[] subTokens = null;
/*     */       
/* 398 */       if (tokens.length == 3)
/*     */       {
/* 400 */         if (this.currentGroupObject.glDrawingMode == -1)
/*     */         {
/* 402 */           this.currentGroupObject.glDrawingMode = 4;
/*     */         }
/* 404 */         else if (this.currentGroupObject.glDrawingMode != 4)
/*     */         {
/* 406 */           throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
/*     */         }
/*     */       }
/* 409 */       else if (tokens.length == 4)
/*     */       {
/* 411 */         if (this.currentGroupObject.glDrawingMode == -1)
/*     */         {
/* 413 */           this.currentGroupObject.glDrawingMode = 7;
/*     */         }
/* 415 */         else if (this.currentGroupObject.glDrawingMode != 7)
/*     */         {
/* 417 */           throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 422 */       if (isValidFace_V_VT_VN_Line(line))
/*     */       {
/* 424 */         face.vertices = new Vertex[tokens.length];
/* 425 */         face.textureCoordinates = new TextureCoordinate[tokens.length];
/* 426 */         face.vertexNormals = new Vertex[tokens.length];
/*     */         
/* 428 */         for (int i = 0; i < tokens.length; i++)
/*     */         {
/* 430 */           subTokens = tokens[i].split("/");
/*     */           
/* 432 */           face.vertices[i] = ((Vertex)this.vertices.get(Integer.parseInt(subTokens[0]) - 1));
/* 433 */           face.textureCoordinates[i] = ((TextureCoordinate)this.textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1));
/* 434 */           face.vertexNormals[i] = ((Vertex)this.vertexNormals.get(Integer.parseInt(subTokens[2]) - 1));
/*     */         }
/*     */         
/* 437 */         face.faceNormal = face.calculateFaceNormal();
/*     */ 
/*     */       }
/* 440 */       else if (isValidFace_V_VT_Line(line))
/*     */       {
/* 442 */         face.vertices = new Vertex[tokens.length];
/* 443 */         face.textureCoordinates = new TextureCoordinate[tokens.length];
/*     */         
/* 445 */         for (int i = 0; i < tokens.length; i++)
/*     */         {
/* 447 */           subTokens = tokens[i].split("/");
/*     */           
/* 449 */           face.vertices[i] = ((Vertex)this.vertices.get(Integer.parseInt(subTokens[0]) - 1));
/* 450 */           face.textureCoordinates[i] = ((TextureCoordinate)this.textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1));
/*     */         }
/*     */         
/* 453 */         face.faceNormal = face.calculateFaceNormal();
/*     */ 
/*     */       }
/* 456 */       else if (isValidFace_V_VN_Line(line))
/*     */       {
/* 458 */         face.vertices = new Vertex[tokens.length];
/* 459 */         face.vertexNormals = new Vertex[tokens.length];
/*     */         
/* 461 */         for (int i = 0; i < tokens.length; i++)
/*     */         {
/* 463 */           subTokens = tokens[i].split("//");
/*     */           
/* 465 */           face.vertices[i] = ((Vertex)this.vertices.get(Integer.parseInt(subTokens[0]) - 1));
/* 466 */           face.vertexNormals[i] = ((Vertex)this.vertexNormals.get(Integer.parseInt(subTokens[1]) - 1));
/*     */         }
/*     */         
/* 469 */         face.faceNormal = face.calculateFaceNormal();
/*     */ 
/*     */       }
/* 472 */       else if (isValidFace_V_Line(line))
/*     */       {
/* 474 */         face.vertices = new Vertex[tokens.length];
/*     */         
/* 476 */         for (int i = 0; i < tokens.length; i++)
/*     */         {
/* 478 */           face.vertices[i] = ((Vertex)this.vertices.get(Integer.parseInt(tokens[i]) - 1));
/*     */         }
/*     */         
/* 481 */         face.faceNormal = face.calculateFaceNormal();
/*     */       }
/*     */       else
/*     */       {
/* 485 */         throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 490 */       throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */     }
/*     */     
/* 493 */     return face;
/*     */   }
/*     */   
/*     */   private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException
/*     */   {
/* 498 */     GroupObject group = null;
/*     */     
/* 500 */     if (isValidGroupObjectLine(line))
/*     */     {
/* 502 */       String trimmedLine = line.substring(line.indexOf(" ") + 1);
/*     */       
/* 504 */       if (trimmedLine.length() > 0)
/*     */       {
/* 506 */         group = new GroupObject(trimmedLine);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 511 */       throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
/*     */     }
/*     */     
/* 514 */     return group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidVertexLine(String line)
/*     */   {
/* 524 */     if (vertexMatcher != null)
/*     */     {
/* 526 */       vertexMatcher.reset();
/*     */     }
/*     */     
/* 529 */     vertexMatcher = vertexPattern.matcher(line);
/* 530 */     return vertexMatcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidVertexNormalLine(String line)
/*     */   {
/* 540 */     if (vertexNormalMatcher != null)
/*     */     {
/* 542 */       vertexNormalMatcher.reset();
/*     */     }
/*     */     
/* 545 */     vertexNormalMatcher = vertexNormalPattern.matcher(line);
/* 546 */     return vertexNormalMatcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidTextureCoordinateLine(String line)
/*     */   {
/* 556 */     if (textureCoordinateMatcher != null)
/*     */     {
/* 558 */       textureCoordinateMatcher.reset();
/*     */     }
/*     */     
/* 561 */     textureCoordinateMatcher = textureCoordinatePattern.matcher(line);
/* 562 */     return textureCoordinateMatcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidFace_V_VT_VN_Line(String line)
/*     */   {
/* 572 */     if (face_V_VT_VN_Matcher != null)
/*     */     {
/* 574 */       face_V_VT_VN_Matcher.reset();
/*     */     }
/*     */     
/* 577 */     face_V_VT_VN_Matcher = face_V_VT_VN_Pattern.matcher(line);
/* 578 */     return face_V_VT_VN_Matcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidFace_V_VT_Line(String line)
/*     */   {
/* 588 */     if (face_V_VT_Matcher != null)
/*     */     {
/* 590 */       face_V_VT_Matcher.reset();
/*     */     }
/*     */     
/* 593 */     face_V_VT_Matcher = face_V_VT_Pattern.matcher(line);
/* 594 */     return face_V_VT_Matcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidFace_V_VN_Line(String line)
/*     */   {
/* 604 */     if (face_V_VN_Matcher != null)
/*     */     {
/* 606 */       face_V_VN_Matcher.reset();
/*     */     }
/*     */     
/* 609 */     face_V_VN_Matcher = face_V_VN_Pattern.matcher(line);
/* 610 */     return face_V_VN_Matcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidFace_V_Line(String line)
/*     */   {
/* 620 */     if (face_V_Matcher != null)
/*     */     {
/* 622 */       face_V_Matcher.reset();
/*     */     }
/*     */     
/* 625 */     face_V_Matcher = face_V_Pattern.matcher(line);
/* 626 */     return face_V_Matcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidFaceLine(String line)
/*     */   {
/* 636 */     return (isValidFace_V_VT_VN_Line(line)) || (isValidFace_V_VT_Line(line)) || (isValidFace_V_VN_Line(line)) || (isValidFace_V_Line(line));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isValidGroupObjectLine(String line)
/*     */   {
/* 646 */     if (groupObjectMatcher != null)
/*     */     {
/* 648 */       groupObjectMatcher.reset();
/*     */     }
/*     */     
/* 651 */     groupObjectMatcher = groupObjectPattern.matcher(line);
/* 652 */     return groupObjectMatcher.matches();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 658 */     return "obj";
/*     */   }
/*     */ }


/* Location:              D:\Users\drzzm32\Downloads\forge-1.7.10-10.13.4.1614-1.7.10-universal.jar!\net\minecraftforge\client\model\obj\WavefrontObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */