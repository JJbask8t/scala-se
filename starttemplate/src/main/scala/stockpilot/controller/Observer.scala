package stockpilot.controller

//Observer interface for views
//Views implement this to get notified about changes

//interface -> view
trait Observer {
  def update(): Unit
}

//Basic Observable implementation, used by Controller
// (Controller extends Observable & call notifyObservers() by canges in Modek)
trait Observable {

  private var observers: List[Observer] = Nil

  def addObserver(o: Observer): Unit =
    observers = o :: observers

  def removeObserver(o: Observer): Unit =
    observers = observers.filterNot(_ == o)

  // Notify all registered observers
  protected def notifyObservers(): Unit =
    observers.foreach(_.update())
}
