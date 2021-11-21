// In the actor model, a multithreaded application is divided into single-threaded components, called actors.
// If each actor is single-threaded, it doesn't need to share data using locks or synchronization.
// Actors communicate with other actors by way of effects, as if such communication were the I/O of messages.
// This means that actors rely on a mechanism for serializing (i.e. handling one message after the other) the messages they receive.
// Actors can process messages one at a time without having to bother about concurrent access to their internal resources.
// An actor system can be seen as a series of functional programs communicating with each other through effects.
// Actors can send messages to other actors. Messages are sent asynchronously, which means an actor doesn't need to wait for an answer — there isn't one
// As soon as a message is sent, the sender can continue its job, which mostly consists of processing one at a time a queue of messages it receives

// Actors are useful when multiple threads are supposed to share some mutable state,
// as when a thread produces the result of a computation,
// and this result must be passed to another thread for further processing.
