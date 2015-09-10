package org.ensime.client

import scala.concurrent.Future
import org.ensime.api.ConnectionInfo
import org.ensime.api.SourceSymbol
import org.ensime.api.SymbolDesignations
import org.ensime.api.OffsetRange
import java.io.File
import org.ensime.api.TypeInspectInfo
import org.ensime.api.SymbolInfo
import org.ensime.api.TypeInfo
import org.ensime.api.PackageInfo
import org.ensime.api.ERangePosition
import org.ensime.api.ERangePositions
import org.ensime.api.ImplicitInfos

/**
 * Contains a selection of rpc requests offered by an ensime-server.
 */
trait EnsimeAsyncApi {

  /**
   * Sends a ConnectionInfoReq to an ensime-server.
   */
  def connectionInfo(): Future[ConnectionInfo]

  /**
   * Sends a SymbolDesignationsReq to the ensime-server.
   * @param   file            File
   * @param   start           Start offset range.
   * @param   end             End offset range.
   * @param   requestedTypes  List of SourceSymbol
   * @return                  Future[SymbolDesignations]
   */
  def symbolDesignations(f: File, start: Int, end: Int, requestedTypes: List[SourceSymbol]): Future[SymbolDesignations]

  /**
   * Sends a InspectTypeAtPointReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range to inspect.
   * @return        Future[TypeInspectInfo]
   */
  def inspectTypeAtPoint(file: File, range: OffsetRange): Future[TypeInspectInfo]

  /**
   * Sends a SymbolAtPointReq to the ensime-server.
   * @param   file  File
   * @param   point Offset of symbol.
   * @return        Future[SymbolInfo]
   */
  def symbolAtPoint(file: File, point: Int): Future[SymbolInfo]

  /**
   * Sends a UsesOfSymbolAtPointReq to the ensime-server.
   * @param   file  File
   * @param   point Offset of symbol.
   * @return        Future[ERangePositions]
   */
  def usesOfSymAtPoint(file: File, point: Int): Future[ERangePositions]

  /**
   * Sends a TypeAtPointReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range.
   * @return        Future[TypeInfo]
   */
  def typeAtPoint(file: File, range: OffsetRange): Future[TypeInfo]

  /**
   * Sends a InspectPackageByPathReq to the ensime-server.
   * @param   path  Package path like org.ensime.server
   * @return        Future[PackageInfo]
   */
  def inspectPackageByPath(path: String): Future[PackageInfo]

  /**
   * Sends a ImplicitInfoReq to the ensime-server.
   * @param   file  File
   * @param   range Offset range.
   * @return        Future[ImplicitInfos]
   */
  def implicitInfoReq(file: File, range: OffsetRange): Future[ImplicitInfos]

  // Rest of the rpc requests offered in older ensime version.

  //  def shutdownServer(): Unit
  //  def subscribeAsync(handler: EnsimeEvent => Unit): Future[Boolean]
  //  def peekUndo(): Future[Option[Undo]]
  //  def execUndo(undoId: Int): Future[Either[String, UndoResult]]
  //  def replConfig(): Future[ReplConfig]
  //  def patchSource(f: File, edits: List[PatchOp]): Unit
  //  def typecheckFile(fileInfo: SourceFileInfo): Unit
  //  def typecheckFiles(fs: List[File]): Unit
  //  def removeFile(f: File): Unit
  //  def unloadAll(): Unit
  //  def typecheckAll(): Unit
  //  def completionsAtPoint(fileInfo: SourceFileInfo, point: Int, maxResults: Int, caseSens: Boolean, reload: Boolean): Future[CompletionInfoList]
  //  def packageMemberCompletion(path: String, prefix: String): Future[List[CompletionInfo]]
  //  def inspectTypeById(typeId: Int): Future[Option[TypeInspectInfo]]
  //  def inspectTypeByName(typeFQN: String): Future[Option[TypeInspectInfo]]
  //  def symbolByName(fullyQualifiedName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[SymbolInfo]]
  //  def typeById(id: Int): Future[Option[TypeInfo]]
  //  def typeByName(name: String): Future[Option[TypeInfo]]
  //  def typeByNameAtPoint(name: String, f: File, range: OffsetRange): Future[Option[TypeInfo]]
  //  def callCompletion(id: Int): Future[Option[CallCompletionInfo]]
  //  def importSuggestions(f: File, point: Int, names: List[String], maxResults: Int): Future[ImportSuggestions]
  //  def docSignatureAtPoint(f: File, point: OffsetRange): Future[Option[DocSigPair]]
  //  def docSignatureForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[DocSigPair]]
  //  def docUriAtPoint(f: File, point: OffsetRange): Future[Option[String]]
  //  def docUriForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Future[Option[String]]
  //  def publicSymbolSearch(names: List[String], maxResults: Int): Future[SymbolSearchResults]
  //  def prepareRefactor(procId: Int, refactorDesc: RefactorDesc): Future[Either[RefactorFailure, RefactorEffect]]
  //  def execRefactor(procId: Int, refactorType: RefactorType): Future[Either[RefactorFailure, RefactorResult]]
  //  def cancelRefactor(procId: Int): Unit
  //  def expandSelection(filename: File, start: Int, stop: Int): Future[FileRange]
  //  def formatFiles(filenames: List[File]): Unit
  //  def formatFile(fileInfo: SourceFileInfo): Future[String]
  //  def debugStartVM(commandLine: String): Future[DebugVmStatus]
  //  def debugAttachVM(hostname: String, port: String): Future[DebugVmStatus]
  //  def debugStopVM(): Future[Boolean]
  //  def debugRun(): Future[Boolean]
  //  def debugContinue(threadId: DebugThreadId): Future[Boolean]
  //  def debugSetBreakpoint(file: File, line: Int): Unit
  //  def debugClearBreakpoint(file: File, line: Int): Unit
  //  def debugClearAllBreakpoints(): Unit
  //  def debugListBreakpoints(): Future[BreakpointList]
  //  def debugNext(threadId: DebugThreadId): Future[Boolean]
  //  def debugStep(threadId: DebugThreadId): Future[Boolean]
  //  def debugStepOut(threadId: DebugThreadId): Future[Boolean]
  //  def debugLocateName(threadId: DebugThreadId, name: String): Future[Option[DebugLocation]]
  //  def debugValue(loc: DebugLocation): Future[Option[DebugValue]]
  //  def debugToString(threadId: DebugThreadId, loc: DebugLocation): Future[Option[String]]
  //  def debugSetValue(loc: DebugLocation, newValue: String): Future[Boolean]
  //  def debugBacktrace(threadId: DebugThreadId, index: Int, count: Int): Future[DebugBacktrace]
  //  def debugActiveVM(): Future[Boolean]

}
