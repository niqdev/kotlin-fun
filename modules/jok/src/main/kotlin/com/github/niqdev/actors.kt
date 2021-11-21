package com.github.niqdev

interface Actor<T> {
  val context: ActorContext<T>
  fun self(): MyResult<Actor<T>> = MyResult(this)
  // messages shouldn't be sent to actors, but to actor references i.e. proxies, or some other substitute to transparently send messages to remote actors
  fun tell(message: T, sender: MyResult<Actor<T>> = self())
  fun tell(message: T, sender: Actor<T>) = tell(message, MyResult(sender))
  fun shutdown()

  companion object {
    fun <T> noSender(): MyResult<Actor<T>> = MyResult()
  }
}

interface ActorContext<T> {
  fun behavior(): MessageProcessor<T>
  // allows an actor to change its behavior, meaning the way it processes messages
  // During the life of the application, the behavior of each actor is allowed to change.
  // Generally this change of behavior is caused by a modification to the state of the actor, replacing the original behavior with a new one.
  fun become(behavior: MessageProcessor<T>)
}

interface MessageProcessor<T> {
  fun process(message: T, sender: MyResult<Actor<T>>)
}

abstract class AbstractActor<T>(protected val id: String) : Actor<T> {

  // java.util.concurrent.Executors.newSingleThreadExecutor(DaemonThreadFactory()) ???
  // - use a daemon thread factory to allow automatic shutdown when the main thread terminates
  // - creates daemon threads so that actors don't prevent the application from stopping when the main thread stops
  private val executor: java.util.concurrent.ExecutorService =
    java.util.concurrent.Executors.newSingleThreadExecutor { threadFactory ->
      val thread = Thread(threadFactory)
      thread.isDaemon = true
      thread
    }

  override val context: ActorContext<T> =
    // initial context
    object : ActorContext<T> {

      private var behavior: MessageProcessor<T> =
        object : MessageProcessor<T> {
          // delegates default behaviour to `onReceive`
          override fun process(message: T, sender: MyResult<Actor<T>>) =
            onReceive(message, sender)
        }

      override fun behavior(): MessageProcessor<T> = behavior

      @Synchronized
      override fun become(behavior: MessageProcessor<T>) {
        // mutates and register new behavior
        this.behavior = behavior
      }
    }

  // implements business logics
  abstract fun onReceive(message: T, sender: MyResult<Actor<T>>)

  // process one message at the time
  @Synchronized
  override fun tell(message: T, sender: MyResult<Actor<T>>) =
    executor.execute {
      try {
        // when a message is received, it's processed by the current behavior returned by the actor context
        context.behavior().process(message, sender)
      } catch (e: java.util.concurrent.RejectedExecutionException) {
        // this is probably normal and means all pending tasks were canceled because the actor was stopped
      } catch (e: Exception) {
        // TODO Either ???
        throw java.lang.RuntimeException(e)
      }
    }

  override fun shutdown() {
    executor.shutdown()
  }
}

// ------------------------------

private class Player(id: String, private val referee: Actor<Int>) : AbstractActor<Int>(id) {
  override fun onReceive(message: Int, sender: MyResult<Actor<Int>>) {
    println("[$id] message: $message")
    if (message >= 10) {
      // game is over
      referee.tell(message, sender)
    } else {
      when (sender) {
        // send message to the other player
        is MyResult.Success -> sender.value.tell(message + 1, self())
        // if the other player isn't present end the game
        else -> referee.tell(message, sender)
      }
    }
  }
}

// PING-PONG hello world
object PingPong {
  fun run() {
    // without this, the main application thread terminates as soon as the game is started
    val semaphore = java.util.concurrent.Semaphore(1)

    // translates to `arbitro`
    val referee = object : AbstractActor<Int>("Referee") {
      override fun onReceive(message: Int, sender: MyResult<Actor<Int>>) {
        println("Game ended after $message shots")
        // allows the main thread to resume
        semaphore.release()
      }
    }

    val playerPing = Player("PING", referee)
    val playerPong = Player("PONG", referee)

    // the single available permit is acquired by the current thread, and the game is started
    semaphore.acquire()

    playerPing.tell(1, MyResult(playerPong))

    // the main thread tries to acquire a new permit: as none are available, it blocks until the semaphore is released
    semaphore.acquire()
    // after `release()` is invoked, when resuming, the main thread terminates: all actor threads are daemons, so they also stop automatically
  }
}

// ------------------------------

// TODO more examples
// https://github.com/pysaumont/fpinkotlin/tree/master/fpinkotlin-parent/fpinkotlin-actors/src/main/kotlin/com/fpinkotlin/actors

fun main() {
  PingPong.run()
}
