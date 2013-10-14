/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */package kendzi.kendzi3d.resource.inter;

 import kendzi.util.StringUtil;

 /** Util class for resources.
  * 
  * @author kendzi
  *
  */
 public class ResourceUtil {
     /**
      * Gets directory of resource in url.
      * 
      * @param fileUrl
      *            url
      * @return directory of resource in url
      */
     public static String getUrlDrectory(String fileUrl) {

         if (StringUtil.isBlankOrNull(fileUrl)) {
             return fileUrl;
         }

         fileUrl = fileUrl.trim();

         if (fileUrl.startsWith(ResourceService.PLUGIN_FILE_PREFIX)) {
             return fileUrl;
         }

         if (fileUrl.endsWith("\\") || fileUrl.endsWith("/")) {
             return fileUrl;
         }

         int max = Math.max(fileUrl.lastIndexOf("\\"), fileUrl.lastIndexOf("/"));
         if (max != -1) {
             return fileUrl.substring(0, max);
         }
         return fileUrl;
     }

 }
