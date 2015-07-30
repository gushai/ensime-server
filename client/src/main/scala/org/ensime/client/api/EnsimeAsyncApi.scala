//package org.ensime
//
//import java.io.File
//import org.ensime.core._
//import org.ensime.model._
//import org.ensime.server.ConnectionInfo
//import org.ensime.util.{ FileRange, RefactorType }
//import scala.concurrent.Future
//
//trait EnsimeAsyncApi {
//
//  def connectionInfo(): Future[ConnectionInfo]
//
//  /**
//   * Shutdown the server instance
//   * N.b. This will kill the server - should be moved out.
//   */
//  def shutdownServer(): Unit
//
//  /**
//   * Subscribe to async events from the project, replaying previously seen events if requested.
//   * The first subscriber will get all undelivered events (subsequent subscribers do not).
//   * @param handler The callback handler for events
//   * @return True if caller is first subscriber, False otherwise
//   */
//  def subscribeAsync(handler: EnsimeEvent => Unit): Future[Boolean]
//
//  /**
//   * Return the details of the latest Undo operation on the undo stack.
//   * @return The latest Undo information (if it exists) or None
//   */
//  def peekUndo(): Future[Option[Undo]]
//
//  def execUndo(undoId: Int): Future[Either[String, UndoResult]]
//
//  def replConfig(): Future[ReplConfig]
//
//  /**
//   *   Request the semantic classes of symbols in the given range. These classes are intended to be used for
//   *   semantic highlighting.
//   * Arguments:
//   *   f source filename
//   *   start The character offset of the start of the input range.
//   *   End  The character offset of the end of the input range.
//   *   requestedTypes The semantic classes in which we are interested. (@see SourceSymbol)
//   * Return:
//   *   SymbolDesignations The given
//   */
//  def symbolDesignations(f: File, start: Int, end: Int, requestedTypes: List[SourceSymbol]): Future[SymbolDesignations]
//
//  /**
//   *   Patch the source with the given changes.
//   *   @param f The file to patch
//   *   @param edits The patches to apply to the file.
//   */
//  def patchSource(f: File, edits: List[PatchOp]): Unit
//
//  def typecheckFile(fileInfo: SourceFileInfo): Unit
//  def typecheckFiles(fs: List[File]): Unit
//  def removeFile(f: File): Unit
//  def unloadAll(): Unit
//  def typecheckAll(): Unit
//  def completionsAtPoint(fileInfo: SourceFileInfo, point: Int, maxResults: Int, caseSens: Boolean, reload: Boolean): Future[CompletionInfoList]
//  def packageMemberCompletion(path: String, prefix: String): Future[List[CompletionInfo]]
//
//  /**
//   * Return detailed type information about the item at the given file position.
//   * @param fileName The source filename
//   * @param range The range in the file to inspect.
//   * @return Some(TypeInspectInfo) if the range represents a valid type, None otherwise
//   */
//  def inspectTypeAtPoint(fileName: File, range: OffsetRange): Future[Option[TypeInspectInfo]]
//
//  /**
//   * Lookup detailed type description by typeId
//   * @param typeId The id of the type to inspect (returned by other calls)
//   * @return Some(TypeInspectInfo) if the typeId represents a valid type, None otherwise
//   */
//  def inspectTypeById(typeId: Int): Future[Option[TypeInspectInfo]]
//
//  /**
//   * Lookup detailed type description by fully qualified class name
//   * @param typeFQN The fully qualified type name to inspect
//   * @return Some(TypeInspectInfo) if typeFQN represents a valid type, None otherwise
//   */
//  def inspectTypeByName(typeFQN: String): Future[Option[TypeInspectInfo]]
//
//  def symbolAtPoint(fileName: File, point: Int): Future[Option[SymbolInfo]]
//
//  /**
//   * Lookup a detailed symbol description.
//   * @param fullyQualifiedName The fully qualified name of a type, object or package.
//   * @param memberName The short name of a member symbol of the qualified symbol.
//   * @return signatureString An optional signature to disambiguate overloaded methods.
//   */
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
//  def usesOfSymAtPoint(f: File, point: Int): Future[List[ERangePosition]]
//  def typeAtPoint(f: File, range: OffsetRange): Future[Option[TypeInfo]]
//  def inspectPackageByPath(path: String): Future[Option[PackageInfo]]
//
//  def prepareRefactor(procId: Int, refactorDesc: RefactorDesc): Future[Either[RefactorFailure, RefactorEffect]]
//  def execRefactor(procId: Int, refactorType: RefactorType): Future[Either[RefactorFailure, RefactorResult]]
//  def cancelRefactor(procId: Int): Unit
//
//  def expandSelection(filename: File, start: Int, stop: Int): Future[FileRange]
//  def formatFiles(filenames: List[File]): Unit
//  def formatFile(fileInfo: SourceFileInfo): Future[String]
//
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
//}
//
