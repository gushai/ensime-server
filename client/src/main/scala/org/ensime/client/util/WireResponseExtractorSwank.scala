package org.ensime.client.util
//
//import org.ensime.sexp.SexpParser
//import org.ensime.server.protocol._
//import org.ensime.server.protocol.swank._
//import org.ensime.sexp._
//import org.ensime.sexp.Sexp
//import org.ensime.sexp.SexpFormat
//import org.ensime.model._
//import org.ensime.EnsimeApi
//import org.ensime.sexp._
//import org.ensime.server.ConnectionInfo
//import scala.concurrent.Future
//
//class WireResponseExtractorSwank extends WireResponseExtractor {
//
//  import org.ensime.server.protocol.swank.SwankProtocolConversions._
//  import org.ensime.server.protocol.swank.SwankProtocolCommon._
//  import org.ensime.server.protocol.swank.SwankProtocolRequest._
//  import org.ensime.server.protocol.swank.SwankProtocolResponse._
//
//  private val succRepsonceBegin = "(:return (:ok "
//
//  def dropResponseEnvelope(msg: String): String = {
//    if (isSuccessfullResponse(msg)) {
//      return msg.substring(succRepsonceBegin.length, msg.lastIndexOf(" ") - 1)
//    }
//    return msg
//  }
//
//  def isSuccessfullResponse(msg: String): Boolean = {
//    if (msg.startsWith(succRepsonceBegin)) {
//      return true
//    }
//    return false
//  }
//
//  //  def getResponceObject(msg: String) : T = {
//  //    val sexpObj = SexpParser.parse(msg)
//  //    println("Parsed sexp object: " + sexpObj.toString())
//  //
//  //    val answer = sexpObj.convertTo[T]
//  //    SexpParser.parse(msg).convertTo[T]    
//  //  }
//
//  /*
//  def getResponce(msg: String, hint: Any) : Future[Any]= {
//   val sexpObj = SexpParser.parse(msg)   
//   hint match {
//     case "ConnectionInfo" => sexpObj.convertTo[ConnectionInfo]
//   } 
//     
//  }
//  */
//
//  def getConnectionInfo(msg: String): ConnectionInfo = {
//    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[ConnectionInfo]
//  }
//
//  def getSymbolDesignations(msg: String): SymbolDesignations = {
//    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[SymbolDesignations]
//  }
//
//  def getTypeCheckFile(msg: String): Boolean = {
//    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[Boolean]
//  }
//
//  def getTypeAtPoint(msg: String): Option[TypeInfo] = {
//    if (msg.contains("(:arrow-type t")) {
//      println("trying ArrowTypeInfo")
//      try {
//        val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[ArrowTypeInfo]
//        Some(t)
//      } catch {
//        case e: Throwable =>
//          println("[WireRepsonseExtractor] \t Incoming messing could not be converted to ArrowTypeInfo. " + e.getMessage +
//            "\nStackTrace:" + e.getStackTrace.mkString("\n") + "\n" + "\nOrg. Msg: \n" + msg + "\n----")
//          return None
//      }
//    } else {
//
//      try {
//        val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[BasicTypeInfo]
//        Some(t)
//      } catch {
//        case e: Throwable =>
//          println("[WireRepsonseExtractor] \t Incoming messing could not be converted to BasicTypeInfo. " + e.getMessage +
//            "\nStackTrace:" + e.getStackTrace.mkString("\n") + "\n" + "\nOrg. Msg: \n" + msg + "\n----")
//          return None
//      }
//
//    }
//  }
//
//  def getInspectTypeAtPoint(msg: String): Option[TypeInspectInfo] = {
//
//    //    println(msg)
//    //    println(dropResponseEnvelope(msg))
//    println(SexpParser.parse(dropResponseEnvelope(msg)))
//    try {
//      val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[TypeInspectInfo]
//      return Some(t)
//    } catch {
//      case e: Throwable =>
//        println("[WireRepsonseExtractor] \t Incoming messing could not be converted to TypeInspectInfo. " + e.getMessage +
//          "\nStackTrace:" + e.getStackTrace.mkString("\n") + "\n" + "\nOrg. Msg: \n" + msg + "\n----")
//        return None
//    }
//
//  }
//
//  def getInspectPackageByPath(msg: String): Option[PackageInfo] = {
//    try {
//      val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[PackageInfo]
//      return Some(t)
//    } catch {
//      case e: Throwable =>
//        println("[WireRepsonseExtractor] \t Incoming messing could not be converted to PackageInfo. " + e.getMessage +
//          "\nStackTrace:" + e.getStackTrace.mkString("\n") + "\n" + "\nOrg. Msg: \n" + msg + "\n----")
//        return None
//    }
//  }
//
//  def getUsesOfSymbolsAtPoint(msg: String): Future[List[ERangePosition]] = { ??? }
//
//}