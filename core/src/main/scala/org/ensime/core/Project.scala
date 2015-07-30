package org.ensime.core

import akka.actor._
import akka.event.LoggingReceive
import org.apache.commons.vfs2.FileObject
import org.ensime.api._
import org.ensime.indexer._

import scala.concurrent.duration._
import scala.util.Try

/**
 * The Project actor simply forwards messages coming from the user to
 * the respective subcomponent.
 */
class Project(
    broadcaster: ActorRef,
    implicit val config: EnsimeConfig
) extends Actor with ActorLogging with Stash {
  import context.{ dispatcher, system }

  /* The main components of the ENSIME server */
  private var analyzer: ActorRef = _
  private var debugger: ActorRef = _

  // TODO consolidate search/indexer
  private var indexer: ActorRef = _

  // TODO: use state transitions to manage this state
  // vfs, resolver, search and watchers are considered "reliable" (hah!)
  // TODO: Actor-ise as many of these vals as possible
  private implicit val vfs: EnsimeVFS = EnsimeVFS()
  private val resolver = new SourceResolver(config)
  private val searchService = new SearchService(config, resolver)
  searchService.refresh().onSuccess {
    case (deletes, inserts) =>
      // legacy clients expect to see IndexerReady on connection.
      // we could also just blindly send this on each connection.
      broadcaster ! Broadcaster.Persist(IndexerReadyEvent)
      log.debug(s"indexed $inserts and removed $deletes")
  }(context.dispatcher)

  private val sourceWatcher = new SourceWatcher(config, resolver :: Nil)
  private val reTypecheck = new ClassfileListener {
    def reTypeCheck(): Unit = self ! AskReTypecheck
    def classfileAdded(f: FileObject): Unit = reTypeCheck()
    def classfileChanged(f: FileObject): Unit = reTypeCheck()
    def classfileRemoved(f: FileObject): Unit = reTypeCheck()
  }
  private val classfileWatcher = new ClassfileWatcher(config, searchService :: reTypecheck :: Nil)

  override def preStart(): Unit = {
    indexer = context.actorOf(Indexer(searchService), "indexer")
    analyzer = context.actorOf(Analyzer(broadcaster, indexer, searchService), "analyzer")
    debugger = context.actorOf(DebugManager(broadcaster), "debugging")
  }

  override def postStop(): Unit = {
    // make sure the "reliable" dependencies are cleaned up
    Try(classfileWatcher.shutdown())
    Try(sourceWatcher.shutdown())
    Try(searchService.shutdown())
    Try(vfs.close())
  }

  // debounces ReloadExistingFilesEvent
  private var rechecking: Cancellable = _

  def receive: Receive =
    filesChanging orElse LoggingReceive { respondingToQueries }

  def filesChanging: Receive = {
    case AskReTypecheck =>
      Option(rechecking).foreach(_.cancel())
      rechecking = system.scheduler.scheduleOnce(
        5 seconds, analyzer, ReloadExistingFilesEvent
      )
  }

  def respondingToQueries: Receive = {
    case ConnectionInfoReq => sender() ! ConnectionInfo()

    case m: RpcAnalyserRequest => analyzer forward m
    case m: RpcDebuggerRequest => debugger forward m
    case m: RpcSearchRequest => indexer forward m
  }

  /*
  Funky non-standard cases:

   - SymbolByNameReq (option)

   - PrepareRefactorReq (complex)
   - ExecRefactorReq (either)

   - DebugLocateNameReq (option)
   - DebugValueReq (option)
   - DebugToStringReq (option)

   */

}
object Project {
  def apply(target: ActorRef)(implicit config: EnsimeConfig): Props =
    Props(classOf[Project], target, config)
}
