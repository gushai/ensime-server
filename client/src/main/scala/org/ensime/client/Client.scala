package org.ensime.client

import org.ensime.EnsimeAsyncApi
import java.io.File
import org.ensime.core._
import org.ensime.model._
import org.ensime.server.ConnectionInfo
import org.ensime.util.{ FileRange, RefactorType }
import scala.concurrent.{ Future, Promise }
import scala.util.{ Success, Failure }
import java.util.concurrent.atomic.AtomicInteger
import org.ensime.server.protocol._
import org.ensime.client.util.WireFormatterSwank
import org.ensime.client.util.WireResponseExtractorSwank
import org.ensime.server.protocol.RpcRequest

import scala.concurrent.ExecutionContext.Implicits.global

object Client {

  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = 55222
    val client = new Client(host, port)
    client.initialize()

    client.close()
    // 
  }

}

class Client(host: String, port: Int) extends EnsimeAsyncApi {

  private val networkClient = new NetworkClient(host, port);
  private val currentCallId = new AtomicInteger(1)

  private val wireFormatter = new WireFormatterSwank()
  private val responseExtractor = new WireResponseExtractorSwank()

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

  /// TODO: Delete/ override with prof logger
  def log(s: String): Unit = {
    println("[ENSIME-Client]\t" + s)
  }

  // ===========================================================================  
  // 
  // ===========================================================================

  /**
   * Send the request over the wire.
   */
  private def sendRequest(request: RpcRequest): Promise[String] = {
    val callId = currentCallId.getAndIncrement()
    val msgToBeSend = wireFormatter.toWireFormat(request, callId)
    val p = Promise[String]
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
    val pResponseMessage = sendRequest(ConnectionInfoReq)

    /// TODO: Add timeout exception! and failure...!!!
    val pResponseObject = Promise[ConnectionInfo]

    pResponseMessage.future.onComplete {
      case msg if msg.isSuccess && responseExtractor.isSuccessfullResponse(msg.get) => {
        pResponseObject.success(responseExtractor.getConnectionInfo(msg.get))

      }
      case _ => {
        /// TODO: Include message information? 
        throw new Exception("Unsuccessful response. ")
      }

    }
    pResponseObject.future
  }

  def shutdownServer(): Unit = {
    sendRequestNoResponse(ShutdownServerReq)
  }

  def subscribeAsync(handler: EnsimeEvent => Unit): Future[Boolean] = { ??? }

  def peekUndo(): Future[Option[Undo]] = { ??? }

  def execUndo(undoId: Int): Future[Either[String, UndoResult]] = { ??? }

  def replConfig(): Future[ReplConfig] = { ??? }

  def symbolDesignations(f: File, start: Int, end: Int, requestedTypes: List[SourceSymbol]): Future[SymbolDesignations] = {
    val pResponseMessage = sendRequest(SymbolDesignationsReq(f, start, end, requestedTypes))

    /// TODO: Add timeout exception! and failure...!!!
    val pResponseObject = Promise[SymbolDesignations]

    pResponseMessage.future.onComplete {
      case msg if msg.isSuccess && responseExtractor.isSuccessfullResponse(msg.get) => {
        pResponseObject.success(responseExtractor.getSymbolDesignations(msg.get))

      }
      case _ => {
        /// TODO: Include message information? 
        throw new Exception("Unsuccessful response. ")
      }

    }
    pResponseObject.future
  }

  def patchSource(f: File, edits: List[PatchOp]): Unit = { ??? }

  def typecheckFile(fileInfo: SourceFileInfo): Unit = {
    /// TODO: Not working
    //sendRequestNoResponse(TypecheckFileReq(fileInfo))
    ???
  }
  def typecheckFiles(fs: List[File]): Unit = { ??? }
  def removeFile(f: File): Unit = { ??? }
  def unloadAll(): Unit = { ??? }
  def typecheckAll(): Unit = {
    sendRequestNoResponse(TypecheckAllReq)
  }
  def completionsAtPoint(fileInfo: SourceFileInfo, point: Int, maxResults: Int, caseSens: Boolean, reload: Boolean): Future[CompletionInfoList] = { ??? }
  def packageMemberCompletion(path: String, prefix: String): Future[List[CompletionInfo]] = { ??? }

  def inspectTypeAtPoint(fileName: File, range: OffsetRange): Future[Option[TypeInspectInfo]] = { ??? }

  def inspectTypeById(typeId: Int): Future[Option[TypeInspectInfo]] = { ??? }

  def inspectTypeByName(typeFQN: String): Future[Option[TypeInspectInfo]] = { ??? }

  def symbolAtPoint(fileName: File, point: Int): Future[Option[SymbolInfo]] = { ??? }

  def symbolByName(fullyQualifiedName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[SymbolInfo]] = { ??? }
  def typeById(id: Int): Future[Option[TypeInfo]] = { ??? }
  def typeByName(name: String): Future[Option[TypeInfo]] = { ??? }
  def typeByNameAtPoint(name: String, f: File, range: OffsetRange): Future[Option[TypeInfo]] = { ??? }
  def callCompletion(id: Int): Future[Option[CallCompletionInfo]] = { ??? }
  def importSuggestions(f: File, point: Int, names: List[String], maxResults: Int): Future[ImportSuggestions] = { ??? }
  def docSignatureAtPoint(f: File, point: OffsetRange): Future[Option[DocSigPair]] = { ??? }
  def docSignatureForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[DocSigPair]] = { ??? }
  def docUriAtPoint(f: File, point: OffsetRange): Future[Option[String]] = { ??? }
  def docUriForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[String]] = { ??? }
  def publicSymbolSearch(names: List[String], maxResults: Int): Future[SymbolSearchResults] = { ??? }
  def usesOfSymAtPoint(f: File, point: Int): Future[List[ERangePosition]] = { ??? }

  def typeAtPoint(f: File, range: OffsetRange): Future[Option[TypeInfo]] = {

    val pResponseMessage = sendRequest(TypeAtPointReq(f, range))

    /// TODO: Add timeout exception! and failure...!!!
    val pResponseObject = Promise[Option[TypeInfo]]

    pResponseMessage.future.onComplete {
      case msg if msg.isSuccess && responseExtractor.isSuccessfullResponse(msg.get) => {
        pResponseObject.success(responseExtractor.getTypeAtPoint(msg.get))
      }
      case _ => {
        /// TODO: Include message information? 
        throw new Exception("Unsuccessful response. ")
      }

    }
    pResponseObject.future

  }
  def inspectPackageByPath(path: String): Future[Option[PackageInfo]] = { ??? }

  def prepareRefactor(procId: Int, refactorDesc: RefactorDesc): Future[Either[RefactorFailure, RefactorEffect]] = { ??? }
  def execRefactor(procId: Int, refactorType: RefactorType): Future[Either[RefactorFailure, RefactorResult]] = { ??? }
  def cancelRefactor(procId: Int): Unit = { ??? }

  def expandSelection(filename: File, start: Int, stop: Int): Future[FileRange] = { ??? }
  def formatFiles(filenames: List[File]): Unit = { ??? }
  def formatFile(fileInfo: SourceFileInfo): Future[String] = { ??? }

  def debugStartVM(commandLine: String): Future[DebugVmStatus] = { ??? }
  def debugAttachVM(hostname: String, port: String): Future[DebugVmStatus] = { ??? }
  def debugStopVM(): Future[Boolean] = { ??? }
  def debugRun(): Future[Boolean] = { ??? }
  def debugContinue(threadId: DebugThreadId): Future[Boolean] = { ??? }
  def debugSetBreakpoint(file: File, line: Int): Unit = { ??? }
  def debugClearBreakpoint(file: File, line: Int): Unit = { ??? }
  def debugClearAllBreakpoints(): Unit = { ??? }
  def debugListBreakpoints(): Future[BreakpointList] = { ??? }
  def debugNext(threadId: DebugThreadId): Future[Boolean] = { ??? }
  def debugStep(threadId: DebugThreadId): Future[Boolean] = { ??? }
  def debugStepOut(threadId: DebugThreadId): Future[Boolean] = { ??? }
  def debugLocateName(threadId: DebugThreadId, name: String): Future[Option[DebugLocation]] = { ??? }
  def debugValue(loc: DebugLocation): Future[Option[DebugValue]] = { ??? }
  def debugToString(threadId: DebugThreadId, loc: DebugLocation): Future[Option[String]] = { ??? }
  def debugSetValue(loc: DebugLocation, newValue: String): Future[Boolean] = { ??? }
  def debugBacktrace(threadId: DebugThreadId, index: Int, count: Int): Future[DebugBacktrace] = { ??? }
  def debugActiveVM(): Future[Boolean] = { ??? }

  // ===========================================================================  
  // EnsimeAsyncApi implementation - END
  // ===========================================================================

}