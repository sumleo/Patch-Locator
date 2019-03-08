//package d4j_analysis.fixedmethod_locator.Utils;
//
//import d4j_analysis.fixedmethod_locator.Beans.Bucket;
//import d4j_analysis.fixedmethod_locator.Beans.Patch;
//import d4j_analysis.stacktrace.StackTraceInfo;
//
//import java.util.*;
//
//public class Rebucket {
//    public static Set<Bucket> run(LinkedList<Patch> patches) {
//           Set<Bucket> buckets=new HashSet<>();
//           HashMap<String,Bucket> stackBucket=new HashMap<>();
//           for(Patch patch:patches){
//               String key=patch.getMethodName()+patch.getVersion()
//                       +patch.getStackTraceInfo().getTestFile()+patch.getStackTraceInfo().getTestNum();
//               if(stackBucket.containsKey(key)){
//                   stackBucket.get(key).getPatches().add(patch);
//               }else {
//                   Bucket bucket=new Bucket();
//                   bucket.setStackTraceInfo(patch.getStackTraceInfo());
//                   bucket.getPatches().add(patch);
//                   stackBucket.put(key,bucket);
//                   buckets.add(bucket);
//               }
//           }
//           return buckets;
//    }
//}
