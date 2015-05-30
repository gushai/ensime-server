package org.ensime.client.util

import org.ensime.sexp.SexpParser
import org.ensime.server.protocol._
import org.ensime.server.protocol.swank._
import org.ensime.sexp._
import org.ensime.sexp.Sexp
import org.ensime.sexp.SexpFormat
import org.ensime.model._
import org.ensime.EnsimeApi
import org.ensime.sexp._
import org.ensime.server.ConnectionInfo
import scala.concurrent.Future

class WireResponseExtractorSwank extends WireResponseExtractor {

  import org.ensime.server.protocol.swank.SwankProtocolConversions._
  import org.ensime.server.protocol.swank.SwankProtocolCommon._
  import org.ensime.server.protocol.swank.SwankProtocolRequest._
  import org.ensime.server.protocol.swank.SwankProtocolResponse._

  private val succRepsonceBegin = "(:return (:ok "

  def dropResponseEnvelope(msg: String): String = {
    if (isSuccessfullResponse(msg)) {
      return msg.substring(succRepsonceBegin.length, msg.lastIndexOf(" ") - 1)
    }
    return msg
  }

  def isSuccessfullResponse(msg: String): Boolean = {
    if (msg.startsWith(succRepsonceBegin)) {
      return true
    }
    return false
  }

  //  def getResponceObject(msg: String) : T = {
  //    val sexpObj = SexpParser.parse(msg)
  //    println("Parsed sexp object: " + sexpObj.toString())
  //
  //    val answer = sexpObj.convertTo[T]
  //    SexpParser.parse(msg).convertTo[T]    
  //  }

  /*
  def getResponce(msg: String, hint: Any) : Future[Any]= {
   val sexpObj = SexpParser.parse(msg)   
   hint match {
     case "ConnectionInfo" => sexpObj.convertTo[ConnectionInfo]
   } 
     
  }
  */

  def getConnectionInfo(msg: String): ConnectionInfo = {
    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[ConnectionInfo]
  }

  def getSymbolDesignations(msg: String): SymbolDesignations = {
    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[SymbolDesignations]
  }

  def getTypeCheckFile(msg: String): Boolean = {
    SexpParser.parse(dropResponseEnvelope(msg)).convertTo[Boolean]
  }

  def getTypeAtPoint(msg: String): Option[TypeInfo] = {
    val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[BasicTypeInfo]
    Some(t)
  }

  def getInspectTypeAtPoint(msg: String): Option[TypeInspectInfo] = {
    ???
    println(msg)
    println(dropResponseEnvelope(msg))
    println(SexpParser.parse(dropResponseEnvelope(msg)))
    val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[TypeInspectInfo]
    Some(t)
  }

  def getInspectPackageByPath(msg: String): Option[PackageInfo] = {
    val t = SexpParser.parse(dropResponseEnvelope(msg)).convertTo[PackageInfo]
    Some(t)
  }

  def getUsesOfSymbolsAtPoint(msg: String): Future[List[ERangePosition]] = { ??? }

}