package org.ensime.client

import java.io.File
import scala.concurrent.{ Future, Promise }
import scala.util.{ Success, Failure }
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging
import org.ensime.client.util.NetworkClientJerk
import org.ensime.client.util.NetworkClientContext
import org.ensime.api.RpcRequest
import org.ensime.api.EnsimeServerMessage
import org.ensime.client.util.WireFormatterJerk
import org.ensime.api._

object Client {

  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = args(0).toInt
    val client = new Client()(new ClientContext(host, port, true))
    client.initialize()
    client.close()
  }

}

class ClientContext(
  val host: String,
  val port: Int,
  val verbose: Boolean
) {}

class Client(implicit context: ClientContext)
    extends EnsimeAsyncApi with LazyLogging {

  private val networkClient = new NetworkClientJerk()(new NetworkClientContext(context.host, context.port, context.verbose));
  private val currentCallId = new AtomicInteger(1)

  private val wireFormatter = new WireFormatterJerk()

  /**
   * Sets up the network client
   */
  def initialize(): Unit = {
    networkClient.start()
  }

  /**
   * Close the client
   */
  def close(): Unit = {
    networkClient.close()
  }

  // ===========================================================================  
  // Helpers
  // ===========================================================================

  // TODO: General extractor for the response object
  // Notes:
  // - Check dependence on arrow type..
  // ...

  // ===========================================================================  
  // 
  // ===========================================================================

  /**
   * Send the request over the wire.
   */
  private def sendRequest(request: RpcRequest): Promise[EnsimeServerMessage] = {
    val callId = currentCallId.getAndIncrement()
    val msgToBeSend = wireFormatter.toWireFormat(request, callId)
    val p = Promise[EnsimeServerMessage]
    Future {
      networkClient.sendMessage(p, callId, msgToBeSend)
    }
    p
  }

  private def sendRequestNoResponse(request: RpcRequest): Unit = {
    val callId = currentCallId.getAndIncrement()
    val msgToBeSend = wireFormatter.toWireFormat(request, callId)
    Future {
      networkClient.sendMessage(msgToBeSend)
    }
  }
  // ===========================================================================  
  // EnsimeAsyncApi implementation - BEGIN
  // ===========================================================================
  def connectionInfo(): Future[ConnectionInfo] = {

    val pResponse = sendRequest(ConnectionInfoReq)

    val pResponseObject = Promise[ConnectionInfo]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case ConnectionInfo(_, _, _) => {
            pResponseObject.success(payload.asInstanceOf[ConnectionInfo])
          }
          case _ => {
            pResponseObject.failure(new Exception("ConnectionInfoReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("ConnectionInfoReq failed!"))
      }
    }
    pResponseObject.future
  }

  def symbolDesignations(file: File, start: Int, end: Int, requestedTypes: List[SourceSymbol]): Future[SymbolDesignations] = {
    val pResponse = sendRequest(SymbolDesignationsReq(file, start, end, requestedTypes))

    val pResponseObject = Promise[SymbolDesignations]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case SymbolDesignations(_, _) => {
            pResponseObject.success(payload.asInstanceOf[SymbolDesignations])
          }
          case _ => {
            pResponseObject.failure(new Exception("SymbolDesignationsReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("SymbolDesignationsReq failed!"))
      }
    }
    pResponseObject.future

  }
  def inspectTypeAtPoint(file: File, range: OffsetRange): Future[TypeInspectInfo] = {

    val pResponse = sendRequest(InspectTypeAtPointReq(file, range))

    val pResponseObject = Promise[TypeInspectInfo]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case TypeInspectInfo(_, _, _, _) => { pResponseObject.success(payload.asInstanceOf[TypeInspectInfo]) }
          case _ => {
            pResponseObject.failure(new Exception("InspectTypeAtPointReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("InspectTypeAtPointReq failed!"))
      }
    }
    pResponseObject.future

  }
  def symbolAtPoint(file: File, point: Int): Future[SymbolInfo] = {

    val pResponse = sendRequest(SymbolAtPointReq(file, point))

    val pResponseObject = Promise[SymbolInfo]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case SymbolInfo(_, _, _, _, _, _) => { pResponseObject.success(payload.asInstanceOf[SymbolInfo]) }
          case _ => {
            pResponseObject.failure(new Exception("SymbolAtPointReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("SymbolAtPointReq failed!"))
      }
    }
    pResponseObject.future

  }
  //  def callCompletion(id: Int): Future[Option[CallCompletionInfo]] = { ??? }

  def usesOfSymAtPoint(file: File, point: Int): Future[ERangePositions] = {
    val pResponse = sendRequest(UsesOfSymbolAtPointReq(file, point))

    val pResponseObject = Promise[ERangePositions]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case ERangePositions(_) => {
            pResponseObject.success(payload.asInstanceOf[ERangePositions])
          }
          case _ => {
            pResponseObject.failure(new Exception("UsesOfSymbolAtPointReq failed! " + "Unexpected ReturnType"))
          }
        }
      } case _ => {
        pResponseObject.failure(new Exception("UsesOfSymbolAtPointReq failed!"))
      }
    }
    pResponseObject.future
  }

  def typeAtPoint(file: File, range: OffsetRange): Future[TypeInfo] = {

    val pResponse = sendRequest(TypeAtPointReq(file, range))
    // BasicTypeInfo or ArrowTypeInfo
    val pResponseObject = Promise[TypeInfo]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case BasicTypeInfo(_, _, _, _, _, _, _, _) => {
            pResponseObject.success(payload.asInstanceOf[BasicTypeInfo])
          }
          case ArrowTypeInfo(_, _, _, _) => {
            pResponseObject.success(payload.asInstanceOf[ArrowTypeInfo])
          }
          case _ => {
            pResponseObject.failure(new Exception("TypeAtPointReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("TypeAtPointReq failed!"))
      }
    }
    pResponseObject.future
  }

  def inspectPackageByPath(path: String): Future[PackageInfo] = {
    val pResponse = sendRequest(InspectPackageByPathReq(path))
    val pResponseObject = Promise[PackageInfo]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case PackageInfo(_, _, _) => {
            pResponseObject.success(payload.asInstanceOf[PackageInfo])
          }
          case _ => {
            pResponseObject.failure(new Exception("InspectPackageByPathReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("InspectPackageByPathReq failed!"))
      }
    }
    pResponseObject.future

  }

  def implicitInfoReq(file: File, range: OffsetRange): Future[ImplicitInfos] = {
    val pResponse = sendRequest(ImplicitInfoReq(file, range))
    val pResponseObject = Promise[ImplicitInfos]

    pResponse.future.onComplete {
      case esm if esm.isSuccess => {
        val payload = esm.get
        payload match {
          case EnsimeServerError(desc) => {
            pResponseObject.failure(new Exception("EnsimeSeverError " + desc))
          }
          case ImplicitInfos(_) => {
            pResponseObject.success(payload.asInstanceOf[ImplicitInfos])
          }
          case _ => {
            pResponseObject.failure(new Exception("ImplicitInfoReq failed! " + "Unexpected ReturnType"))
          }
        }

      } case _ => {
        pResponseObject.failure(new Exception("ImplicitInfoReq failed!"))
      }
    }
    pResponseObject.future
  }

  // ===========================================================================  
  // EnsimeAsyncApi implementation - END
  // ===========================================================================

}