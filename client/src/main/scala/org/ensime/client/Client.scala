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

/**
 * Quick test of the client application.
 * Connects to the server and closes the client again.
 */
object Client {
  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = args(0).toInt
    val client = new Client()(new ClientContext(host, port, true))
    client.initialize()
    client.close()
  }
}

/**
 * Helper class for the Client.
 * Contains needed information.
 * @param host    Ip of the ensime-server.
 * @param port    Port of the ensime-server.
 * @param verbose Print verbose output.
 */
class ClientContext(
  val host: String,
  val port: Int,
  val verbose: Boolean
)

/**
 * Async client for ensime-server.
 *
 * Uses the Jerk protocol.
 *
 * How to use:
 *
 *  To start: Use initialize() to connect to the server.
 *  To close: Use close() to free the acquired resources (socket, etc)
 *
 * Client is missing a feature to capture async messages by the ensime-server.
 * TODO: Implement access to async messages from ensime-server.
 *
 * @param context ClientContext with server host and port and verbose indicator.
 */
class Client(implicit context: ClientContext)
    extends EnsimeAsyncApi with LazyLogging {

  /**
   * Ensime network client.
   */
  private val networkClient = new NetworkClientJerk()(new NetworkClientContext(context.host, context.port, context.verbose));
  /**
   * Id assigned to each outgoing message. The id in the response is used
   * to identify the promise to which to return the returned message.
   */
  private val currentCallId = new AtomicInteger(1)

  /**
   * Formats requests into Jerk messages.
   */
  private val wireFormatter = new WireFormatterJerk()

  /**
   * Sets up the client by establishing a connection with the ensime-server.
   */
  def initialize(): Unit = {
    networkClient.start()
  }

  /**
   * Close the client closing the connection with the ensime-server.
   */
  def close(): Unit = {
    networkClient.close()
  }

  /**
   * Send a request with direct response to the ensime-server.
   * @param   request RpcRequest to be sent to the ensime-server.
   * @return          Promise of a EnsimeServerMessage.
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

  /**
   * Send a request without direct response to the ensime-server.
   * @param   request RpcRequest to be sent to the ensime-server.
   */
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
  /**
   * Sends a ConnectionInfo request to the ensime-server.
   * @return  Future[ConnectionInfo]
   */
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

  /**
   * Sends a SymbolDesignationsReq to the ensime-server.
   * @param   file            File
   * @param   start           Start offset range.
   * @param   end             End offset range.
   * @param   requestedTypes  List of SourceSymbol
   * @return                  Future[SymbolDesignations]
   */
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

  /**
   * Sends a InspectTypeAtPointReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range to inspect.
   * @return        Future[TypeInspectInfo]
   */
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

  /**
   * Sends a InspectTypeByIdReq to the ensime-server.
   * @param   id  Id of the requested type.
   * @return      Future[TypeInspectInfo]
   */
  def inspectTypeById(id: Int): Future[TypeInspectInfo] = {

    val pResponse = sendRequest(InspectTypeByIdReq(id))
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
            pResponseObject.failure(new Exception("InspectTypeByIdReq failed! " + "Unexpected ReturnType"))
          }
        }
      } case _ => {
        pResponseObject.failure(new Exception("InspectTypeByIdReq failed!"))
      }
    }
    pResponseObject.future
  }

  /**
   * Sends a SymbolAtPointReq to the ensime-server.
   * @param   file  File
   * @param   point Offset of symbol.
   * @return        Future[SymbolInfo]
   */
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

  /**
   * Sends a UsesOfSymbolAtPointReq to the ensime-server.
   * @param   file  File
   * @param   point Offset of symbol.
   * @return        Future[ERangePositions]
   */
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

  /**
   * Sends a TypeAtPointReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range.
   * @return        Future[TypeInfo]
   */
  def typeAtPoint(file: File, range: OffsetRange): Future[TypeInfo] = {

    val pResponse = sendRequest(TypeAtPointReq(file, range))
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

  /**
   * Sends a InspectPackageByPathReq to the ensime-server.
   * @param   path  Package path like org.ensime.server
   * @return        Future[PackageInfo]
   */
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

  /**
   * Sends a ImplicitInfoReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range.
   * @return        Future[ImplicitInfos]
   */
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
}